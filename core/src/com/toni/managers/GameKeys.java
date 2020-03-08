package com.toni.managers;

import com.badlogic.gdx.math.Vector3;

public class GameKeys {

    private static Vector3 coor;
    private static boolean newCoor;

    /**
     * Update the gameInput with a vector from the
     *
     * @param coor
     */
    public static void update(Vector3 coor){
        newCoor = true;
        GameKeys.coor = coor;
    }


    /**
     * Determine whether a coor has been updated. Set's static variable newCoor to false
     *
     * @return True if the coor was updated, otherwise false.
     */
    public static boolean isNewCoor(){
        if(newCoor){
            newCoor = false;
            return true;
        }
        return false;
    }


    /**
     * @return Vector3 value in coor
     */
    public static Vector3 getCoor(){ return coor; }
}
