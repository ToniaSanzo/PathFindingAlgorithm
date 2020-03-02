package com.toni.managers;

import com.toni.gamestate.GameState;
import com.toni.gamestate.PlayState;

public class GameStateManager {
    private GameState gameState; // Current game state

    public static final int MENU = 0;
    public static final int PLAY = 333;

    public GameStateManager(){
        setGameState(PLAY);
    }

    public void setGameState(int state){
        if(gameState != null) gameState.dispose(); // dispose the current game state

        // Switch to play state
        if(state == PLAY){ gameState = new PlayState(this); }
    }

    // update the current GameState
    public void update(float dt){ gameState.update(dt); }

    // draw the current GameState
    public void draw(){ gameState.draw(); }
}
