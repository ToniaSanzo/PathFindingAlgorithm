package com.toni.managers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.toni.Game;

public class GameInputProcessor implements InputProcessor {
    private Vector3 tp;
    private boolean dragging;
    private OrthographicCamera cam;

    /**
     * Construct GameInputProcessor
     * @param cam Games orthographic camera
     */
    public GameInputProcessor(OrthographicCamera cam){
        this.cam = cam;
        tp = new Vector3();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY){
        // You can handle mouse movement without anything pressed
        // camera.unproject(tp.set(screenX, screenY, 0));  unproject method converts the x & y-screen-coordinate to the
        //                                                 game-universe x & y-coordinate
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        // ignore if its not the left mouse button or first touch pointer
        if(button != Input.Buttons.LEFT || pointer > 0) return false;
        Vector3 v3 = cam.unproject(tp.set(screenX, screenY, 0));
        System.out.println("x-coor: " + v3.x + "y-coor: " + v3.y);
        GameKeys.update(v3);
        dragging = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        if(!dragging) return false;
        cam.unproject(tp.set(screenX,screenY,0));
        dragging = false;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        if(button != Input.Buttons.LEFT || pointer > 0) return false;
        cam.unproject(tp.set(screenX, screenY, 0));
        dragging = false;
        return true;
    }


    @Override
    public boolean keyDown(int keycode){return false; }


    @Override
    public boolean keyUp(int keycode){return false; }


    @Override
    public boolean keyTyped(char character){ return false; }


    @Override
    public boolean scrolled(int amount){
        return false;
    }

}