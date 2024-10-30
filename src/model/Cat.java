package model;

/**
 * Class holding cat information
 */
public class Cat {
    private int x;
    private int y;

    public Cat(int[] position){
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
}
