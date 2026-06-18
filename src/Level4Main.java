package application.level4;

import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Level4Main is the entry point for Level 4 (Bubble Shooter) in the game.
 * 
 * Game Instructions:
 *
 *   Click the mouse to shoot bubbles from the bottom cannon.
 *   Match 3 or more bubbles of the same color/type to eliminate them.
 *   Use the mouse to aim; the cannon will rotate to follow the cursor.
 *   Score points by eliminating bubbles. Reach the target score to win.
 *   If any bubble crosses the golden line near the bottom, you lose.
 *   Click after losing to restart the level.
 *
 * Class Purpose:
 * 
 *   Initializes and displays the Level 4 game scene.
 *   Handles the transition when the level is completed.
 */
public class Level4Main {
    /**
     * Starts Level 4 (Bubble Shooter) in the given stage.
     *
     * @param stage The JavaFX window to display the game.
     * @param onLevelComplete Callback to run when the player wins the level.
     */
    public void startLevel(Stage stage, Runnable onLevelComplete) {
        System.out.println("Level 4 startLevel method called");
        
        try {
            // Create game interface
            GamePane gamePane = new GamePane();
            
            // Set game win callback
            gamePane.setOnGameWin(() -> {
                System.out.println("Level 4 game victory");
                if (onLevelComplete != null) {
                    onLevelComplete.run();
                }
            });
            
            // Create scene and display
            Scene gameScene = new Scene(gamePane, 480, 640);
            stage.setScene(gameScene);
            stage.setTitle("Melody's Bubble Shooter!");
            stage.show();
            
        } catch (Exception ex) {
            System.err.println("Level 4 game startup failed: " + ex.getMessage());
            ex.printStackTrace();
            if (onLevelComplete != null) {
                onLevelComplete.run();
            }
        }
    }
} 