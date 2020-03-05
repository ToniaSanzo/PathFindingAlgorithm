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

    private ArrayList<Vertex> graphElements; // Every element in the graph
    int rows, cols;                           // Specifies the rows and cols of a graph

    /**
     * Create a Graph from an array of Tile's and the indices of the Tile's neighbors
     * @param tiles All Tiles in the Grid
     * @param nArr Indices of the Tile's neighbors
     */
    public Graph(Tile [] tiles, ArrayList<Integer> [] nArr, int rows, int cols){
        graphElements = new ArrayList<Vertex>(); // Instantiate the graphElements collection
        Vertex tVertex;                          // Temporary Entry;

        // Initialize Vertex containers of Tiles, each Vertex containing a Tile, and the Tile's neighborhood
        for(int i = 0; i < tiles.length; i++){
            // Add the Vertex to the graphElements
            tVertex = new Vertex(tiles[i], toTileArray(nArr[i], tiles));
            System.out.print("\ntile[" + i +"] Neighborhood ");
            for(int j = 0; j < nArr[i].size(); j++){
                System.out.print(nArr[i].get(j) + " ");
            }
            graphElements.add(tVertex);
            this.rows = rows;           // Number of rows in the graph
            this.cols = cols;           // Number of cols in the graph
        }
    }

    /**
     * Convert a Tile ArrayList into a Tile array
     * @param arrayList of Tile indices
     * @param tiles Array of Tiles
     * @return Array of Tiles, indexed by arrayList
     */
    public static Tile[] toTileArray(ArrayList<Integer> arrayList, Tile [] tiles){
        Tile [] convertedArray = new Tile[arrayList.size()]; // New Tile array, for the neighborhood to be inserted into
        // Transfer ArrayList into the array
        for(int i = 0; i < convertedArray.length; i++){ convertedArray[i] = tiles[arrayList.get(i)]; }
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
    public Stack<Vertex> aStar(Tile source, Tile target){
        FibonacciHeap<Vertex> open = new FibonacciHeap<>(); // The set of nodes to be evaluated
        HashSet<Tile> elemInOpen   = new HashSet<Tile>();      // The set of nodes in open
        HashSet<Tile> closed       = new HashSet<Tile>();      // The set of nodes to be evaluated
        Stack<Vertex> path         = new Stack<Vertex>();        // Return Variable, stack of the shortest-path Vertex
        Vertex cVertex;                                     // Current Vertex
        Vertex nVertex;                                     // Neighbor Vertex
        double altDist;                                     // Elements new distance
        int nInd;                                           // Neighbor index
        int [] tCoor = new int[2];                          // Target coordinates

        if(source.weight == Tile.INPASSABLE_WEIGHT || target.weight == Tile.INPASSABLE_WEIGHT){ return null; }

        // Prepare the graph elements for a* algorithm
        for(int i = 0; i < graphElements.size(); i++){
            cVertex = graphElements.get(i);  // Temporarily store the Vertex, for editing

            // Branch-Statement: if the Vertex matches the source apply a special condition, otherwise apply the default
            if(equals(cVertex, source)){
                // Source special condition
                for(int j = 0; j < graphElements.size(); j++) {
                    // Find targets graphElements index
                    if(equals(graphElements.get(j), target)) {
                        tCoor = getXYCoordinate(j);
                        // Determine the distance between the start and finish positions
                        cVertex.dist = distance(getXYCoordinate(i), tCoor);
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
            graphElements.remove(i);
            graphElements.add(i, cVertex);
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
                nInd = indexOfGElements(nTile);
                nVertex = graphElements.get(nInd);

                // Determine the altDistance of the neighborTile
                altDist = cVertex.dist + nTile.weight + distance(getXYCoordinate(nInd), tCoor);

                // if new path to neighbor is shorter OR neighbor is not in open
                if(nVertex.dist > altDist | !elemInOpen.contains(nTile)){
                    nVertex.dist = altDist; // Set f_cost of neighbor
                    nVertex.prev = cVertex; // Set parent of neighbor

                    // Update graphElements
                    graphElements.remove(nInd);
                    graphElements.add(nInd, nVertex);



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
        int nInd;                                                           // Temp index of the neighbors position in
                                                                            // graphElements.

        if(source.weight == Tile.INPASSABLE_WEIGHT || target.weight == Tile.INPASSABLE_WEIGHT){ return null; }

        // Prepare the graph elements for Dijkstra's algorithm
        for(int i = 0; i < graphElements.size(); i++){
            tVertex = graphElements.get(i);  // Temporarily store the Vertex, for editing

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
            graphElements.remove(i);
            graphElements.add(i, tVertex);

            // Add the temporary Vertex to the priority queue
            priorityQueue.enqueue(tVertex, tVertex.dist);
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
                nInd = indexOfGElements(tile);
                nVertex = graphElements.get(nInd);
                altDist = tVertex.dist + nVertex.getValue().weight;

                // If the alternate distance is less than the original Vertex distance, update the graph elements and
                // priority queue
                if(altDist < nVertex.dist){
                    // Update the neighbors Vertex to reflect the new shortest path
                    nVertex.dist = altDist;
                    nVertex.prev = tVertex;

                    // Update graphElements
                    graphElements.remove(nInd);
                    graphElements.add(nInd, nVertex);
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
    private int [] getXYCoordinate(int index){
        int [] xy = new int [2];

        // Determine the y-coordinate
        for(int i = 0; i < rows; i++) {
            if (i * cols <= index && index > (i + 1) * cols) { xy[1] = i; }
        }

        xy[0] = index - cols * xy[1];
        return xy;
    }

    /**
     * Pythagorean theorem to determine the distance between any two XY coordinates
     *
     * @param cA xy-coordinates of node A
     * @param cB xy-coordinates of nobe B
     * @return returns the distance between any 2 points
     */
    public double distance(int [] cA, int [] cB){
        double a2, b2, c2; // a^2 + b^2 = c^2

        // Determine the square of adjacent side
        a2 = 5 * (cA[0] - cB[0]);
        a2 = a2 * a2;
        System.out.print("\ncoordinate-1 (" + cA[0] + ", " + cA[1] + ")\ncoordinate-2 (" + cB[0] + ", " + cB[1] +
                ")\n a^2: " + a2);


        // Determine the square of opposite side
        b2 =5 *  (cA[1] - cB[1]);
        b2 = b2 * b2;

        System.out.print(" b^2: " + b2);

        // Determine square of the hypotenuse
        c2 = a2 + b2;
        System.out.print(" c^2: " + c2 + " return Value: " + (Math.sqrt(c2)));


        // Return the hypotenuse
        return Math.sqrt(c2);
    }

    /**
     * Returns index of the Tile if found in the graphElements
     * @param tile Tile being searched for
     * @return index of Tile in graphElements, or -1 if not found
     */
    private int indexOfGElements(Tile tile){
        // Travers through graphElements to look for the Tile
        for(int i = 0; i < graphElements.size(); i++) {
            // If the Tile is found in the graph element array list, return its index
            if (tile.tileID.equals(graphElements.get(i).getValue().tileID)) return i;
        }

        // Return -1, if Tile object is not found in the graphElement arrayList
        return -1;
    }

    /**
     * Return true if a Vertex's element matches the parameter tile
     * @param tVertex vertex object to compare
     * @param tile tile object to compare
     * @return True, if the object's share URL paths, otherwise false
     */
    private boolean equals(Vertex tVertex, Tile tile){ return tVertex.getValue().tileID.equals(tile.tileID); }
}
