package model;

import view.UI;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class responsible for managing the game appearance and state
 */
public class GameState {
    private char[][] maze;
    private Player player;
    private Cheese cheese;
    private Cat[] cats;
    private final int WIDTH = 20;
    private final int HEIGHT = 15;
    private final char OPEN = ' ';
    private final Random random = new Random();
    private UI ui;
    private RandomMovementGenerator randomMovementGenerator;
    private int[][] lastCatPositions;
    private int cheeseCollected = 0;
    private int cheeseToWin = 5;
    private boolean[][] revealedCells;


    public GameState(char[][] maze){
        this.maze = maze;

        this.randomMovementGenerator = new RandomMovementGenerator(this);

        this.lastCatPositions = new int[3][2];

        this.revealedCells = new boolean[HEIGHT][WIDTH];
        for(int i = 0; i < HEIGHT; i++){
            Arrays.fill(revealedCells[i], false);
        }

        ui = new UI();

        player = new Player(new int[]{1,1});

        cheese = new Cheese(getRandomPosition());

        cats = new Cat[3];
        cats[0] = new Cat(new int[]{ WIDTH - 2,1});
        cats[1] = new Cat(new int[]{1,HEIGHT - 2});
        cats[2] = new Cat(new int[]{WIDTH - 2, HEIGHT - 2});

    }

    /**
     * Method which creates a random position in maze
     * @return random position in the maze
     */
    private int[] getRandomPosition(){
        int x;
        int y;
        do{
            x = random.nextInt(maze[0].length - 2) + 1;
            y = random.nextInt(maze.length - 2) + 1;
        }while (maze[y][x] != OPEN || (x == player.getX() && y == player.getY()));
        return new int[]{x,y};
    }

    /**
     * "Places" entities onto the maze
     */
    public void placeEntities(){
        maze[player.getY()][player.getX()] = '@';

        maze[cheese.getY()][cheese.getX()] = '$';

        Arrays.stream(cats)
                .forEach(cat -> maze[cat.getY()][cat.getX()] = '!');
    }

    /**
     * Method which marks the perimeter cells as revealed and all surrounding cells to the player as revealed
     * Revealed cells will have the actual contents of their cell printed, otherwise '.' will be outputted.
     * @param playerX player current x position
     * @param playerY player current y position
     */
    public void revealCells(int playerX, int playerY){

        for (int x = 0; x < WIDTH; x++) {
            revealedCells[0][x] = true; // Top wall
            revealedCells[HEIGHT - 1][x] = true; // Bottom wall
        }
        for (int y = 0; y < HEIGHT; y++) {
            revealedCells[y][0] = true; // Left wall
            revealedCells[y][WIDTH - 1] = true; // Right wall
        }

        for(int y = -1; y <=1; y++){
            for(int x = -1; x <= 1; x++){
                if(x == 0 && y == 0){
                    continue;
                }
                int newX = playerX + x;
                int newY = playerY + y;

                if (newX >= 0 && newX < WIDTH && newY >= 0 && newY < HEIGHT) {
                    revealedCells[newY][newX] = true; // Reveal the cell
                }
            }
        }
    }

