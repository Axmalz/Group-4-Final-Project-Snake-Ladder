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

class Snake {
    private final int headPosition;
    private final int tailPosition;

    public Snake(int h, int t) {
        this.headPosition = h;
        this.tailPosition = t;
    }

    public int getHeadPosition() {
        return this.headPosition;
    }

    public int getTailPosition() {
        return this.tailPosition;
    }
}
