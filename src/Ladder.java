/**
 * -----------------------------------------------------
 * ES234211 - Programming Fundamental
 * Genap - 2023/2024
 * Group Capstone Project: Snake and Ladder Game
 * -----------------------------------------------------
 * Class    : D
 * Group    : 4
 * Members  :
 * 1. 5026221162 - Raphael Andhika Pratama
 * 2. 5026221213 - Muhammad Rafa Akbar
 * 3. 5026231128 - Fadhiil Akmal Hamizan
 * ------------------------------------------------------
 */

class Ladder {
    private final int topPosition;
    private final int bottomPosition;

    public Ladder(int b, int t) {
        this.topPosition = t;
        this.bottomPosition = b;
    }

    public int getTopPosition() {
        return this.topPosition;
    }

    public int getBottomPosition() {
        return this.bottomPosition;
    }
}
