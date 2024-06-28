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

class Player {
    private final String name;
    private int position;
    private boolean skipNextTurn;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.skipNextTurn = false;
    }

    public String getName() {
        return this.name;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSkipNextTurn() {
        return this.skipNextTurn;
    }

    public void setSkipNextTurn(boolean skipNextTurn) {
        this.skipNextTurn = skipNextTurn;
    }

    public int rollDice() {
        return (int) (Math.random() * 6 + 1);
    }

    public void moveAround(int steps, int boardSize) {
        this.position += steps;
        if (this.position > boardSize) {
            this.position = boardSize;
        }
    }
}
