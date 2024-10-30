package model;

/**
 * Class holding player information
 */
public class Player {
    private int x;
    private int y;

    public Player(int[] position){
        this.x = position[0];
        this.y = position[1];
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int[] getPlayerPosition(){
        return new int[]{x,y};
    }


}
