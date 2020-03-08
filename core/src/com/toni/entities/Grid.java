package com.toni.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Stack;

import static com.toni.Game.xpos;
import static com.toni.Game.ypos;

public class Grid {

    public static class TeleportTiles{
        int x1, y1, x2, y2, tpId;   // the indices of the teleport tiles array position & teleport-tuple #
        boolean t1Set, t2Set;        // boolean flags: determine whether tiles have been set or not

        /**
         * Construct default teleport tile
         */
        private TeleportTiles() {
            t1Set = false;
            t2Set = false;
        }

        /**
         * Add a TeleportTile's position to the grid
         * @param x x-pos
         * @param y y- pos
         */
        private void addTile(int x, int y, int tpId){
            if(!t1Set){
                x1 = x; y1 = y;
                this.tpId = tpId;
                t1Set = true;
                return;
            }
            if(!t2Set){
                x2 = x; y2 = y;
                t2Set = true;
                return;
            }
            System.err.println("Attempted to add more than two teleport tile's with same index");
            System.exit(1);
        }

        /**
         * Return's true if a given x & y-value matches a TeleportTile x & y pair
         * @param x x-coordinate
         * @param y y-coordinate
         * @return True if the x & y coordinate are apart of this TeleportTile pair
         */
        private boolean checkXY(int x, int y){
            if(x1 == x && y1 == y) return true;
            if(x2 == x && y2 == y) return true;
            return false;
        }

        /**
         * X and Y coordinate of the corresponding x & y teleport tile
         * @param x x-coordinate
         * @param y y-coordnate
         * @return x & y-coordinate connected to the parameter's x & y-coordinate
         */
        private int [] getXY(int x, int y){
            int [] xy = new int[2];
            if(x1 == x && y1 == y){
                xy[0] = x2;
                xy[1] = y2;
            }

            if(x2 == x && y2 == y){
                xy[0] = x1;
                xy[1] = y1;
            }

            return xy;
        }

        /**
         * @return TeleportTiles ID
         */
        private int getTpId(){ return tpId; }
    }

    private int rows;                         // Number of rows in Grid
    private int cols;                         // Number of cols in Grid
    protected float tileWidth;                // Width of a Tile
    protected float tileHeight;               // Height of a Tile
    private Tile src, trgt;                   // Source and Taget Tile for the Path-Finding Algorithm
    private boolean setSrc, setTrgt;          // Flags: determine what action clicking should results in
    private Tile [][] tiles;                  // 2-D array of tiles
    private Graph graph;
    private ArrayList<TeleportTiles> tpTiles; // Collection of Teleport Tiles

    /**
     * Create a grid with specified dimensions
     */
    public Grid(){
        this.rows = 0;
        this.cols = 0;
        tileWidth = 0;
        tileHeight = 0;
        setSrc = true;
        setTrgt = false;
        tpTiles = new ArrayList<>();
    }

    /**
     * Handle user interaction with the game world
     * @param v3 vector location of the user's click
     */
    public void getTile(Vector3 v3){
        if(setSrc){
            int colCoor = 0, rowCoor = 0;
            float x = v3.x/tileWidth;
            float y = v3.y/tileHeight;

            // Determine x-index
            for(int i = 0; i < cols; i++){
                if((float)i <= x && x < (float)i + 1f){ colCoor = i - (int)xpos; }
            }

            // Determine y-index
            for(int j = 0; j < rows; j++){
                if((float)j <= y && y < (float)j + 1f){ rowCoor = j - (int)ypos; }
            }


            if((colCoor < 0 || colCoor > cols) || (rowCoor < 0 || rowCoor > rows) ){ return; }
            src = tiles[colCoor][rowCoor];
            setSrc = false;
            setTrgt = true;
            return;
        }


        if(setTrgt){
            int colCoor = 0, rowCoor = 0;
            float x = v3.x/tileWidth;
            float y = v3.y/tileHeight;
            // Determine x-index
            for(int i = 0; i < cols; i++){
                if((float)i <= x && x < (float)i + 1f) { colCoor = i - (int)xpos; }
            }

            // Determine y-index
            for(int j = 0; j < rows; j++){
                if(((float)j <= y) && y <= (float)j + 1f){ rowCoor = j - (int)ypos; }
            }

            if((colCoor < 0 || colCoor > cols) || (rowCoor < 0 || rowCoor > rows) ){ return; }
            Graph.Vertex vertex;
            trgt = tiles[colCoor][rowCoor];
            setTrgt = false;

            Stack<Graph.Vertex> vertexStack;

            try {
                vertexStack = graph.aStar(src, trgt);
            } catch(Exception ex){
                vertexStack = null;
            }
            //Stack<Graph.Vertex> vertexStack = graph.dijkstra(src, trgt);
            while(vertexStack != null && !vertexStack.isEmpty()){
                vertex = vertexStack.pop();
                for(int y1 = 0; y1 < rows; y1++){
                    for(int x1 = 0; x1 < cols; x1++) {
                        if (tiles[x1][y1].tileID.equals(vertex.getValue().tileID)) { tiles[x1][y1].setPath(); }
                    }
                }
            }
            return;
        }

        // Reset the graph
        src = null;
        trgt = null;
        setSrc = true;
        for(int y = 0; y < rows; y++){ for(int x = 0; x < cols; x++) { tiles[x][y].resetPath(); } }
    }


