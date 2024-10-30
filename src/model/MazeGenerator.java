package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for generating the maze.
 */
public class MazeGenerator {
    private static final char WALL = '#';
    private static final char OPEN = ' ';
    private static final int WIDTH = 20;
    private static final int HEIGHT = 15;
    private final Random random = new Random();

    /**
     * Generates a valid maze and makes sure it has a valid structure.
     * @return A generated valid maze.
     */
    public char[][] generateValidMaze() {
        char[][] maze;

        do {
            maze = generateMaze();
            addCycles(maze, 0.2);
        } while (!isMazeValid(maze));

        return maze;
    }

    /**
     * Generates an initial maze structure using a version of Prims Algorithm for generating mazes.
     * @return Returns a generated maze.
     */
    public char[][] generateMaze() {
        char[][] maze = new char[HEIGHT][WIDTH];

        // Fill maze with walls
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                maze[y][x] = WALL;
            }
        }


        int startingX = 1 + random.nextInt((WIDTH - 2) / 2) * 2;
        int startingY = 1 + random.nextInt((HEIGHT - 2) / 2) * 2;
        maze[startingY][startingX] = OPEN;

        List<int[]> walls = new ArrayList<>();
        addWalls(maze, walls, startingX, startingY);

        while (!walls.isEmpty()) {
            int[] wall = walls.remove(random.nextInt(walls.size()));
            int wallX = wall[0];
            int wallY = wall[1];

            if (canBePath(maze, wallX, wallY)) {
                makePath(maze, wallX, wallY);
                addWalls(maze, walls, wallX, wallY);
            }
        }

        ensureCornerOpenness(maze);

        return maze;
    }

    /**
     * Ensures that the corners of the maze are always open
     * @param maze The 2D array representing the maze.
     */
    private void ensureCornerOpenness(char[][] maze) {
        maze[1][1] = OPEN; // Top-left corner
        maze[1][2] = OPEN;
        maze[2][1] = OPEN;

        maze[1][WIDTH - 2] = OPEN; // Top-right corner
        maze[1][WIDTH - 3] = OPEN;
        maze[2][WIDTH - 2] = OPEN;

        maze[HEIGHT - 2][1] = OPEN; // Bottom-left corner
        maze[HEIGHT - 3][1] = OPEN;
        maze[HEIGHT - 2][2] = OPEN;

        maze[HEIGHT - 2][WIDTH - 2] = OPEN; // Bottom-right corner
        maze[HEIGHT - 3][WIDTH - 2] = OPEN;
        maze[HEIGHT - 2][WIDTH - 3] = OPEN;
    }

    /**
     * Adds walls to the list of walls in the current position.
     * @param maze 2D array representing the maze.
     * @param walls The list of walls to be added.
     * @param x The x coordinate of the current position.
     * @param y The y coordinate of the current position.
     */
    private void addWalls(char[][] maze, List<int[]> walls, int x, int y) {
        if (x > 1 && maze[y][x - 2] == WALL) {
            walls.add(new int[]{x - 2, y});
        }
        if (x < WIDTH - 3 && maze[y][x + 2] == WALL) {
            walls.add(new int[]{x + 2, y});
        }
        if (y > 1 && maze[y - 2][x] == WALL) {
            walls.add(new int[]{x, y - 2});
        }
        if (y < HEIGHT - 3 && maze[y + 2][x] == WALL) {
            walls.add(new int[]{x, y + 2});
        }
    }

    /**
     * Checks if the current position can be turned into a path for the maze.
     * @param maze 2D array representing the maze.
     * @param x The x coordinate of the current position.
     * @param y The y coordinate of the current position.
     * @return True if the position can be turned into a path, false otherwise.
     */
    private boolean canBePath(char[][] maze, int x, int y) {
        int openCount = 0;
        if (x > 1 && maze[y][x - 2] == OPEN) {
            openCount++;
        }
        if (x < WIDTH - 2 && maze[y][x + 2] == OPEN) {
            openCount++;
        }
        if (y > 1 && maze[y - 2][x] == OPEN) {
            openCount++;
        }
        if (y < HEIGHT - 2 && maze[y + 2][x] == OPEN) {
            openCount++;
        }
        return openCount == 1;
    }

    /**
     * Makes a path in the maze at the specified location
     * @param maze 2D array representing the maze.
     * @param x The x coordinate of the position to be turned into a path.
     * @param y The y coorindate of the position to be turned into a path.
     */
    private void makePath(char[][] maze, int x, int y) {
        maze[y][x] = OPEN;
        if (x > 1 && maze[y][x - 2] == OPEN) {
            maze[y][x - 1] = OPEN;
        }
        if (x < WIDTH - 2 && maze[y][x + 2] == OPEN) {
            maze[y][x + 1] = OPEN;
        }
        if (y > 1 && maze[y - 2][x] == OPEN) {
            maze[y - 1][x] = OPEN;
        }
        if (y < HEIGHT - 2 && maze[y + 2][x] == OPEN) {
            maze[y + 1][x] = OPEN;
        }
    }

    /**
     * Randomly removes some walls from the maze to create cycles.
     * @param maze 2D array representing the maze.
     * @param removalProbability The probability to remove a wall.
     */
    private void addCycles(char[][] maze, double removalProbability) {
        for (int attempt = 0; attempt < 5; attempt++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                for (int x = 1; x < WIDTH - 1; x++) {
                    if (maze[y][x] == WALL && random.nextDouble() < removalProbability) {
                        if (x > 0 && x < WIDTH - 1 && y > 0 && y < HEIGHT - 1) {
                            if (!creates2x2OpenBlock(maze, x, y)) {
                                maze[y][x] = OPEN;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the maze has no invalid structures such as 2x2 spaces.
     * @param maze 2D array representing the maze.
     * @return true if the maze is valid, false otherwise.
     */
    private static boolean isMazeValid(char[][] maze) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {

                // Check for 2x2 open space
                if (maze[y][x] == OPEN &&
                        maze[y][x + 1] == OPEN &&
                        maze[y + 1][x] == OPEN &&
                        maze[y + 1][x + 1] == OPEN) {
                    return false;
                }

                // Check for 2x2 wall space
                if (maze[y][x] == WALL &&
                        maze[y][x + 1] == WALL &&
                        maze[y + 1][x] == WALL &&
                        maze[y + 1][x + 1] == WALL) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean creates2x2OpenBlock(char[][] maze, int x, int y) {
        if (maze[y - 1][x] == OPEN && maze[y][x - 1] == OPEN && maze[y - 1][x - 1] == OPEN) return true;
        if (maze[y - 1][x] == OPEN && maze[y][x + 1] == OPEN && maze[y - 1][x + 1] == OPEN) return true;
        if (maze[y + 1][x] == OPEN && maze[y][x - 1] == OPEN && maze[y + 1][x - 1] == OPEN) return true;
        if (maze[y + 1][x] == OPEN && maze[y][x + 1] == OPEN && maze[y + 1][x + 1] == OPEN) return true;
        return false;
    }
}

