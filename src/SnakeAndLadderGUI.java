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

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SnakeAndLadderGUI {
    private JFrame frame;
    private JLabel playerLabel;
    private JLabel positionLabel;
    private JLabel rollLabel;
    private JLabel rushHourTimerLabel;
    private JButton rollButton;
    private JButton quitButton;
    private JButton setupButton;
    private JPanel boardPanel;
    private int playerRoll;
    private Random random;
    private ArrayList<Player> players;
    private ArrayList<Snake> snakes;
    private ArrayList<Ladder> ladders;
    private int boardSize;
    private int nowPlaying;
    private boolean againstComputer;
    private boolean rushHourMode;
    private int rushHourTimeLeft;
    private int maxPosition;

    private Clip diceRollClip;
    private Clip snakeClip;
    private Clip moveClip;
    private Clip ladderClip;
    private Clip playerMoveClip;

    public SnakeAndLadderGUI() {
        initializeGUI();
        loadAudio();
        random = new Random();
        players = new ArrayList<>();
        snakes = new ArrayList<>();
        ladders = new ArrayList<>();
        nowPlaying = 0;
        rushHourMode = false;
    }

    private void initializeGUI() {
        frame = new JFrame("Snake and Ladder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700); // Adjusted size to fit the board and controls comfortably
        frame.setLayout(new BorderLayout());

        JPanel controlPanel = createControlPanel();

        frame.add(controlPanel, BorderLayout.WEST);

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout()); // Using GridBagLayout for more precise control

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);

        playerLabel = new JLabel("Player: ");
        controlPanel.add(playerLabel, gbc);

        gbc.gridy++;
        positionLabel = new JLabel("Position: 0");
        controlPanel.add(positionLabel, gbc);

        gbc.gridy++;
        rollLabel = new JLabel("Roll: 0");
        controlPanel.add(rollLabel, gbc);

        gbc.gridy++;
        rushHourTimerLabel = new JLabel("Rush Hour Timer: ");
        controlPanel.add(rushHourTimerLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(new RollButtonListener());
        controlPanel.add(rollButton, gbc);

        gbc.gridx++;
        quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitButtonListener());
        controlPanel.add(quitButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        setupButton = new JButton("Setup Game");
        setupButton.addActionListener(new SetupButtonListener());
        controlPanel.add(setupButton, gbc);

        return controlPanel;
    }

    private void loadAudio() {
        try {
            // Load dice roll sound
            AudioInputStream diceRollStream = AudioSystem.getAudioInputStream(new File("/mnt/data/dice_roll.wav"));
            diceRollClip = AudioSystem.getClip();
            diceRollClip.open(diceRollStream);

            // Load snake sound
            AudioInputStream snakeStream = AudioSystem.getAudioInputStream(new File("/mnt/data/snake.wav"));
            snakeClip = AudioSystem.getClip();
            snakeClip.open(snakeStream);

            // Load player move sound
            AudioInputStream moveStream = AudioSystem.getAudioInputStream(new File("/mnt/data/player_moves.wav"));
            moveClip = AudioSystem.getClip();
            moveClip.open(moveStream);

            // Load ladder sound
            AudioInputStream ladderStream = AudioSystem.getAudioInputStream(new File("/mnt/data/ladder.wav"));
            ladderClip = AudioSystem.getClip();
            ladderClip.open(ladderStream);

            // Load player moves sound
            AudioInputStream playerMoveStream = AudioSystem.getAudioInputStream(new File("/mnt/data/player_moves.wav"));
            playerMoveClip = AudioSystem.getClip();
            playerMoveClip.open(playerMoveStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void rollDice() {
        playDiceRollSound();
        Player currentPlayer = players.get(nowPlaying);
        playerRoll = currentPlayer.rollDice();
        rollLabel.setText("Roll: " + playerRoll);
        currentPlayer.moveAround(playerRoll, boardSize);
        positionLabel.setText("Position: " + currentPlayer.getPosition());

        playPlayerMoveSound();  // Play the player moves sound here

        checkLaddersAndSnakes(currentPlayer);

        if (currentPlayer.getPosition() >= boardSize) {
            currentPlayer.setPosition(boardSize);
            JOptionPane.showMessageDialog(frame, "Congratulations " + currentPlayer.getName() + ", you won!");
            rollButton.setEnabled(false);
        }

        if (rushHourMode) {
            rushHourTimeLeft--;
            rushHourTimerLabel.setText("Rush Hour Timer: " + rushHourTimeLeft);
            if (rushHourTimeLeft <= 0) {
                determineRushHourWinner();
                return;
            }
        }

        if (againstComputer && nowPlaying == 1) {
            // Computer's turn
            Player computer = players.get(0);
            int computerRoll = computer.rollDice();
            computer.moveAround(computerRoll, boardSize);
            playPlayerMoveSound();  // Play the player moves sound here
            checkLaddersAndSnakes(computer);
            if (computer.getPosition() >= boardSize) {
                computer.setPosition(boardSize);
                JOptionPane.showMessageDialog(frame, "Computer wins!");
                rollButton.setEnabled(false);
            }
        }

        nowPlaying = (nowPlaying + 1) % players.size();
        playerLabel.setText("Player: " + players.get(nowPlaying).getName());
        boardPanel.repaint();
    }

    private void playDiceRollSound() {
        if (diceRollClip != null) {
            diceRollClip.setFramePosition(0); // rewind to the beginning
            diceRollClip.start();
        }
    }

    private void playSnakeSound() {
        if (snakeClip != null) {
            snakeClip.setFramePosition(0); // rewind to the beginning
            snakeClip.start();
        }
    }

    private void playMoveSound() {
        if (moveClip != null) {
            moveClip.setFramePosition(0); // rewind to the beginning
            moveClip.start();
        }
    }

    private void playLadderSound() {
        if (ladderClip != null) {
            ladderClip.setFramePosition(0); // rewind to the beginning
            ladderClip.start();
        }
    }

    private void playPlayerMoveSound() {
        if (playerMoveClip != null) {
            playerMoveClip.setFramePosition(0); // rewind to the beginning
            playerMoveClip.start();
        }
    }

    private void checkLaddersAndSnakes(Player player) {
        for (Ladder ladder : ladders) {
            if (player.getPosition() == ladder.getBottomPosition()) {
                player.setPosition(ladder.getTopPosition());
                playLadderSound(); // Play the ladder sound here
            }
        }
        for (Snake snake : snakes) {
            if (player.getPosition() == snake.getHeadPosition()) {
                player.setPosition(snake.getTailPosition());
                playSnakeSound(); // Play the snake sound here
            }
        }
    }

    private class RollButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            rollDice();
        }
    }

    private class QuitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    private class SetupButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setupGame();
        }
    }

    private void setupGame() {
        String[] difficulties = { "Easy", "Normal", "Hard" };
        String difficulty = (String) JOptionPane.showInputDialog(frame, "Select difficulty:", "Setup Game",
                JOptionPane.QUESTION_MESSAGE, null, difficulties, difficulties[0]);
        if (difficulty == null) return;

        switch (difficulty) {
            case "Easy":
                boardSize = 50;
                addLadders(new int[][] { { 2, 23 }, { 8, 34 }, { 22, 36 }, { 32, 45 } });
                addSnakes(new int[][] { { 47, 5 }, { 29, 9 }, { 38, 15 }, { 41, 25 } });
                break;
            case "Normal":
                boardSize = 100;
                addLadders(new int[][] { { 2, 23 }, { 8, 34 }, { 22, 77 }, { 32, 68 }, { 41, 79 }, { 74, 88 }, { 82, 98 }, { 85, 95 } });
                addSnakes(new int[][] { { 47, 5 }, { 29, 9 }, { 38, 15 }, { 97, 25 }, { 53, 33 }, { 62, 37 }, { 86, 54 }, { 92, 70 } });
                break;
            case "Hard":
                boardSize = 200;
                addLadders(new int[][] { { 2, 23 }, { 8, 34 }, { 22, 77 }, { 32, 68 }, { 41, 79 }, { 74, 88 }, { 82, 98 }, { 85, 195 },
                        { 101, 123 }, { 122, 177 }, { 132, 168 }, { 141, 179 }, { 174, 188 }, { 182, 198 }, { 185, 195 } });
                addSnakes(new int[][] { { 47, 5 }, { 29, 9 }, { 38, 15 }, { 197, 25 }, { 153, 133 }, { 162, 137 }, { 186, 154 }, { 192, 170 } });
                break;
        }

        String[] options = { "2 Players", "1 Player vs Computer" };
        int n = JOptionPane.showOptionDialog(frame, "Select game mode:", "Setup Game", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        againstComputer = (n == 1);

        String[] modes = { "Normal Mode", "Rush Hour Mode" };
        String mode = (String) JOptionPane.showInputDialog(frame, "Select game mode:", "Setup Game",
                JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
        if (mode == null) return;

        rushHourMode = mode.equals("Rush Hour Mode");
        if (rushHourMode) {
            String timeInput = JOptionPane.showInputDialog(frame, "Enter time limit in seconds:");
            try {
                rushHourTimeLeft = Integer.parseInt(timeInput);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid time input. Setting to default 60 seconds.");
                rushHourTimeLeft = 60;
            }
        }

        players.clear();
        players.add(new Player("Player 1"));
        players.add(new Player(againstComputer ? "Computer" : "Player 2"));

        nowPlaying = 0;
        rollButton.setEnabled(true);
        JOptionPane.showMessageDialog(frame, "Game is ready to play!");
    }

    private void addLadders(int[][] positions) {
        ladders.clear();
        for (int[] pos : positions) {
            ladders.add(new Ladder(pos[0], pos[1]));
        }
    }

    private void addSnakes(int[][] positions) {
        snakes.clear();
        for (int[] pos : positions) {
            snakes.add(new Snake(pos[0], pos[1]));
        }
    }

    private void drawBoard(Graphics g) {
        int tileSize = boardPanel.getWidth() / 10; // Assuming a 10x10 grid
        for (int i = 0; i < boardSize; i++) {
            int x = (i % 10) * tileSize;
            int y = boardPanel.getHeight() - (i / 10 + 1) * tileSize;
            g.drawRect(x, y, tileSize, tileSize);
            g.drawString(String.valueOf(i + 1), x + tileSize / 2, y + tileSize / 2);
        }

        for (Player player : players) {
            int pos = player.getPosition();
            int x = (pos % 10) * tileSize + tileSize / 2;
            int y = boardPanel.getHeight() - (pos / 10 + 1) * tileSize + tileSize / 2;
            g.drawString(player.getName().substring(0, 1), x, y);
        }
    }

    private void determineRushHourWinner() {
        Player winner = players.get(0);
        for (Player player : players) {
            if (player.getPosition() > winner.getPosition()) {
                winner = player;
            }
        }
        JOptionPane.showMessageDialog(frame, "Time's up! " + winner.getName() + " wins with position " + winner.getPosition());
        rollButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeAndLadderGUI::new);
    }
}

