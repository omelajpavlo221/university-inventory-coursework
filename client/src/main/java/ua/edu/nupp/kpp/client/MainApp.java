package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpInventoryClient api = new HttpInventoryClient();

    private final TableView<Asset> assetsTable = new TableView<>();
    private final TableView<AppUser> usersTable = new TableView<>();
    private final TableView<IssueRequest> requestsTable = new TableView<>();
    private final TableView<TransferRecord> transfersTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
            new Tab("Майно", assetPane()),
            new Tab("Користувачі", userPane()),
            new Tab("Заявки на видачу", requestPane()),
            new Tab("Записи передачі", transferPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        stage.setScene(new Scene(tabs, 1120, 680));
        stage.setTitle("Інформаційна система видачі та повернення університетського майна");
        stage.show();
        refreshAll();
    }

    private Pane assetPane() {
        assetsTable.getColumns().setAll(
            col("ID", "id"),
            col("Інв. номер", "invNumber"),
            col("Назва", "name"),
            col("Категорія", "category"),
            col("Доступно (шт.)", "availableQty"),
            col("Критичність (F2)", "criticalityScore")
        );

        TextField invNum = new TextField();
        TextField name = new TextField();
        TextField category = new TextField();
        TextField qty = new TextField();
        TextField crit = new TextField();

        ProgressBar progress = new ProgressBar(0);
        progress.setMaxWidth(Double.MAX_VALUE);
        Label status = new Label("CSV-імпорт не запущено");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importCsv = new Button("Імпортувати CSV (інвентар)");

        add.setOnAction(e -> safe(() -> {
            api.addAsset(new Asset(null, invNum.getText(), name.getText(), category.getText(),
                Integer.parseInt(qty.getText()), Double.parseDouble(crit.getText())));
            refreshAssets();
        }));

        update.setOnAction(e -> safe(() -> {
            Asset sel = selected(assetsTable);
            api.updateAsset(new Asset(sel.getId(), invNum.getText(), name.getText(), category.getText(),
                Integer.parseInt(qty.getText()), Double.parseDouble(crit.getText())));
            refreshAssets();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteAsset(selected(assetsTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshAssets());

        assetsTable.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                invNum.setText(s.getInvNumber());
                name.setText(s.getName());
                category.setText(s.getCategory());
                qty.setText(String.valueOf(s.getAvailableQty()));
                crit.setText(String.valueOf(s.getCriticalityScore()));
            }
        });

        importCsv.setOnAction(e -> {
            importCsv.setDisable(true);
            ImportInventoryTask task = new ImportInventoryTask(Path.of("sample-data", "inventory.csv"), api);
            progress.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(ev -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Готово. Успішно імпортовано: " + task.getValue());
                importCsv.setDisable(false);
                refreshAssets();
            });

            task.setOnFailed(ev -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Помилка імпорту CSV");
                importCsv.setDisable(false);
                showError(task.getException());
            });

            new Thread(task, "csv-inventory-import").start();
        });

        VBox form = new VBox(6,
            new Label("Інвентарний номер"), invNum,
            new Label("Назва майна"), name,
            new Label("Категорія"), category,
            new Label("Доступна кількість"), qty,
            new Label("Критичність F2 (0..100)"), crit,
            new HBox(8, add, update, delete, refresh),
            new Separator(),
            importCsv, progress, status
        );
        return layout(assetsTable, form);
    }

    private Pane userPane() {
        usersTable.getColumns().setAll(
            col("ID", "id"),
            col("ПІБ", "fullName"),
            col("Підрозділ / Кафедра", "department"),
            col("Посада / Роль", "role"),
            col("Email", "email")
        );

        TextField fullName = new TextField();
        TextField dept = new TextField();
        TextField role = new TextField();
        TextField email = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addUser(new AppUser(null, fullName.getText(), dept.getText(), role.getText(), email.getText()));
            refreshUsers();
        }));

        update.setOnAction(e -> safe(() -> {
            AppUser u = selected(usersTable);
            api.updateUser(new AppUser(u.getId(), fullName.getText(), dept.getText(), role.getText(), email.getText()));
            refreshUsers();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteUser(selected(usersTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshUsers());

        usersTable.getSelectionModel().selectedItemProperty().addListener((o, a, u) -> {
            if (u != null) {
                fullName.setText(u.getFullName());
                dept.setText(u.getDepartment());
                role.setText(u.getRole());
                email.setText(u.getEmail());
            }
        });

        VBox form = new VBox(8,
            new Label("ПІБ користувача"), fullName,
            new Label("Кафедра / Відділ"), dept,
            new Label("Роль"), role,
            new Label("Email"), email,
            new HBox(8, add, update, delete, refresh)
        );
        return layout(usersTable, form);
    }

    private Pane requestPane() {
        requestsTable.getColumns().setAll(
            col("ID", "id"),
            col("Asset ID", "assetId"),
            col("User ID", "userId"),
            col("К-сть", "requestedQty"),
            col("Стан", "state"),
            col("F1 (Терм.)", "f1"),
            col("F2 (Крит.)", "f2"),
            col("F3 (Деф.)", "f3"),
            col("Індекс I", "priorityIndex")
        );

        TextField assetId = new TextField();
        TextField userId = new TextField();
        TextField reqQty = new TextField();
        TextField urgency = new TextField();
        ComboBox<RequestState> stateBox = new ComboBox<>(FXCollections.observableArrayList(RequestState.values()));

        Button add = new Button("Створити");
        Button update = new Button("Перерахувати");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addRequest(
                Long.parseLong(assetId.getText()),
                Long.parseLong(userId.getText()),
                Integer.parseInt(reqQty.getText()),
                Double.parseDouble(urgency.getText())
            );
            refreshRequests();
        }));

        update.setOnAction(e -> safe(() -> {
            IssueRequest sel = selected(requestsTable);
            api.updateRequest(
                sel.getId(),
                Long.parseLong(assetId.getText()),
                Long.parseLong(userId.getText()),
                Integer.parseInt(reqQty.getText()),
                Double.parseDouble(urgency.getText())
            );
            refreshRequests();
        }));

        changeState.setOnAction(e -> safe(() -> {
            api.changeRequestState(selected(requestsTable).getId(), stateBox.getValue());
            refreshRequests();
            refreshAssets();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteRequest(selected(requestsTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshRequests());

        requestsTable.getSelectionModel().selectedItemProperty().addListener((o, a, r) -> {
            if (r != null) {
                assetId.setText(String.valueOf(r.getAssetId()));
                userId.setText(String.valueOf(r.getUserId()));
                reqQty.setText(String.valueOf(r.getRequestedQty()));
                urgency.setText(String.valueOf(r.getUrgencyScore()));
                stateBox.setValue(r.getState());
            }
        });

        VBox form = new VBox(6,
            new Label("ID майна (Asset ID)"), assetId,
            new Label("ID користувача (User ID)"), userId,
            new Label("Запитувана кількість"), reqQty,
            new Label("Терміновість робіт F1 (0..100)"), urgency,
            new Label("Новий стан життєвого циклу"), stateBox,
            new HBox(8, add, update, changeState),
            new HBox(8, delete, refresh)
        );
        return layout(requestsTable, form);
    }

    private Pane transferPane() {
        transfersTable.getColumns().setAll(
            col("ID", "id"),
            col("Request ID", "requestId"),
            col("Тип операції", "actionType"),
            col("Дата (YYYY-MM-DD)", "actionDate"),
            col("Примітка про стан майна", "conditionNote")
        );

        TextField requestId = new TextField();
        TextField actionType = new TextField();
        TextField actionDate = new TextField();
        TextField note = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addTransfer(new TransferRecord(null, Long.parseLong(requestId.getText()),
                actionType.getText(), actionDate.getText(), note.getText()));
            refreshTransfers();
        }));

        update.setOnAction(e -> safe(() -> {
            TransferRecord tr = selected(transfersTable);
            api.updateTransfer(new TransferRecord(tr.getId(), Long.parseLong(requestId.getText()),
                actionType.getText(), actionDate.getText(), note.getText()));
            refreshTransfers();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteTransfer(selected(transfersTable).getId());
            refreshTransfers();
        }));

        refresh.setOnAction(e -> refreshTransfers());

        transfersTable.getSelectionModel().selectedItemProperty().addListener((o, a, tr) -> {
            if (tr != null) {
                requestId.setText(String.valueOf(tr.getRequestId()));
                actionType.setText(tr.getActionType());
                actionDate.setText(tr.getActionDate());
                note.setText(tr.getConditionNote());
            }
        });

        VBox form = new VBox(8,
            new Label("ID заявки на видачу"), requestId,
            new Label("Тип операції (ВИДАЧА / ПЕРЕВІРКА КОМПЛЕКТНОСТІ / ПОВЕРНЕННЯ)"), actionType,
            new Label("Дата (YYYY-MM-DD)"), actionDate,
            new Label("Стан майна та примітки"), note,
            new HBox(8, add, update, delete, refresh)
        );
        return layout(transfersTable, form);
    }

    private void refreshAll() {
        refreshAssets();
        refreshUsers();
        refreshRequests();
        refreshTransfers();
    }

    private void refreshAssets() { safe(() -> assetsTable.setItems(FXCollections.observableArrayList(api.getAssets()))); }
    private void refreshUsers() { safe(() -> usersTable.setItems(FXCollections.observableArrayList(api.getUsers()))); }
    private void refreshRequests() { safe(() -> requestsTable.setItems(FXCollections.observableArrayList(api.getRequests()))); }
    private void refreshTransfers() { safe(() -> transfersTable.setItems(FXCollections.observableArrayList(api.getTransfers()))); }

    private <T> T selected(TableView<T> table) {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) throw new IllegalArgumentException("Виберіть запис у таблиці");
        return item;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (NumberFormatException nfe) {
            showError(new IllegalArgumentException("Перевірте правильність введення числових полів"));
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Невідома помилка" : e.getMessage(), ButtonType.OK);
        d.setHeaderText("Помилка виконання операції");
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.68);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
