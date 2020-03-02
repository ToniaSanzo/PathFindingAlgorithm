package com.toni.gamestate;

import com.toni.managers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;

    // Constructor, initialize current GameState, all GameState's should have the same manager
    protected GameState(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }

    // Called when the game starts
    public abstract void init();

    /**
     * Update is called during render() method
     * @param dt Delta Time, time in seconds since the last render method
     */
    public abstract void update(float dt);

    // Draw is called during the render() method
    public abstract void draw();

    // Method for interacting with GameKeys
    public abstract void handleInput();

    // Called when we switch to a new GameState, and dispose current GameState
    public abstract void dispose();
}
