package com.toni.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static com.toni.Game.xpos;
import static com.toni.Game.ypos;

public class Tile {
    protected static final int  INPASSABLE_WEIGHT = 10;
    private static BitmapFont font = new BitmapFont();
    private static SpriteBatch batch = new SpriteBatch();
    protected String tileID;                               // Unique TileID of the object
    protected int weight;                                  // weight of the tile 0-10, 10 being impassable, 0 being teleport
    private String weightStr;                              // weight represented as a String
    private boolean pathTile;                              // True, if tile is apart of the shortest path
    private boolean tpTile;                                // True, if tile is a teleport tile


    /**
     * Construct tile, with weight
     * @param type what type of tile this is
     */
    public Tile(String type){
        tileID = this.toString().substring(25);
        pathTile = false;
        tpTile = false;
        if(type.equals("F")) {
            this.weight = INPASSABLE_WEIGHT;
            this.weightStr = type;
            return;
        }
        if(type.substring(0,1).equals("T")){
            this.weight = 0;
            this.weightStr = type;
            this.tpTile = true;
            return;
        }
        this.weight = Integer.parseInt(type);
        this.weightStr = type;
    }


    /**
     * The path has been reset
     */
    public void resetPath(){ pathTile = false; }


    /**
     * This Tile is apart of the path
     */
    public void setPath(){ pathTile = true; }

    /**
     * @return Tile ID
     */
    public String getTileID(){return tileID; }



    boolean tmpBoolean = false;
    /**
     * Draws the tile at the given XY coordinate
     *
     * @param sr ShapeRender, draws the tile
     * @param x x-coordinte
     * @param y y-coordinte
     * @param tileWidth
     * @param tileHeight
     * @param slctd selected tiles
     */
    public void draw(ShapeRenderer sr, float x, float y, float tileWidth, float tileHeight, boolean slctd){
        if(slctd) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(.722f, .525f, .043f, 1); // Set Color to dark golden rod
            sr.rect((xpos + x) * tileWidth, (ypos + y) * tileHeight, tileWidth, tileHeight);
            sr.end();

            // Draw numbers
            /*
            batch.begin();
            font.setColor(1,1,1,1);
            font.getData().setScale(2f);
            sr.rect(x * tileWidth, y * (tileHeight + 11f),tileWidth, tileHeight + 50f);
            batch.end();*/
            return;
        }

        if(pathTile) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(.133f, .545f, .133f, 1); // Set Color to forest green
            sr.rect((xpos + x) * tileWidth, (ypos + y) * tileHeight, tileWidth, tileHeight);
            sr.end();

            // Draw numbers
            /*batch.begin();
            font.setColor(1,1,1,1);
            font.getData().setScale(2f);
            font.draw(batch,weightStr, x * tileWidth + (tileWidth/2f), y * tileHeight + (tileHeight/2f));
            batch.end();*/
            return;
        }

        if(tpTile) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(.125f, .698f, .67f, 1); // Set Color to forest green
            sr.rect((xpos + x) * tileWidth, (ypos + y) * tileHeight, tileWidth, tileHeight);
            sr.end();

            // Draw numbers
            /*batch.begin();
            font.setColor(1,1,1,1);
            font.getData().setScale(2f);
            font.draw(batch,weightStr, x * tileWidth + (tileWidth/2f), y * tileHeight + (tileHeight/2f));
            batch.end();*/
            return;
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);
        switch(weight){
            case 0: {
                sr.setColor(1f, 1f, 1f, 1); // Set Color to white
                break;
            }
            case 1: {
                sr.setColor(.92f, .92f, .92f, 1); // Set Color to light gray
                break;
            }
            case 2: {
                sr.setColor(.941f, .5f, .5f, 1); // Set Color to light pink
                break;
            }
            case 3: {
                sr.setColor(1f, .921f, .8f, 1); // Set Color to light skin tone
                break;
            }
            case 4: {
                sr.setColor(1f, .894f, .77f, 1); // Set Color to dark skin tone
                break;
            }
            case 5: {
                sr.setColor(.87f, .72f, .53f, 1); // Set Color to darker skin
                break;
            }
            case 6: {
                sr.setColor(.125f, .125f, .125f, 1); // Set Color to turoquoise
                break;
            }
            case 7: {
                sr.setColor(.627f, .322f, .176f, 1); // Set Color to light brown
                break;
            }
            case 8: {
                sr.setColor(.545f, .271f, .075f, 1); // Set Color to dark brown
                break;
            }
            case 9: {
                sr.setColor(.08f, .08f, .08f, 1); // Set Color to dark gray
                break;
            }
            case 10: {
                sr.setColor(0f, 0f, 0f, 1); // Set Color to black
                break;
            }

        }



        sr.rect((xpos + x) * tileWidth, (ypos + y) * tileHeight, tileWidth, tileHeight);
        sr.end();

        // Draw Numbers
        /*batch.begin();
        font.setColor(.1f * weight,.1f * weight,.1f * weight,1);
        font.getData().setScale(2f);
        font.draw(batch,weightStr, x * tileWidth + (tileWidth/2f), y * tileHeight + (tileHeight/2f));
        batch.end();*/
    }
}