package application.level4;

import application.level2.SoundManager;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;

/**
 * GamePane is the main game panel for Level 4 (Bubble Shooter).
 *
 * Responsibilities:
 * 
 *   Handles all game logic, rendering, and user interaction for the bubble shooter level.
 *   Manages the game state, including bubbles, score, win/lose conditions, and user input.
 *   Draws the game scene, updates the game loop, and processes mouse/keyboard events.
 *
 * Usage:
 * 
 *   Instantiated and displayed by Level4Main.
 *   Set a win callback using setOnGameWin(Runnable) to handle level completion.
 */
public class GamePane extends Pane {
    // Canvas and graphics context for drawing
    private Canvas canvas;
    private GraphicsContext gc;

    // Game image resources
    private Image melodyImage, backgroundImage;
    private Image[] dessertImages;
    private Image winImage, loseImage;
    private Font cuteFont;

    // Game data
    private List<Bubble> bubbles = new ArrayList<>();
    private Bubble currentBubble;
    private boolean gameOver = false, gameWin = false;
    private double cannonAngle = 90;
    private boolean useImages = false;

    // Game constants
    private static final double CANNON_X = 240, CANNON_Y = 580;
    private static final double BUBBLE_RADIUS = 20;
    private static final double COLLIDE_DIST = BUBBLE_RADIUS * 2;
    private static final double CONNECT_DIST = BUBBLE_RADIUS * 2 + 8;

    // Game score and targets
    private int score = 0;
    private int targetScore = 400;
    private int clearCount = 0;
    private int clearThreshold = 7;

    // Game win callback
    private Runnable onGameWin;

    /**
     * Constructs the GamePane, initializes resources, sets up the game, and starts the game loop.
     * Handles mouse and keyboard events for gameplay.
     */
    public GamePane() {
        canvas = new Canvas(480, 640);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
        loadImages();
        loadFont();
        initGame();
        startGameLoop();
        setupMouseEvents();
        setupKeyboardEvents();
    }

    /**
     * Loads all image resources (background, bubbles, icons, etc.).
     * If loading fails, falls back to color-only mode.
     */
    private void loadImages() {
        try {
            melodyImage = new Image(getClass().getResource("/melody.png").toExternalForm());
            dessertImages = new Image[]{
                new Image(getClass().getResource("/bow.png").toExternalForm()),
                new Image(getClass().getResource("/heart.png").toExternalForm()),
                new Image(getClass().getResource("/wand.png").toExternalForm()),
                new Image(getClass().getResource("/crown.png").toExternalForm())
            };
            backgroundImage = new Image(getClass().getResource("/background4.png").toExternalForm());
            winImage = new Image(getClass().getResource("/win4.png").toExternalForm());
            loseImage = new Image(getClass().getResource("/lose.png").toExternalForm());
            useImages = true;
        } catch (Exception ex) {
            useImages = false;
        }
    }

    /**
     * Loads the custom font for UI text. Falls back to Arial if loading fails.
     */
    private void loadFont() {
        try {
            cuteFont = Font.loadFont(getClass().getResourceAsStream("/PixelifySans-Bold.ttf"), 24);
            if (cuteFont == null) throw new Exception("Font not found");
        } catch (Exception e) {
            System.err.println("Failed to load custom font: " + e.getMessage());
            cuteFont = Font.font("Arial", FontWeight.BOLD, 24);
        }
    }

