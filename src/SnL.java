import java.util.ArrayList;
import java.util.Scanner;

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

class SnL {
    private final ArrayList<Player> players;
    private final ArrayList<Snake> snakes;
    private final ArrayList<Ladder> ladders;
    private final ArrayList<Integer> powerUps;
    private final ArrayList<Integer> penalties;
    private int boardSize;
    private int gameStatus;
    private int nowPlaying;

    public SnL() {
        this.players = new ArrayList<>();
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.penalties = new ArrayList<>();
        this.gameStatus = 0;
    }

    public void setupGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select difficulty (1. Easy, 2. Normal, 3. Hard): ");
        int difficulty = scanner.nextInt();

        if (difficulty == 1) {
            this.boardSize = 50;
            addLadders(new int[][]{{2, 23}, {8, 34}, {22, 36}, {32, 45}});
            addSnakes(new int[][]{{47, 5}, {29, 9}, {38, 15}, {41, 25}});
            addPowerUps(new int[]{10, 30});
            addPenalties(new int[]{20, 40});
        } else if (difficulty == 2) {
            this.boardSize = 100;
            addLadders(new int[][]{{2, 23}, {8, 34}, {22, 77}, {32, 68}, {41, 79}, {74, 88}, {82, 98}, {85, 95}});
            addSnakes(new int[][]{{47, 5}, {29, 9}, {38, 15}, {97, 25}, {53, 33}, {62, 37}, {86, 54}, {92, 70}});
            addPowerUps(new int[]{10, 30, 50, 70});
            addPenalties(new int[]{20, 40, 60, 80});
        } else if (difficulty == 3) {
            this.boardSize = 200;
            addLadders(new int[][]{{2, 23}, {8, 34}, {22, 77}, {32, 68}, {41, 79}, {74, 88}, {82, 98}, {85, 95}, {112, 154}, {136, 178}, {142, 188}, {164, 190}});
            addSnakes(new int[][]{{47, 5}, {29, 9}, {38, 15}, {97, 25}, {53, 33}, {62, 37}, {86, 54}, {92, 70}, {121, 103}, {135, 113}, {165, 121}, {175, 155}});
            addPowerUps(new int[]{10, 30, 50, 70, 90, 110, 130, 150});
            addPenalties(new int[]{20, 40, 60, 80, 100, 120, 140, 160});
        }

        System.out.println("Enter number of players (max 6): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numPlayers; i++) {
            System.out.println("Enter name for Player " + (i + 1) + ": ");
            String name = scanner.nextLine();
            players.add(new Player(name));
        }
    }

    private void addLadders(int[][] positions) {
        for (int[] pos : positions) {
            ladders.add(new Ladder(pos[0], pos[1]));
        }
    }

    private void addSnakes(int[][] positions) {
        for (int[] pos : positions) {
            snakes.add(new Snake(pos[0], pos[1]));
        }
    }

    private void addPowerUps(int[] positions) {
        for (int pos : positions) {
            powerUps.add(pos);
        }
    }

    private void addPenalties(int[] positions) {
        for (int pos : positions) {
            penalties.add(pos);
        }
    }

    public void play() {
        Player playerInTurn;
        Scanner read = new Scanner(System.in);

        do {
            playerInTurn = getWhoseTurn();

            if (playerInTurn.isSkipNextTurn()) {
                System.out.println(playerInTurn.getName() + " is skipping this turn due to a penalty.");
                playerInTurn.setSkipNextTurn(false);
                nowPlaying = (nowPlaying + 1) % players.size();
                continue;
            }

            System.out.println("Now Playing: " + playerInTurn.getName());
            System.out.println(playerInTurn.getName() + ", please press enter to roll the dice");
            read.nextLine();
            int diceRoll = playerInTurn.rollDice();
            System.out.println("Dice Number: " + diceRoll);

            movePlayer(playerInTurn, diceRoll);

            if (diceRoll == 6) {
                System.out.println(playerInTurn.getName() + " rolled a 6! Roll again.");
                read.nextLine();
                diceRoll = playerInTurn.rollDice();
                System.out.println("Dice Number: " + diceRoll);
                movePlayer(playerInTurn, diceRoll);
            }

            while (powerUps.contains(playerInTurn.getPosition())) {
                System.out.println(playerInTurn.getName() + " landed on a power-up at " + playerInTurn.getPosition() + "! Roll again.");
                read.nextLine();
                diceRoll = playerInTurn.rollDice();
                System.out.println("Dice Number: " + diceRoll);
                movePlayer(playerInTurn, diceRoll);
            }

            System.out.println("New Position: " + playerInTurn.getPosition());
            System.out.println("==============================================");

        } while (getGameStatus() != 2);

        System.out.println("The winner is: " + playerInTurn.getName());
    }

    private void movePlayer(Player player, int steps) {
        player.moveAround(steps, this.boardSize);

        for (Ladder ladder : ladders) {
            if (player.getPosition() == ladder.getBottomPosition()) {
                System.out.println(player.getName() + " climbed a ladder from " + ladder.getBottomPosition() + " to " + ladder.getTopPosition());
                player.setPosition(ladder.getTopPosition());
            }
        }

        for (Snake snake : snakes) {
            if (player.getPosition() == snake.getHeadPosition()) {
                System.out.println(player.getName() + " slid down a snake from " + snake.getHeadPosition() + " to " + snake.getTailPosition());
                player.setPosition(snake.getTailPosition());
            }
        }

        if (penalties.contains(player.getPosition())) {
            System.out.println(player.getName() + " landed on a penalty at " + player.getPosition() + "! Skipping next turn.");
            player.setSkipNextTurn(true);
        }

        if (player.getPosition() == this.boardSize) {
            this.gameStatus = 2;
        }
    }

    private Player getWhoseTurn() {
        if (this.gameStatus == 0) {
            this.gameStatus = 1;
            this.nowPlaying = (int) (Math.random() * players.size());
        } else {
            this.nowPlaying = (this.nowPlaying + 1) % players.size();
        }
        return players.get(nowPlaying);
    }

    private int getGameStatus() {
        return this.gameStatus;
    }
}
