package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for creating random movement for the cats.
 */
public class RandomMovementGenerator {
    private Random random = new Random();
    private GameState gameState;

    public RandomMovementGenerator(GameState gameState){
        this.gameState = gameState;
    }
    public final int[][] moves = {
            {0, -1}, // UP
            {0, 1},  // DOWN
            {-1, 0}, // LEFT
            {1, 0},  // RIGHT
    };

    /**
     * Method responsible for generating random move for the cat. Tries to avoid backtracking if possible.
     * @param catX The current x coordinate of the cat.
     * @param catY The current y coordinate of the cat.
     * @param lastXMoveX The x coordinate of the cat's last move.
     * @param lastYMoveY The y coordinate of the cat's last move.
     * @return A 2d array representing the cat's next move.
     */
    public int[] generateRandomMove(int catX, int catY,int lastXMoveX, int lastYMoveY) {
        int[] move;
        List<int[]> possibleMoves = new ArrayList<>();

        for (int[] potentialMove : moves) {
            int newX = catX + potentialMove[0];
            int newY = catY + potentialMove[1];
            if (gameState.isValidMove(newX, newY) && !isBackTracking(catX, catY, potentialMove, lastXMoveX, lastYMoveY)) {
                possibleMoves.add(potentialMove);
            }
        }

        if (!possibleMoves.isEmpty()) {
            move = possibleMoves.get(random.nextInt(possibleMoves.size()));
        } else {
            move = new int[]{lastXMoveX - catX, lastYMoveY - catY};
        }

        return move;
    }

    /**
     *
     * @param catX The current x coordinate of the cat.
     * @param catY The current y coordinate of the cat.
     * @param move Array representing the move's x and y offsets to determine where the cat will be placed.
     * @param lastMoveX The x coordinate of the cat's last move.
     * @param lastMoveY The y coordinate of the cat's last move.
     * @return
     */
    private boolean isBackTracking(int catX, int catY, int[] move, int lastMoveX, int lastMoveY){
        return catX + move[0] == lastMoveX && catY + move[1] == lastMoveY;
    }
}
