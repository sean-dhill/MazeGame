package view;

/**
 * Class resonabile for printing starting and help menues.
 */
public class UI {
    public void startingMenu(){
        System.out.println("----------------------------------------");
        System.out.println("Welcome to Cat and Mouse Maze Adventure!");
        System.out.println("by Sean Dhillon");
        System.out.println("----------------------------------------");
        System.out.println();

    }
    public void helpMenu(){
        System.out.println("DIRECTIONS:");
        System.out.println("        Find 5 cheese before a cat eats you!");
        System.out.println("LEGEND:");
        System.out.println("        #: Wall");
        System.out.println("        @: You (a mouse)");
        System.out.println("        !: Cat");
        System.out.println("        $: Cheese");
        System.out.println("        .: Unexplored Space");
        System.out.println("MOVES:");
        System.out.println("        Use W (up), A (left), S (down), and D (right) to move.");
        System.out.println("        (You must press enter after each move");
    }
}
