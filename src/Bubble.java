package application.level4;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Bubble represents a single bubble in the Level 4 Bubble Shooter game.
 *
 * Purpose:
 *   Stores the position, color, and type of a bubble on the game grid.
 *   Provides a method to draw itself on the game canvas, either as a colored circle or as an image.
 * 
 *
 * Usage:
 *   Created and managed by GamePane to represent all bubbles in the game.
 *   Used for collision detection, elimination, and rendering.
 */
public class Bubble {
    /**
     * Row and column position in the bubble grid.
     */
    public int row, col;
    /**
     * Screen coordinates (pixels) for drawing.
     */
    public double x, y;
    /**
     * Color of the bubble (used if not using images).
     */
    public Color color;
    /**
     * Dessert type index (used for image mode, -1 for color mode).
     */
    public int dessertType;
    /**
     * The radius of the bubble in pixels.
     */
    private static final double BUBBLE_RADIUS = 20;

    /**
     * Constructs a Bubble at the given grid position, with the specified color and type.
     * Calculates the screen coordinates based on the grid position.
     *
     * @param row Row index in the grid
     * @param col Column index in the grid
     * @param color Bubble color
     * @param dessertType Dessert type index (for image mode)
     */
    public Bubble(int row, int col, Color color, int dessertType) {
        this.row = row;
        this.col = col;
        // Calculate screen coordinates: even and odd rows have different offsets
        this.x = col * 50 + (row % 2 == 0 ? 25 : 50);
        this.y = row * 45 + 25;
        this.color = color;
        this.dessertType = dessertType;
    }

    /**
     * Draws the bubble on the provided graphics context.
     * If dessertImages is provided and dessertType is valid, draws the image; otherwise draws a colored circle.
     *
     * @param gc The GraphicsContext to draw on
     * @param dessertImages Array of dessert images (can be null for color mode)
     */
    public void draw(GraphicsContext gc, Image[] dessertImages) {
        // If there are dessert images and type is valid, draw dessert image
        if (dessertImages != null && dessertType >= 0 && dessertType < dessertImages.length) {
            gc.drawImage(dessertImages[dessertType], x-BUBBLE_RADIUS, y-BUBBLE_RADIUS, BUBBLE_RADIUS*2, BUBBLE_RADIUS*2);
        } else {
            // Otherwise draw colored circle
            gc.setFill(color);
            gc.fillOval(x-BUBBLE_RADIUS, y-BUBBLE_RADIUS, BUBBLE_RADIUS*2, BUBBLE_RADIUS*2);
            // Add white border
            gc.setStroke(Color.WHITE);
            gc.strokeOval(x-BUBBLE_RADIUS, y-BUBBLE_RADIUS, BUBBLE_RADIUS*2, BUBBLE_RADIUS*2);
        }
    }
}