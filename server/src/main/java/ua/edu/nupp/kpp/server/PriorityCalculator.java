package ua.edu.nupp.kpp.server;

public class PriorityCalculator {
    /**
     * Обчислення фактора F3 — дефіцит доступних одиниць (нормування 0..100).
     * Якщо availableQty <= 0 або requestedQty >= availableQty -> дефіцит 100%.
     * Інакше (requestedQty / availableQty) * 100.
     */
    public static double factor3(int requestedQty, int availableQty) {
        if (requestedQty <= 0 || availableQty < 0) {
            throw new IllegalArgumentException("Некоректна кількість одиниць для розрахунку F3");
        }
        if (availableQty == 0 || requestedQty >= availableQty) {
            return 100.0;
        }
        double ratio = ((double) requestedQty / availableQty) * 100.0;
        return Math.round(Math.max(0.0, Math.min(100.0, ratio)) * 100.0) / 100.0;
    }

    /**
     * Інтегральний показник пріоритету видачі:
     * I = 0.40 * F1 + 0.35 * F2 + 0.25 * F3
     * F1 - терміновість робіт (0..100)
     * F2 - критичність майна (0..100)
     * F3 - дефіцит доступних одиниць (0..100)
     */
    public static double calculate(double f1, double f2, double f3) {
        checkRange(f1, "F1 (терміновість робіт)");
        checkRange(f2, "F2 (критичність майна)");
        checkRange(f3, "F3 (дефіцит доступних одиниць)");
        return Math.round((0.40 * f1 + 0.35 * f2 + 0.25 * f3) * 100.0) / 100.0;
    }

    private static void checkRange(double value, String name) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Фактор " + name + " має бути в діапазоні 0..100");
        }
    }
}
