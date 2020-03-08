package com.toni.entities;

public class Coordinate {
    private int x, y;

    /**
     * An xy-coordinate
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * get x-coordinate
     * @return int
     */
    public int getX() { return x; }


    /**
     * set x-coordinate
     * @param x int
     */
    public void setX(int x) { this.x = x; }


    /**
     * get y-coordinate
     * @return int
     */
    public int getY() { return y; }


    /**
     * set y-coordinate
     * @param y int
     */
    public void setY(int y) { this.y = y; }
}