    /**
     * Method which outputs maze to screen. Takes into account which cells are revealed to the player and which are not
     * and does the respective output
     */
    public void printMaze() {
        System.out.println("Maze:");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if(revealedCells[y][x] || maze[y][x] == '@' || maze[y][x] == '$' || maze[y][x] == '!'){
                    System.out.print(maze[y][x]);
                }else{
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    /**
     * Method resposbile for moving the player. Depending on their input, will depend on the players new location
     * @param move is the player's move (W,A,S,D)
     * @return true if
     */
    public boolean playerMove(char move){
        int[] playerPosition = player.getPlayerPosition();
        int x = playerPosition[0];
        int y = playerPosition[1];
        int newX = x;
        int newY = y;

        switch (move){
            case 'W':
                newY = y - 1;
                break;
            case 'A':
                newX = x - 1;
                break;
            case 'S':
                newY = y + 1;
                break;
            case 'D':
                newX = x + 1;
                break;
            default:
                System.out.println("Invalid move. Please enter just A (left), S (down), D (right), or W (up).");
                return false;
        }
        if(isValidMove(newX,newY)){
            updatePlayerPosition(newX,newY);
            return true;
        }else{
            System.out.println("Invalid move: you cannot move through walls!");
            return false;
        }
    }

    /**
     * Method responsible for moving the cats. Takes a position generated from the RandomMovementGenerator class and
     * then uses that postion and updates the cats position. Uses streams to go iterate through cat list.
     */
    public void moveCats(){
        AtomicInteger index = new AtomicInteger(0);
        Arrays.stream(cats).forEach(cat -> {
            int catX = cat.getX();
            int catY = cat.getY();

            int currentIndex = index.getAndIncrement();

            int[] move = randomMovementGenerator.generateRandomMove(
            catX, catY,lastCatPositions[currentIndex][0],lastCatPositions[currentIndex][1]
            );

            lastCatPositions[currentIndex][0] = catX;
            lastCatPositions[currentIndex][1] = catY;

            updateCatPosition(cat, catX + move[0], catY + move[1]);

        });
    }

    /**
     * Method which checks if move is valid and doesn't collide with a wall
     * @param x is the x coordinate of the intended move
     * @param y is the y coordinate of the intended move
     * @return returns true if x and y coordinates of the intended move don't collide with a wall, false if they do.
     */
    public boolean isValidMove(int x, int y){
        return x >= 1 && x < maze[0].length - 1 &&
               y >= 1 && y < maze.length - 1 &&
               maze[y][x] != '#';
    }

    /**
     * Updates the players position on the game board.
     * @param x is the new x position of the player.
     * @param y is the new y position of the player.
     */
    public void updatePlayerPosition(int x, int y){
        maze[player.getY()][player.getX()] = ' ';

        player.setPosition(x,y);

        if(Arrays.equals(player.getPlayerPosition(),cheese.getCheesePosition())){
            cheeseCollected++;
            respawnCheese();
        }

        maze[player.getY()][player.getX()] = '@';
    }

    /**
     * Method responsible for regenerating a new cheese on the board after the player collects it.
     */
    private void respawnCheese() {
        int [] newPosition = getRandomPosition();
        cheese.setPosition(newPosition[0],newPosition[1]);
        maze[cheese.getY()][cheese.getX()] = '$';
    }

    /**
     * Method which checks to see if the player has collected the amount of cheese needed to win.
     * @return returns true if player has collected the amount of cheese needed to win, false otherwise.
     */
    public boolean checkWin(){
        return cheeseCollected >= cheeseToWin;
    }

    /**
     * Method which is responsible for updating the cats position on the board.
     * @param cat is the list of cats in the maze and their positions.
     * @param x is the new x value of the cat's position.
     * @param y is the new y value of the cats position.
     */
    public void updateCatPosition(Cat cat, int x, int y){

        if(cat.getX() == cheese.getX() && cat.getY() == cheese.getY()){
            maze[cat.getY()][cat.getX()] = '$';
        }else{
            maze[cat.getY()][cat.getX()] = ' ';
        }
        cat.setPosition(x,y);

        maze[cat.getY()][cat.getX()] = '!';
    }

    /**
     * Method which checks if any of the cats positions are the same as the players position, indicating a collision has occurred.
     * @return returns true if collision has occurred, false otherwise.
     */
    public boolean checkLoss(){
        return Arrays.stream(cats)
                .anyMatch(cat -> player.getX() == cat.getX() && player.getY() == cat.getY());
    }

    /**
     * Cheat code used to change the required amount of cheese to win
     */
    public void changeCheeseToWin(){
        System.out.println("Changing cheese to win from " + cheeseToWin + " to 1.");
        cheeseToWin = 1;
    }

    /**
     * Cheat code to reveal the entire maze.
     */
    public void revealEntireMaze() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                revealedCells[y][x] = true; // Mark all cells as revealed
            }
        }
    }
    public int getCheeseToWin(){
        return cheeseToWin;
    }
    public int getCheeseCollected(){
        return cheeseCollected;
    }
    public Player getPlayer() {
        return player;
    }


}


