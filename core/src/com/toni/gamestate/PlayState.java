package com.toni.gamestate;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.toni.Game;
import com.toni.entities.Grid;
import com.toni.managers.GameKeys;
import com.toni.managers.GameStateManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;

public class PlayState extends GameState {
    private ShapeRenderer sr;
    private Grid grid;

    // Links PlayState to GameStateManager
    public PlayState(GameStateManager gsm){ super(gsm); }

    // init called when GameState starts
    public void init(){
        try{
            String gridStr = "";
            File file = new File(Paths.get("core/assets/grid.txt").toAbsolutePath().toString());
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){ gridStr = gridStr.concat(scan.nextLine()).concat("\n"); }
            sr = new ShapeRenderer();
            grid = new Grid();
            grid.initGrid(gridStr);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    // Update is called during the render() method
    public void update(float dt){
        handleInput();
        grid.update();
    }

    public void draw(){ grid.draw(sr); }


    // Method to interact with GameKeys
    public void handleInput(){
        // Handle mouse clicks
        if(GameKeys.isNewCoor()){ grid.getTile(GameKeys.getCoor()); }
    }

    // Called when we switch to a new GameState, and dispose current GameState
    public void dispose(){}
}
