package com.toni.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Stack;

public class Graph {

    // Container for each Tile in the grid
    public static class Vertex {
        private Tile elem;            // The element contained within this entry
        private Tile [] neighborhood; // Array of Vertices with an edge to this Tile
        private double dist;          // Measure's the current Distance between this Vertex and the source
        private Vertex prev;          // Store current path to source in memory

        /**
         * Constructor for Vertex class
         * @param elem current Tie element
         * @param neighborhood Tile's with an edge to the current Tile element
         */
        private Vertex(Tile elem, Tile [] neighborhood){
            this.elem = elem;                 // Tile element
            this.neighborhood = neighborhood; // Neighboring URL elements
        }

        /**
         * @return Tile element contained in Vertex
         */
        public Tile getValue(){ return elem; }

        /**
         * @return Array of Tiles, neighboring the current Tile element
         */
        public Tile[] getNeighborhood(){ return neighborhood; }
    }

    private Vertex [][] graphElements; // 2-D array list
    int rows, cols;                    // Specifies the rows and cols of a graph

    /**
     * Create a Graph from an array of Tile's and the indices of the Tile's neighbors
     * @param tiles All Tiles in the Grid
     * @param nArr Indices of the Tile's neighbors
     */
    public Graph(Tile [][] tiles, ArrayList<Coordinate> [][] nArr, int rows, int cols){
        this.rows = rows;                        // Number of rows in the 2D array
        this.cols = cols;                        // Number of columns in the 2D array
        Vertex tVertex;                          // Temporary Entry;
        graphElements = new Vertex[cols][rows];  // Instantiate the 2D graph element array

        // Initialize Vertex containers of Tiles, each Vertex containing a Tile, and the Tile's neighborhood
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++) {
                // Add the Vertex to the graphElements
                tVertex = new Vertex(tiles[x][y], toTileArray(nArr[x][y], tiles));
                graphElements[x][y] = tVertex;
            }
        }

        this.rows = rows;           // Number of rows in the graph
        this.cols = cols;           // Number of cols in the graph
    }

    /**
     * Convert a Tile ArrayList into a Tile array
     * @param arrayList of Tile indices
     * @param tiles Array of Tiles
     * @return Array of Tiles, indexed by arrayList
     */
    public static Tile[] toTileArray(ArrayList<Coordinate> arrayList, Tile [][] tiles){
        Tile [] convertedArray = new Tile[arrayList.size()]; // New Tile array, for the neighborhood to be inserted into
        // Transfer ArrayList into the 2D array
        for(int i = 0; i < convertedArray.length; i++){
            convertedArray[i] = tiles[arrayList.get(i).getX()][arrayList.get(i).getY()];
        }
        return convertedArray;
    }


    /**
     * Given a source and a target, will determine the shortest path between these two Tiles. Shortest-Path is
     * determined by a* pathfinding algorithm
     *
     * @param source Tile object, where the path will start
     * @param target Tile objects, where the path will end
     * @return Shortest-Path
     */
    public Stack<Vertex> aStar(Tile source, Tile target) throws Exception{
        FibonacciHeap<Vertex> open = new FibonacciHeap<>(); // The set of nodes to be evaluated
        HashSet<Tile> elemInOpen   = new HashSet<Tile>();      // The set of nodes in open
        HashSet<Tile> closed       = new HashSet<Tile>();      // The set of nodes to be evaluated
        Stack<Vertex> path         = new Stack<Vertex>();        // Return Variable, stack of the shortest-path Vertex
        Vertex cVertex;                                     // Current Vertex
        Vertex nVertex;                                     // Neighbor Vertex
        double altDist;                                     // Elements new distance
        Coordinate nCoor;                                   // Neighbor coordinate
        int [] tCoor = new int[2];                          // Target coordinates

        if(source.weight == Tile.INPASSABLE_WEIGHT || target.weight == Tile.INPASSABLE_WEIGHT){ return null; }

        // Prepare the graph elements for a* algorithm
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++) {
                cVertex = graphElements[x][y];  // Temporarily store the Vertex, for editing

                // Branch-Statement: if the Vertex matches the source apply a special condition,
                //                   otherwise apply the default
                if (equals(cVertex, source)) {
                    // Source special condition
                    for(int x1 = 0; x1 < cols; x1++){
                        for(int y1 = 0; y1 < rows; y1++){
                            // Find targets graphElements index
                            if(equals(graphElements[x][y], target)){
                                tCoor[0] = x1;
                                tCoor[1] = y1;
                                // Determine the distance between the start and finish positions
                                cVertex.dist = distance(new Coordinate(x, y), new Coordinate(tCoor[0], tCoor[1]));
                            }

                        }
                    }

                    cVertex.prev = null;
                    open.enqueue(cVertex, cVertex.dist);
                    elemInOpen.add(cVertex.getValue());
                } else {
                    // Default condition
                    cVertex.dist = Integer.MAX_VALUE;
                    cVertex.prev = null;
                }

                // Update the graph elements
                graphElements[x][y] = cVertex;
            }
        }


        while(true){
            cVertex = open.dequeueMin().getValue();      // currVertex = node in open with the lowest f_cost
            elemInOpen.remove(cVertex.getValue());       // Remove currVertex from open
            closed.add(cVertex.elem);                    // add currVertex to the closed collection

            // If current vertex equals the target the path has been found
            if(equals(cVertex, target)){
                if(cVertex.prev != null || equals(cVertex, source)){
                    while(cVertex != null){
                        path.push(cVertex);
                        cVertex = cVertex.prev;
                    }
                }
                return path;
            }

            for(Tile nTile: cVertex.getNeighborhood()){
                // If the neighbor is in closed skip to the next neighbor
                if(closed.contains(nTile)){ continue; }
                nCoor = coorOfGElements(nTile);
                nVertex = graphElements[nCoor.getX()][nCoor.getY()];

                // Determine the altDistance of the neighborTile
                altDist = cVertex.dist + nTile.weight + distance(nCoor, new Coordinate(tCoor[0], tCoor[1]));

                // if new path to neighbor is shorter OR neighbor is not in open
                if(nVertex.dist > altDist | !elemInOpen.contains(nTile)){
                    nVertex.dist = altDist; // Set f_cost of neighbor
                    nVertex.prev = cVertex; // Set parent of neighbor

                    // Update graphElements
                    graphElements[nCoor.getX()][nCoor.getY()] = nVertex;

                    if(elemInOpen.contains(nTile)){
                        try {
                            open = decreasePriority(nVertex, open);
                        } catch(NoSuchElementException ex){
                            ex.printStackTrace();
                            return null;
                        }
                    } else {
                        // Add the neighbor to open
                        open.enqueue(nVertex,nVertex.dist);
                        elemInOpen.add(nVertex.getValue());
                    }
                }
            }
        }
    }


    /**
     * Given a source and a target, will determine the shortest path between these two Tiles. Shortest-Path is
     * determined by Dijkstra's algorithm.
     *
     * @param source Tile object, where the path will start
     * @param target Tile objects, where the path will end
     * @return Shortest-Path
     */
    public Stack<Vertex> dijkstra(Tile source, Tile target){
        FibonacciHeap<Vertex> priorityQueue = new FibonacciHeap<Vertex>();       // Fibonacci Heap used as priority queue.
        Stack<Vertex> path = new Stack<Vertex>();                                // Return Variable, stack of the shortest
                                                                            // path between the objects.
        Vertex tVertex;                                                     // Temp Vertex.
        Vertex nVertex;                                                     // Neighbor Vertex.
        double altDist;                                                     // Used to store the alternative distance
                                                                            // from the source.
        Coordinate nCoor;                                                   // Temp index of the neighbors position in
                                                                            // graphElements.

        if(source.weight == Tile.INPASSABLE_WEIGHT || target.weight == Tile.INPASSABLE_WEIGHT){ return null; }

        // Prepare the graph elements for Dijkstra's algorithm
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++){
                tVertex = graphElements[x][y]; // Temporarily store the Vertex, for editing

                // Branch-Statement: if the Vertex matches the source apply a special condition, otherwise apply the default
                if(equals(tVertex, source)){
                    // Source special condition
                    tVertex.dist = 0;
                    tVertex.prev = null;
                } else {
                    // Default condition
                    tVertex.dist = Integer.MAX_VALUE;
                    tVertex.prev = null;
                }

                // Update the graph elements
                graphElements[x][y] = tVertex;

                // Add the temporary Vertex to the priority queue
                priorityQueue.enqueue(tVertex, tVertex.dist);
            }
        }

        while(!priorityQueue.isEmpty()){
            // Retrieve Vertex with shortest path to the previous element
            tVertex = priorityQueue.dequeueMin().getValue();

            // If the vertex is equal to the target, build and return the path between source and target
            if(equals(tVertex, target)){
                if(tVertex.prev != null || equals(tVertex, source)){
                    while(tVertex != null){
                        path.push(tVertex);
                        tVertex = tVertex.prev;
                    }
                }
                return path;
            }

            // Determine the distance between the current element and the current elements neighbors
            for(Tile tile: tVertex.neighborhood){
                nCoor = coorOfGElements(tile);
                nVertex = graphElements[nCoor.getX()][nCoor.getY()];
                altDist = tVertex.dist + nVertex.getValue().weight;

                // If the alternate distance is less than the original Vertex distance, update the graph elements and
                // priority queue
                if(altDist < nVertex.dist){
                    // Update the neighbors Vertex to reflect the new shortest path
                    nVertex.dist = altDist;
                    nVertex.prev = tVertex;

                    // Update graphElements
                    graphElements[nCoor.getX()][nCoor.getY()] = nVertex;

                    // Update the priority queue
                    try {
                        priorityQueue = decreasePriority(nVertex, priorityQueue);
                    } catch(NoSuchElementException ex){
                        return null;
                    }
                }
            }
        }
        return null; // Return null if target not found
    }


    /**
     * Dequeue the min element in the queue until the min element matches the argument Vertex. Than re-add all the
     * elements back into the Fibonacci Heap, with the updated Vertex. Return the updated Fibonacci Heap.
     * @param aVertex Vertex with a priority to update
     * @param tHeap The Fibonacci Heap that needs to be updated
     * @return The updated Fibonacci Heap
     */
    private FibonacciHeap<Vertex> decreasePriority(Vertex aVertex, FibonacciHeap<Vertex> tHeap){
        // Create a stack and a Entry object to temporarily hold the elements in the FibonacciHeap
        Stack<Vertex> tStack = new Stack<Vertex>();
        FibonacciHeap.Entry<Vertex> tEntry;  // Temporary Entry
        Vertex tVertex;                      // Temporary Vertex
        boolean flag = true;                 // Flag that controls the while-loop below

        // Retrieve's the
        while(flag){
            // Get elements from the heap
            try {
                tEntry = tHeap.dequeueMin();
            } catch(NoSuchElementException ex){
                throw new NoSuchElementException("Heap is empty.");
            }
            // If the Entry matches the vertex
            if(tEntry.getValue().getValue().tileID.equals(aVertex.getValue().tileID)){
                flag = false;
                tStack.push(aVertex);
            } else { tStack.push(tEntry.getValue()); }
        }

        // Re-add each vertex removed from the heap
        while(!tStack.isEmpty()){
            tVertex = tStack.pop();
            tHeap.enqueue(tVertex, tVertex.dist);
        }

        // Return the updated heap
        return tHeap;
    }

    /**
     * Determine a graphElements index x & y-coordinate
     * @param index the index of the element that's being searched for in the graph
     * @return the XY coordinate of the graph element
     */
    /*
    private int [] getXYCoordinate(int index){
        int [] xy = new int [2];

        // Determine the y-coordinate
        for(int i = 0; i < rows; i++) {
            if (i * cols <= index && index > (i + 1) * cols) { xy[1] = i; }
        }

        xy[0] = index - cols * xy[1];
        return xy;
    }*/

    /**
     * Pythagorean theorem to determine the distance between any two XY coordinates
     *
     * @param cA xy-coordinates of node A
     * @param cB xy-coordinates of nobe B
     * @return returns the distance between any 2 points
     */
    public double distance(Coordinate cA, Coordinate cB){
        double a2, b2, c2; // a^2 + b^2 = c^2

        // Determine the square of adjacent side
        a2 = 5 * (cA.getX() - cB.getX());
        a2 = a2 * a2;
        System.out.print("\ncoordinate-1 (" + cA.getX() + ", " + cA.getY() + ")\ncoordinate-2 (" + cB.getX() + ", " +
                cB.getY() + ")\n a^2: " + a2);


        // Determine the square of opposite side
        b2 = 5 *  (cA.getY() - cB.getY());
        b2 = b2 * b2;

        System.out.print(" b^2: " + b2);

        // Determine square of the hypotenuse
        c2 = a2 + b2;
        System.out.print(" c^2: " + c2 + " return Value: " + (Math.sqrt(c2)));


        // Return the hypotenuse
        return Math.sqrt(c2);
    }


    /**
     * Returns coordinate of the Tile if found in the graphElements
     * @param tile Tile being searched for
     * @return Coordinate of Tile in graphElements, or null if not found
     */
    private Coordinate coorOfGElements(Tile tile){
        // Travers through graphElements to look for the Tile
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                if(tile.tileID.equals(graphElements[x][y].getValue().tileID)){ return new Coordinate(x,y); }
            }
        }
        // Return null, if Tile object is not found in the graphElement 2D array
        return null;
    }


    /**
     * Return true if a Vertex's element matches the parameter tile
     * @param tVertex vertex object to compare
     * @param tile tile object to compare
     * @return True, if the object's share URL paths, otherwise false
     */
    private boolean equals(Vertex tVertex, Tile tile){ return tVertex.getValue().tileID.equals(tile.tileID); }
}
