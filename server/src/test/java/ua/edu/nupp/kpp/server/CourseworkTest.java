package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    @Test
    void factor3PartialDeficitCalculation() {
        assertEquals(50.0, PriorityCalculator.factor3(1, 2));
    }

    @Test
    void factor3FullDeficitWhenRequestedEqualsAvailable() {
        assertEquals(100.0, PriorityCalculator.factor3(2, 2));
    }

    @Test
    void factor3RejectsInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.factor3(0, 5));
    }

    @Test
    void priorityIndexCalculationMatchesFormula() {
        // I = 0.40 * 75 + 0.35 * 80 + 0.25 * 50 = 30.0 + 28.0 + 12.5 = 70.50
        assertEquals(70.50, PriorityCalculator.calculate(75.0, 80.0, 50.0));
    }

    @Test
    void priorityIndexRejectsFactorOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.calculate(105.0, 50.0, 50.0));
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.calculate(50.0, -1.0, 50.0));
    }

    @Test
    void lifecycleCreatedToApprovedAllowed() {
        assertTrue(IssueRequestService.isAllowedTransition(RequestState.СТВОРЕНО, RequestState.СХВАЛЕНО));
    }

    @Test
    void lifecycleApprovedToIssuedAllowed() {
        assertTrue(IssueRequestService.isAllowedTransition(RequestState.СХВАЛЕНО, RequestState.ВИДАНО));
    }

    @Test
    void lifecycleIssuedToReturnedAllowed() {
        assertTrue(IssueRequestService.isAllowedTransition(RequestState.ВИДАНО, RequestState.ПОВЕРНУТО));
    }

    @Test
    void lifecycleSkipStateRejected() {
        assertFalse(IssueRequestService.isAllowedTransition(RequestState.СТВОРЕНО, RequestState.ВИДАНО));
        assertFalse(IssueRequestService.isAllowedTransition(RequestState.СТВОРЕНО, RequestState.ПОВЕРНУТО));
        assertFalse(IssueRequestService.isAllowedTransition(RequestState.ПОВЕРНУТО, RequestState.СТВОРЕНО));
    }

    @Test
    void assetValidationRejectsBlankFieldsAndNegativeQty() {
        assertThrows(IllegalArgumentException.class, () ->
            AssetService.validate(new Asset(null, "", "Проєктор", "Мультимедіа", 2, 80.0))
        );
        assertThrows(IllegalArgumentException.class, () ->
            AssetService.validate(new Asset(null, "INV-1", "Проєктор", "Мультимедіа", -1, 80.0))
        );
    }

    @Test
    void userValidationRejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
            AppUserService.validate(new AppUser(null, "Іваненко І.І.", "КІТС", "Викладач", "invalid-email"))
        );
    }

    @Test
    void transferRecordValidationRejectsBadDateFormat() {
        assertThrows(IllegalArgumentException.class, () ->
            TransferRecordService.validate(new TransferRecord(null, 1L, "ВИДАЧА", "17.11.2026", "Справний"))
        );
    }
}