    /**
     * Determine grid dimensions
     * @param str grid string
     */
    public void initDimensions(String str){
        char tmpChar;            // Temporary character
        int charIndex = 0;       // initialize charsIndex value
        rows = 0;                // initialize rows value
        cols = 1;                // initialize cols value

        // Determine number of columns
        while(true){
            // Get the character at charIndex
            tmpChar = str.charAt(charIndex);
            if(tmpChar == ' '){ ++cols; }

            if(tmpChar == '\n'){
                ++rows;
                break;
            }
            charIndex++;
        }

        // Determine number of rows
        while((charIndex + 1) < str.length()){
            charIndex += cols;
            tmpChar = str.charAt(charIndex);
            while(tmpChar !='\n' && (charIndex + 1) < str.length()){
                charIndex++;
                tmpChar = str.charAt(charIndex);
            }
            ++rows;
        }
        tileWidth = Gdx.graphics.getWidth()/ (float)cols;
        tileHeight = Gdx.graphics.getHeight()/ (float)rows;

        if(tileWidth < 1) tileWidth = 1;
        if(tileHeight < 1) tileHeight = 1;
        tiles = new Tile[cols][rows];
    }


    /**
     * Initialize Grid
     * @param string String representation of Grid
     */
    public void initGrid(String string){
        // Determine the number of rows and columns
        initDimensions(string);

       String [] gridVal = string.split(" |(\\n)");
       int gridValInd = 0 ;

       for(int y = 0; y < rows; y++){
           for(int x = 0; x < cols; x++){
               tiles[x][y] = new Tile(gridVal[gridValInd]);
               if(gridVal[gridValInd].substring(0, 1).equals("T")){
                   boolean found = false;
                   int tpID =  Integer.parseInt(gridVal[gridValInd].substring(1));  // tpID <- teleport tile ID
                   int tpTilesInd = 0;                                              // Index in ArrayList Tiles hould be
                                                                                    // added to
                   // Add the Teleport Tile to the repository
                   if(tpTiles.isEmpty()){
                       tpTiles.add(new TeleportTiles());
                   } else{
                       for(TeleportTiles tmp: tpTiles) {
                           if(tpID == tmp.getTpId()){
                               found = true;
                               break;
                           }
                           ++tpTilesInd;
                       }
                       if(!found){ tpTiles.add(new TeleportTiles()); }
                   }

                   tpTiles.get(tpTilesInd).addTile(x, y, tpID);
               }
               gridValInd++;
           }
       }

       initGraph();
    }


    /**
     * Initialize the Graph
     */
    public void initGraph(){
        ArrayList<Coordinate> [][] nPos = new ArrayList[cols][rows];   // Neighbors position in the 2D array of

        // Instantiate the nPos array
        for(int x = 0; x < cols; x++){ for(int y = 0; y < rows; y++){ nPos[x][y] = new ArrayList<Coordinate>(); } }

        // Init tiles1D and nPos array's to contain valid information
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++){

                // impassable weights don't have neighbors
                if(tiles[x][y].weight == Tile.INPASSABLE_WEIGHT){ continue; }

                // Determine neighbors
                // left neighbor:
                if( x - 1 >= 0){
                    if(tiles[x - 1][y].weight != Tile.INPASSABLE_WEIGHT){
                        nPos[x][y].add(new Coordinate(x-1, y));
                    }
                }

                // right neighbor:
                if( x + 1 < cols){
                    if(tiles[x + 1][y].weight != Tile.INPASSABLE_WEIGHT) {
                        nPos[x][y].add(new Coordinate(x + 1, y));
                    }
                }

                // below neighbor:
                if( y - 1 >= 0){
                    if(tiles[x][y - 1].weight != Tile.INPASSABLE_WEIGHT){
                        nPos[x][y].add(new Coordinate(x, y - 1));
                    }
                }

                // above neighbor:
                if( y + 1 < rows){
                    if(tiles[x][y + 1].weight != Tile.INPASSABLE_WEIGHT){
                        nPos[x][y].add(new Coordinate(x, y + 1));
                    }
                }

                // teleport neighbors:
                for(TeleportTiles tmpTp: tpTiles){
                    if(tmpTp.checkXY(x, y)){
                        int [] tpNeighbor = tmpTp.getXY(x, y);
                        nPos[x][y].add(new Coordinate(tpNeighbor[0], tpNeighbor[1]));
                    }
                }
            }
        }
        graph = new Graph(tiles, nPos, rows, cols);
    }


    /**
     * Converts a x & y-coordinate, to the corresponding 1-D coordinate
     * @param x x-coordinate
     * @param y y-coordinate
     * @return 1-D coordinate
     */
    public int convertTo1D(int x, int y){ return (y * cols) + x; }


    /**
     * Update the tileWidth and tileHeight to reflect changes made to the program window
     */
    public void update(){
        tileWidth = (float)Gdx.graphics.getWidth()/ (float)cols;
        tileHeight = (float)Gdx.graphics.getHeight()/ (float)rows;

        if(tileWidth < 1) tileWidth = 1;
        if(tileHeight < 1) tileHeight = 1;
    }


    /**
     * Draw the Grid
     * @param sr ShapeRenderer used to draw
     */
    public void draw(ShapeRenderer sr){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                if((src != null && tiles[x][y].tileID.equals(src.tileID)) ||
                        (trgt != null && tiles[x][y].tileID.equals(trgt.tileID))){
                    tiles[x][y].draw(sr, x, y, tileWidth, tileHeight, true);
                    continue;
                }
                tiles[x][y].draw(sr, x, y, tileWidth, tileHeight, false);
            }
        }
    }
}
