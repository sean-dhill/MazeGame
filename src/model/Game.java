package model;
import view.UI;
import java.util.Scanner;

/**
 * Class responsible for handling the game loop.Prompts user for their move input and handles and calls the respective
 * methods.
 */
public class Game {
    private static UI ui = new UI();


    public static void main(String[] args) {
        MazeGenerator generator = new MazeGenerator();
        char[][] maze = generator.generateValidMaze();

        GameState gameState = new GameState(maze);
        gameState.placeEntities();
        ui.startingMenu();
        ui.helpMenu();
        int playerX = gameState.getPlayer().getX();
        int playerY = gameState.getPlayer().getY();
        gameState.revealCells(playerX,playerY);

        Scanner scanner = new Scanner(System.in);
        boolean validMove = true;
        boolean skipPrint = false;
        while (true) {
            if(validMove && !skipPrint){
                gameState.printMaze();
                System.out.println("Cheese collected: " + gameState.getCheeseCollected() + " of " + gameState.getCheeseToWin());
            }
            System.out.println("Enter your move [WASD?]: ");
            String input = scanner.nextLine().toUpperCase();

            if(input.isEmpty()){
                System.out.println("Invalid move. Please enter just A (left), S (down), D (right), or W (up).");
                skipPrint = true;
                continue;
            }

            char move = input.charAt(0);
            validMove = true;
            skipPrint = false;

            if (move == '?') {
                ui.helpMenu();
                skipPrint = true;
                continue;
            }


            if (move == 'C'){
                gameState.changeCheeseToWin();
                continue;
            }
            if(move == 'M'){
                gameState.revealEntireMaze();
                System.out.println("The entire maze has been revealed!");
                continue;
            }
            if(!gameState.playerMove(move)){
                validMove = false;
            }else {
                int playerNewX = gameState.getPlayer().getX();
                int playerNewY = gameState.getPlayer().getY();
                gameState.revealCells(playerNewX, playerNewY);

                if (gameState.checkWin()) {
                    System.out.println("Game Over! You Win!");
                    break;
                }
                if (gameState.checkLoss()) {
                    System.out.println("Game Over!");
                    break;
                }
                gameState.moveCats();
                System.out.println("cats Moved");

                if (gameState.checkLoss()) {
                    System.out.println("Game Over!");
                    break;
                }
            }
        }
        scanner.close();
    }
}