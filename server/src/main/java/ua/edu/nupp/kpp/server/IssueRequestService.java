package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IssueRequestService {
    private static final Logger LOG = Logger.getLogger(IssueRequestService.class.getName());
    private final IssueRequestDAO dao = new IssueRequestDAO();
    private final AssetDAO assetDAO = new AssetDAO();
    private final AppUserDAO userDAO = new AppUserDAO();

    public List<IssueRequest> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(long assetId, long userId, int requestedQty, double urgencyScore) throws SQLException {
        IssueRequest req = buildRequest(null, assetId, userId, requestedQty, urgencyScore, RequestState.СТВОРЕНО);
        dao.add(req);
        LOG.info("Створено заявку на видачу майна ID=" + assetId + " для користувача ID=" + userId);
    }

    public void update(long id, long assetId, long userId, int requestedQty, double urgencyScore) throws SQLException {
        IssueRequest existing = dao.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }
        if (existing.getState() == RequestState.ВИДАНО || existing.getState() == RequestState.ПОВЕРНУТО) {
            throw new IllegalArgumentException("Редагувати можна лише заявки у стані СТВОРЕНО або СХВАЛЕНО");
        }
        IssueRequest updated = buildRequest(id, assetId, userId, requestedQty, urgencyScore, existing.getState());
        dao.update(updated);
        LOG.info("Оновлено заявку ID=" + id);
    }

    public void changeState(long id, RequestState nextState) throws SQLException {
        if (nextState == null) {
            throw new IllegalArgumentException("Не вказано новий стан заявки");
        }
        IssueRequest current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }
        if (!isAllowedTransition(current.getState(), nextState)) {
            LOG.warning("Спроба недопустимого переходу стану заявки ID=" + id + ": " + current.getState() + " -> " + nextState);
            throw new IllegalArgumentException("Недопустимий перехід стану: " + current.getState() + " -> " + nextState);
        }

        Asset asset = assetDAO.getById(current.getAssetId());
        if (asset == null) {
            throw new IllegalArgumentException("Пов'язане майно не знайдено");
        }

        if (nextState == RequestState.ВИДАНО) {
            if (asset.getAvailableQty() < current.getRequestedQty()) {
                throw new IllegalArgumentException("Недостатньо доступних одиниць майна на складі для видачі");
            }
            assetDAO.updateAvailableQty(asset.getId(), asset.getAvailableQty() - current.getRequestedQty());
        } else if (nextState == RequestState.ПОВЕРНУТО) {
            assetDAO.updateAvailableQty(asset.getId(), asset.getAvailableQty() + current.getRequestedQty());
        }

        dao.updateState(id, nextState);
        LOG.info("Змінено стан заявки ID=" + id + " на " + nextState);
    }

    public void delete(long id) throws SQLException {
        IssueRequest existing = dao.getById(id);
        if (existing != null && existing.getState() == RequestState.ВИДАНО) {
            throw new IllegalArgumentException("Заявку у стані ВИДАНО не можна видалити: спочатку оформіть повернення майна");
        }
        dao.delete(id);
        LOG.info("Видалено заявку ID=" + id);
    }

    public static boolean isAllowedTransition(RequestState from, RequestState to) {
        return (from == RequestState.СТВОРЕНО && to == RequestState.СХВАЛЕНО)
            || (from == RequestState.СХВАЛЕНО && to == RequestState.ВИДАНО)
            || (from == RequestState.ВИДАНО && to == RequestState.ПОВЕРНУТО);
    }

    static void validateInput(long assetId, long userId, int requestedQty, double urgencyScore) {
        if (assetId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор майна або користувача");
        }
        if (requestedQty <= 0) {
            throw new IllegalArgumentException("Запитувана кількість має бути більшою за 0");
        }
        if (urgencyScore < 0 || urgencyScore > 100) {
            throw new IllegalArgumentException("Терміновість робіт (F1) має бути в діапазоні 0..100");
        }
    }

    private IssueRequest buildRequest(Long id, long assetId, long userId, int requestedQty, double urgencyScore, RequestState state) throws SQLException {
        validateInput(assetId, userId, requestedQty, urgencyScore);
        Asset asset = assetDAO.getById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Майно із зазначеним ID не знайдено");
        }
        AppUser user = userDAO.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Користувача із зазначеним ID не знайдено");
        }
        if (asset.getAvailableQty() < requestedQty) {
            throw new IllegalArgumentException("Запитувана кількість перевищує доступний залишок (" + asset.getAvailableQty() + " од.)");
        }

        double f1 = urgencyScore;
        double f2 = asset.getCriticalityScore();
        double f3 = PriorityCalculator.factor3(requestedQty, asset.getAvailableQty());
        double priorityIndex = PriorityCalculator.calculate(f1, f2, f3);

        return new IssueRequest(id, assetId, userId, requestedQty, urgencyScore, state, f1, f2, f3, priorityIndex);
    }
}
