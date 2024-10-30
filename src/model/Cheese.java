package model;

/**
 * Class holding cheese information
 */
public class Cheese {
    private int x;
    private int y;

    public Cheese(int[] position){
        this.x = position[0];
        this.y = position[1];
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
    public int[] getCheesePosition(){
        return new int[]{x,y};
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
}