    /**
     * Starts the main game loop using AnimationTimer.
     * Continuously updates and redraws the game scene.
     */
    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver) {
                    drawGame();
                    return;
                }
                updateGame();
                drawGame();
            }
        }.start();
    }

    /**
     * Sets up mouse movement and click events for aiming and shooting.
     * Mouse movement rotates the cannon; click fires a bubble.
     */
    private void setupMouseEvents() {
        setOnMouseMoved(e -> {
            if (gameOver) return;
            cannonAngle = Math.toDegrees(Math.atan2(e.getX() - CANNON_X, -(e.getY() - CANNON_Y)));
        });
        setOnMouseClicked(e -> {
            if (gameOver) {
                restartGame();
            } else if (!gameWin) {
                shootBubble(e.getX(), e.getY());
            }
        });
    }

    /**
     * Sets up keyboard events for restarting the game after losing.
     * Press ENTER or SPACE to restart if the game is over.
     */
    private void setupKeyboardEvents() {
        Platform.runLater(() -> {
            getScene().setOnKeyPressed(e -> {
                if (gameOver && !gameWin) {
                    if (e.getCode() == javafx.scene.input.KeyCode.ENTER ||
                        e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                        restartGame();
                    }
                }
            });
            requestFocus();
        });
    }

    /**
     * Restarts the game by resetting state and re-initializing bubbles.
     */
    private void restartGame() {
        gameOver = false;
        gameWin = false;
        initGame();
        setupMouseEvents();
    }

    /**
     * Sets the callback to be run when the player wins the level.
     * @param callback Runnable to execute on game win
     */
    public void setOnGameWin(Runnable callback) {
        this.onGameWin = callback;
    }

    /**
     * Initializes the game state, creates the bubble grid and the first bubble to shoot.
     */
    private void initGame() {
        bubbles.clear();
        score = 0;
        int totalRows = 16;
        int showRows = 8;
        int cols = 9;
        // Create initial bubble grid
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < cols; col++) {
                Bubble b = new Bubble(row, col, getRandomColor(), useImages ? getRandomDessertType() : -1);
                if (row < showRows) {
                    bubbles.add(b);
                }
            }
        }
        // Create current bubble
        currentBubble = new Bubble(12, 4, getRandomColor(), useImages ? getRandomDessertType() : -1);
    }

    /**
     * Fires a bubble in the direction of the mouse click.
     * Handles collision, snapping to grid, and elimination logic.
     * @param mx Mouse X coordinate
     * @param my Mouse Y coordinate
     */
    private void shootBubble(double mx, double my) {
        if (gameOver || gameWin) return;
        // Calculate shooting direction
        double dx = mx - CANNON_X, dy = my - CANNON_Y;
        double len = Math.hypot(dx, dy);
        dx = dx / len * 10;
        dy = dy / len * 10;
        // Simulate bubble flight trajectory
        double x = CANNON_X, y = CANNON_Y;
        boolean hit = false;
        outer:
        while (x > 0 && x < 480 && y > 0 && y < 640) {
            x += dx;
            y += dy;
            // Boundary bounce
            if (x <= BUBBLE_RADIUS || x >= 480 - BUBBLE_RADIUS) {
                dx = -dx;
                x += dx;
            }
            // Detect collision with other bubbles
            for (Bubble b : bubbles) {
                if (Math.hypot(x - b.x, y - b.y) < COLLIDE_DIST) {
                    x -= dx;
                    y -= dy;
                    hit = true;
                    break outer;
                }
            }
        }
        if (!hit) return;
        // Snap bubble to grid
        int[] snap = snapToGrid(x, y);
        int row = snap[0], col = snap[1];
        String key = getKey(row, col);
        boolean occupied = bubbles.stream().anyMatch(b -> getKey(b.row, b.col).equals(key));
        if (!occupied) {
            // Add new bubble
            Bubble nb = new Bubble(row, col, currentBubble.color, currentBubble.dessertType);
            bubbles.add(nb);
            currentBubble = new Bubble(12, 4, getRandomColor(), useImages ? getRandomDessertType() : -1);
            // Delay check for elimination
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                Set<Bubble> group = new HashSet<>();
                flood(nb, group);
                // If connected count >= 3, eliminate
                if (group.size() >= 3) {
                    SoundManager.getInstance().playBubbleSound();
                    bubbles.removeAll(group);
                    score += group.size() * 5;
                    clearCount++;
                    // Check if target score reached
                    if (score >= targetScore) {
                        gameWin = true;
                        SoundManager.getInstance().playHappySound();
                        currentBubble = null;
                        PauseTransition winPause = new PauseTransition(Duration.seconds(2));
                        winPause.setOnFinished(event -> {
                            if (onGameWin != null) onGameWin.run();
                        });
                        winPause.play();
                        return;
                    }
                    // Check if need to add new row
                    if (clearCount >= clearThreshold) {
                        clearCount = 0;
                        // All bubbles move down one row
                        for (Bubble b : bubbles) b.row++;
                        for (Bubble b : bubbles) {
                            b.y = b.row * 45 + 25;
                            b.x = b.col * 50 + (b.row % 2 == 0 ? 25 : 50);
                        }
                        // Add new row at top
                        int fillRow = 0;
                        for (int fillCol = 0; fillCol < 9; fillCol++) {
                            Bubble newB = new Bubble(fillRow, fillCol, getRandomColor(), useImages ? getRandomDessertType() : -1);
                            newB.x = fillCol * 50 + (fillRow % 2 == 0 ? 25 : 50);
                            newB.y = fillRow * 45 + 25;
                            bubbles.add(newB);
                        }
                    }
                }
            });
            pause.play();
        }
    }

    /**
     * Updates the game state, checks for lose condition (bubbles crossing the bottom line).
     */
    private void updateGame() {
        boolean hasBubbleOverLine = bubbles.stream().anyMatch(b -> b.y > 500);
        if (hasBubbleOverLine) {
            SoundManager.getInstance().playSadSound();
            gameOver = true;
            gameWin = false;
            bubbles.clear();
            currentBubble = null;
        }
    }

    /**
     * Draws the entire game scene, including background, bubbles, cannon, and UI.
     */
    private void drawGame() {
        // Draw background
        if (useImages && backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, 480, 640);
        } else {
            gc.setFill(Color.LAVENDERBLUSH);
            gc.fillRect(0, 0, 480, 640);
        }
        if (!gameOver && !gameWin) {
            // Draw cannon
            gc.save();
            gc.translate(CANNON_X, CANNON_Y);
            gc.rotate(cannonAngle);
            gc.setStroke(Color.DEEPPINK);
            gc.setLineWidth(4);
            gc.strokeLine(0, 0, 0, -50);
            gc.restore();
            // Draw Melody character
            if (useImages && melodyImage != null) {
                gc.drawImage(melodyImage, 180, 500, 120, 120);
            }
            // Draw bottom line
            gc.setFill(Color.GOLD);
            gc.fillRect(0, 500, 480, 5);
            // Draw all bubbles
            for (Bubble b : bubbles) b.draw(gc, useImages ? dessertImages : null);
            // Draw current bubble
            if (currentBubble != null) {
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(14));
                gc.fillText("Current Bubble:", 360, 550);
                currentBubble.x = 430;
                currentBubble.y = 580;
                currentBubble.draw(gc, useImages ? dessertImages : null);
            }
        }
        // Draw score information
        gc.setFill(Color.DEEPPINK);
        gc.setFont(Font.font(18));
        gc.fillText("Score: " + score, 20, 590);
        gc.setFont(Font.font(14));
        gc.fillText("Target: " + targetScore, 20, 610);
        // Draw game over interface
        if (gameOver) {
            if(useImages && loseImage != null) {
                gc.drawImage(loseImage, (480 - loseImage.getWidth()) / 2, 140);
            }
            gc.setFill(Color.HOTPINK);
            gc.setFont(cuteFont);
            gc.fillText("Click to Restart", 125, 450);
        } else if (gameWin && useImages && winImage != null) {
            gc.drawImage(winImage, (480 - winImage.getWidth()) / 2, 140);
        }
    }

    /**
     * Converts screen coordinates to grid coordinates for bubble placement.
     * @param x Screen X coordinate
     * @param y Screen Y coordinate
     * @return int array [row, col] representing grid position
     */
    private int[] snapToGrid(double x, double y) {
        int row = (int) Math.round((y - 25) / 45);
        boolean even = (row % 2 == 0);
        double xOffset = even ? 25 : 50;
        int col = (int) Math.round((x - xOffset) / 50);
        return new int[]{row, col};
    }

    /**
     * Returns a list of neighboring grid positions for a given bubble.
     * @param row Row coordinate
     * @param col Column coordinate
     * @return List of int arrays [row, col] for each neighbor
     */
    private List<int[]> getNeighborPositions(int row, int col) {
        List<int[]> neighbors = new ArrayList<>();
        boolean even = (row % 2 == 0);
        if (even) {
            neighbors.add(new int[]{row, col - 1});
            neighbors.add(new int[]{row, col + 1});
            neighbors.add(new int[]{row - 1, col - 1});
            neighbors.add(new int[]{row - 1, col});
            neighbors.add(new int[]{row + 1, col - 1});
            neighbors.add(new int[]{row + 1, col});
        } else {
            neighbors.add(new int[]{row, col - 1});
            neighbors.add(new int[]{row, col + 1});
            neighbors.add(new int[]{row - 1, col});
            neighbors.add(new int[]{row - 1, col + 1});
            neighbors.add(new int[]{row + 1, col});
            neighbors.add(new int[]{row + 1, col + 1});
        }
        return neighbors.stream().filter(p -> p[0] >= 0 && p[1] >= 0 && p[1] < 9).toList();
    }

    /**
     * Generates a unique string key for a grid position.
     * @param row Row coordinate
     * @param col Column coordinate
     * @return String key in the format "row,col"
     */
    private String getKey(int row, int col) {
        return row + "," + col;
    }

    /**
     * Flood fill algorithm to find all connected bubbles of the same color/type.
     * Used for elimination logic after a bubble is placed.
     * @param start The starting bubble
     * @param group Set to collect all connected bubbles
     */
    private void flood(Bubble start, Set<Bubble> group) {
        Map<String, Bubble> positionMap = new HashMap<>();
        for (Bubble b : bubbles) {
            positionMap.put(getKey(b.row, b.col), b);
        }
        Queue<Bubble> queue = new LinkedList<>();
        queue.offer(start);
        group.add(start);
        while (!queue.isEmpty()) {
            Bubble current = queue.poll();
            for (int[] neighborPos : getNeighborPositions(current.row, current.col)) {
                Bubble neighbor = positionMap.get(getKey(neighborPos[0], neighborPos[1]));
                if (neighbor != null && !group.contains(neighbor)) {
                    boolean match = useImages ? 
                        neighbor.dessertType == start.dessertType && neighbor.dessertType != -1 : 
                        neighbor.color.equals(start.color);
                    if (match) {
                        group.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Returns a random color for a bubble (used in color-only mode).
     * @return A random Color
     */
    private Color getRandomColor() {
        Color[] cs = {Color.PINK, Color.LIGHTPINK, Color.HOTPINK, Color.PALEVIOLETRED};
        return cs[new Random().nextInt(cs.length)];
    }

    /**
     * Returns a random dessert type index (used in image mode).
     * @return An integer index for dessertImages
     */
    private int getRandomDessertType() {
        return useImages && dessertImages != null ? new Random().nextInt(dessertImages.length) : -1;
    }
}