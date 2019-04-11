/**************************************
 *
 * N-Puzzle Solver
 *
 * By: Alexis Johnson		18285287
 *     Victor Guerra		18289592
 *     Sabelle O'Connell	18274471
 *
 * For CS4006 Intelligent Systems
 *     University of Limerick
 *     10 April 2019
 *
 * This is the final submission for the N-Puzzle Solver for CS4006 at UL.
 * This version has a user input the size of the puzzle as either 8 or 15,
 *  then accepts user input of an initial and goal state,
 * 	then validates the inputs as unique and consecutive starting at 0.
 * From the user inputted states, this program identifies all possible
 * 	moves which can be made next, and calculates the heuristic value for h
 * 	for each possible move, where h represents the sum of the distances of
 * 	each tile out of place, and the function g which represents the cost of the
 *  path from the start point.
 * The program then calculates f as the sum of g and h, associates this f
 * 	with the theoretical board state, and considers all potential moves.
 * It selects the move with the lowest f, and uses the A* algorithm to
 * 	traverse potential moves until the puzzle is solved. In the event that
 * 	the puzzle is unsolvable, an error message is printed to standard output after exhausting all moves
 * 	(traversing the entire search space).
 *
 * For the user inputted states, 0 represents the gap tile in the N-puzzle.
 *
 **************************************/

import javax.swing.*; // JOptionPane library
import java.util.*;

public class is18285287{

    // jFrame object for getting input
    private static JFrame frame;
    // size of the game (8 or 15)
    private static int size;
    // dimension of the board in one direction (3 or 4)
    private static int dimension;
    // initial state, represented as a list of integers 0 - size
    // conceptually, this represents a dimension x dimension board with index 0 in the top left corner,
    // as shown in the assignment criteria
    private static ArrayList<Integer> init;
    // final state, represented as a list of integers 0 - size
    private static ArrayList<Integer> goal;

    /*****************************
     *
     * setUpGame begins the game,
     * calls getSizeFromUserInput to find puzzle size,
     * calls getStateFromUserInput to populate initial and goal states
     *
     *****************************/
    private static void setUpGame()
    {
        frame = new JFrame();
        getSizeFromUserInput();
        String initMessage = "Please enter a start state.";
        init = getStateFromUserInput(initMessage);
        String goalMessage = "Now, enter a final state.";
        goal = getStateFromUserInput(goalMessage);

        // define dimension based on size
        dimension = (int) Math.sqrt(size+1);
    }

    /*****************************
     *
     * getStateFromUserInput instantiates JFrame windows and retrieves user input
     *
     * @param message	string indicating what the user should enter. Shows error message if invalid input was entered
     *
     * @return retVal ArrayList populated by user input
     *
     *****************************/
    private static ArrayList<Integer> getStateFromUserInput(String message)
    {
        String instructions = String.format(" This should be entered as a unique sequence of the numbers 0 through %d, with 0 indicating the empty tile. " +
                "All numbers should be separated by a space.", size);
        String input = JOptionPane.showInputDialog(frame, message + instructions);
        // return value is an ArrayList of Integers that represents either the initial state or the goal state
        ArrayList<Integer> retVal = new ArrayList<>();

        // input has not yet been validated
        boolean inputValidated = false;

        // continue checking new inputs until a valid input has been entered
        while (!inputValidated)
        {
            // use scanner to parse integers from input string
            Scanner scanner = new Scanner(input);
            // clear ArrayList so that the new input can be parsed into Integers
            retVal.clear();
            // count maintains the number of Integers in the retVal ArrayList
            int count = 0;

            //parse the first size+1 integers in the string into retVal (this should be all of them if the input is valid)
            while (scanner.hasNextInt() && count < size + 1)
            {
                retVal.add(scanner.nextInt());
                count++;
            }

            // if count != size+1, then there are not enough values in the input string
            // if scanner.hasNext(), then there are too many values in the input string
            // if !uniqueIntegers(retVal), then the input values are not unique numbers from 0 to size
            if (count != size + 1 || scanner.hasNext() || !uniqueIntegers(retVal))
            {
                input = JOptionPane.showInputDialog(frame, "Your input was not valid. Please re-enter the state. " + instructions);
            }

            else
            {
                inputValidated = true;
            }
            scanner.close();
        }
        return retVal;
    }

    /*****************************
     *
     * getSizeFromUserInput instantiates JFrame window that allows user to choose puzzle size.
     *
     *****************************/
    private static void getSizeFromUserInput()
    {
        String input = JOptionPane.showInputDialog(frame, "Hello, please enter the number corresponding to which puzzle size you would like to solve (8 or 15)");

        // input has not yet been validated
        boolean inputValidated = false;

        // continue checking new inputs until a valid input has been entered
        while (!inputValidated)
        {
            // use scanner to parse integers from input string
            Scanner scanner = new Scanner(input);

            // check for valid int input
            if (scanner.hasNextInt())
            {
                int val = scanner.nextInt();
                // if this was the only value entered AND it equals 8 or 15, the input is valid
                if (!scanner.hasNext() && (val == 8 || val == 15))
                {
                    inputValidated = true;
                    size = val;
                }
            }
            // if invalid input, ask the user for new input
            if (!inputValidated)
            {
                input = JOptionPane.showInputDialog(frame, "Your input was not valid. Please re-enter either 8 or 15 for your puzzle size.");
            }
            scanner.close();
        }
    }

    /*****************************
     *
     * uniqueIntegers validates that the arr is an ArrayList of unique Integers for start and final states
     *
     * @param 	arr	board state to test uniqueness of input int values
     *
     * @return	isValid true if arr only contains unique integers, false otherwise
     *
     *****************************/
    private static boolean uniqueIntegers(ArrayList<Integer> arr)
    {
        HashSet<Integer> set = new HashSet<>();
        int i = 0;
        boolean isValid = true;
        while (i<size+1 && isValid)
        {
            Integer cur = arr.get(i);
            if (set.contains(cur) || cur<0 || cur>size)
            {
                isValid = false;
            }
            else
            {
                set.add(cur);
            }
            i++;
        }
        return isValid;
    }

    /*****************************
     *
     * calculateH performs calculation on given parameter state to estimate the heuristic value h,
     * 	which is the total distance of all pieces in their current state to their goal state
     *
     * @param 	state	the given board state for which h will be calculated
     *
     * @return	totalDist heuristic h, the total distance of all pieces to their goal state from state testState
     *
     *****************************/
    private static int calculateH(ArrayList<Integer> state){
        // totalDist accumulates the distance of each tile, giving h
        int totalDist = 0;

        for(int i = 1; i < state.size(); i++){
            // find the index of tile i in the current state
            int curIdx = state.indexOf(i);

            // find the index of tile i in the goal state
            int goalIdx = goal.indexOf(i);

            // a piece's column is given by its index in the array % the number of columns,
            // so number of horizontal moves is given by the difference between the current and goal columns
            int horizMoves = Math.abs((getCol(curIdx)) - (getCol(goalIdx)));

            // a piece's row is given by its index in the array / the number of rows,
            // so number of vertical moves is given by the difference between the current and goal rows
            int vertMoves = Math.abs((getRow(curIdx)) - (getRow(goalIdx)));

            totalDist += horizMoves + vertMoves;
        }
        return totalDist;
    }

    /*****************************
     *
     * getRow calculates the row of the given index idx based on the dimensions of the game
     *
     * @param 	idx	the index of the tile to be calculated
     *
     * @return	row of idx, assuming topmost row is 0
     *
     *****************************/
    private static int getRow(int idx){
        return idx / dimension;
    }

    /*****************************
     *
     * getCol calculates the column of the given index idx based on the dimensions of the game
     *
     * @param 	idx	the index of the tile to be calculated
     *
     * @return	column of idx, assuming leftmost column is 0
     *
     *****************************/
    private static int getCol(int idx){
        return idx % dimension;
    }

    /*****************************
     *
     * aStarAlgorithm performs the A* algorithm given by the following steps:
     * 	1) Priority Queues for board state nodes closed and open are created
     * 	2) The initial state is added to the open list
     * 	3) A loop begins which is repeated until the open list is empty. During this loop:
     * 		a) The current state to be examined is grabbed from the open list
     * 		b) The current state is tested, if it is the goal state the current state is returned
     * 		c) A list containing all successors to the current board state is created, and for each successor:
     * 			i) The open and closed lists are tested to see if this state has been found in a shorter path
     * 			ii) So long as it has not, the successor is added to the open list with the heuristic values calculated here
     * 		d) Then, once each successor has been added to the open list, the current state is added to the closed list
     * 	4) If the loop completes without reaching a goal state, the method returns null, indicating an unsolvable puzzle
     *
     * @return	current	returns the current board state when the current board state reaches the goal state
     * @return	null	returns null when the initial board state cannot reach the goal state
     *
     *****************************/
    private static BoardStateNode aStarAlgorithm()
    {
        // customized priority queue for BoardStateNode objects
        BSNPriorityQueue open = new BSNPriorityQueue();
        BSNPriorityQueue closed = new BSNPriorityQueue();
        // add initial state to the open list
        open.add(new BoardStateNode(init, null, calculateH(init), 0));

        while (!open.isEmpty())
        {
            // get state with lowest f off the front of the priority queue
            BoardStateNode current = open.poll();
            if (current.state.equals(goal))
            {
                // goal found
                return current;
            }

            // else, generate successors from current
            List<ArrayList<Integer>> successors = current.getSuccessors();
            // iterate over successors
            for (ArrayList<Integer> nextState : successors)
            {
                // g value of nextState from the current path
                int g = current.g + 1;
                // check if open contains this state. if it does, see if it has the lower g value
                boolean openContainsBetterHeuristic = open.contains(nextState) && open.getNodeForState(nextState).g <= g;
                // check if closed contains this state. if it does, see if it has the lower g value
                boolean closedContainsBetterHeuristic = closed.contains(nextState) && closed.getNodeForState(nextState).g <= g;

                // if neither the open or closed list have a better path for this state, remove it from closed and re-add it to open
                // using the new heuristic value
                if (!openContainsBetterHeuristic && !closedContainsBetterHeuristic)
                {
                    open.remove(nextState);
                    closed.remove(nextState);
                    open.add(new BoardStateNode(nextState, current, calculateH(nextState), g));
                }
            }
            // add current to closed list
            closed.add(current);
        }
        // if this reached, all possible states were searched and the solution was not found. the puzzle is therefore unsolvable
        return null;
    }

    /*****************************
     *
     * printState creates a string representing the given board state
     * which is nicely readable by a person and represents the
     * board state in its two-dimensional form.
     *
     * @param	node	the board state which will be printed
     *
     * @return result	representation of 2d board
     *
     *****************************/
    private static String printState(BoardStateNode node)
    {
        String result = "H: " + node.h + ", G: " + node.g + ", F: " + node.getF() + "\n";
        for (int i = 0; i<size+1; i+= dimension)
        {
            for (int j=i; j<i + dimension; j++)
            {
                result += node.state.get(j) + "   ";
            }
            result += "\n";
        }
        result += "\n\n";
        return result;
    }


    public static void main(String args[])
    {
        setUpGame();
        BoardStateNode solution = aStarAlgorithm();
        String printedSolution = "";
        BoardStateNode current = solution;

        if (current == null)
        {
            System.out.println("No solution.");
        }
        else
        {
            // iterate through each nodes previous state to construct the path from init to goal
            while(current!= null)
            {
                printedSolution = printState(current) + printedSolution;
                current = current.prev;
            }

            System.out.println("Results: \n" + printedSolution);
        }
    }


    /*****************************
     *
     * A subclass BoardStateNode is created which implements Comparable, which will allow the nodes to be used in a PriorityQueue
     * A BoardStateNode will have:
     * 	an ArrayList state, which is the order of the pieces in the puzzle,
     * 	a BoardStateNode prev, which is the state of this node's parent,
     * 	an int h, which is its heuristic h value,
     * 	an int g, which is its heuristic g value.
     *
     * BoardStateNode methods:
     *
     * 	getSuccessors:
     * 		generates a list of all successors for the given state, which represent potential moves.
     * 		this only generates the possible board states, and not their heuristic values
     *
     * 	getF:
     * 		calculates the given state's heuristic f
     *
     * 	compareTo:
     * 		when comparing BoardStateNodes, compareTo will only compare them based on their heuristic f
     *
     * 	switchTiles:
     * 		generates a board state where a given tile is moved to a new place
     *
     *****************************/
    public static class BoardStateNode implements Comparable
    {
        public ArrayList<Integer> state;
        public BoardStateNode prev; // previous state -> used to trace path from init to goal
        public int h;
        public int g;

        public BoardStateNode(ArrayList<Integer> state, BoardStateNode prev, int h, int g)
        {
            this.state = state;
            this.prev = prev;
            this.h= h;
            this.g= g;
        }

        /*****************************
         *
         * 	generates a list of all successors for the given state, which represent potential moves.
         * 	this only generates the possible board states, and not their heuristic values
         *  @return list	list of board states which can be reached from the given board state
         *
         *****************************/
        public List<ArrayList<Integer>> getSuccessors()
        {
            int emptyIdx = state.indexOf(0);
            int emptyRow = getRow(emptyIdx);
            int emptyCol = getCol(emptyIdx);

            List<ArrayList<Integer>> list = new ArrayList<>();

            // above
            if(emptyRow - 1 >= 0)
            {
                // see what the state would be if we switched 0 with the tile above it
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx - dimension);
                // confirm that this isn't the move we just made (i.e. switching a piece back to where it just was)
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // below
            if(emptyRow + 1 < dimension)
            {
                // see what the state would be if we switched 0 with the tile below it
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx + dimension);
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // left
            if(emptyCol - 1 >= 0)
            {
                // see what the state would be if we switched 0 with the tile to the left
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx - 1);
                if (this.prev== null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // right
            if(emptyCol +1 < dimension)
            {
                // see what the state would be if we switched 0 with the tile to the right
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx + 1);
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            return list;
        }

        /*****************************
         *
         * 	calculates the given state's heuristic f
         * 	@return	f: sum of h and g
         *
         *****************************/
        public int getF()
        {
            return h+g;
        }

        /*****************************
         *
         * 	when comparing BoardStateNodes, compareTo will only compare them based on their heuristic f
         * 	@param	x	the board state that the current state, y, will be compared to
         * 	@return	the value 0 if y == x;a value less than 0 if y is less than x; and a value greater than 0 if y is greater than x
         *
         *****************************/
        public int compareTo(Object x)
        {

            return Integer.compare(this.getF(), ((BoardStateNode)x).getF());
        }

        /*****************************
         *
         * 	generates a board state where a given tile is moved to a new place
         * 	@param	currentMove	the board state before a move is made
         * 	@param	curIdx		the index of the tile which will be moved
         * 	@param	newIdx		the index of the new location of the tile
         * 	@return	nextMove	a board state where the title at index curIdx has been moved to the index newIdx
         *
         *****************************/
        private ArrayList<Integer> switchTiles(ArrayList<Integer> currentMove, int curIdx, int newIdx)
        {
            ArrayList<Integer> nextMove = new ArrayList<>(currentMove);
            Collections.swap(nextMove, curIdx, newIdx);
            return nextMove;
        }
    }

    /*****************************
     *
     * A subclass BSNPriorityQueue is created which implements PriorityQueue.
     * This will allow BoardStateNode objects to be stored in a way conducive to the open and closed lists
     * in the A* algorithm. 
     *
     * A BSNPriorityQueue will be a PriorityQueue.
     *
     * BSNPriorityQueue methods:
     *
     * 	contains:
     * 		checks the given BSNPriorityQueue to see if it contains the parameterized board state
     *
     * 	getNodeForState:
     * 		searches the BSNPriorityQueue for a board state, and returns it if it exists. Otherwise, returns null
     *
     * 	remove:
     * 		removes a BoardStateNode with a given state from the BSNPriorityQueue
     *
     *****************************/
    public static class BSNPriorityQueue extends PriorityQueue<BoardStateNode>
    {

        // constructor
        public BSNPriorityQueue()
        {
            new PriorityQueue<>();
        }

        /*****************************
         *
         * 	checks the given BSNPriorityQueue to see if it contains the parameterized board state
         * 	@param	state	the board state to test if it exists in the BSNPriority Queue
         * 	@return	true or false, true if the queue contains the state, false otherwise
         *
         *****************************/
        public boolean contains(ArrayList<Integer> state)
        {
            Iterator it = this.iterator();
            if (state == null)
            {
                return false;
            }
            while(it.hasNext())
            {
                BoardStateNode node = (BoardStateNode) it.next();
                if (node != null && node.state != null && node.state.equals(state))
                {
                    return true;
                }
            }
            return false;
        }

        /*****************************
         *
         * 	searches the BSNPriorityQueue for a board state, and returns it if it exists. Otherwise, returns null
         * 	@param	state	the board state that will be searched for
         * 	@return	node	the first BoardStateNode in the BSNPriorityQueue with the board state
         * 	@return	null otherwise
         *
         *****************************/
        public BoardStateNode getNodeForState(ArrayList<Integer> state)
        {
            Iterator it = this.iterator();
            if (state == null)
            {
                return null;
            }
            while(it.hasNext())
            {
                BoardStateNode node = (BoardStateNode) it.next();
                if (node != null && node.state != null && node.state.equals(state))
                {
                    return node;
                }
            }
            return null;
        }

        /*****************************
         *
         * 	removes a BoardStateNode with a given state from the BSNPriorityQueue
         * 	@param state	the board state whose BoardStateNode will be removed
         * 	@return	true when the node was found and removed
         * 	@return	false otherwise
         *
         *****************************/
        public boolean remove(ArrayList<Integer> state)
        {
            Iterator it = this.iterator();
            if (state == null)
            {
                return false;
            }
            while(it.hasNext())
            {
                BoardStateNode node = (BoardStateNode) it.next();
                if (node != null && node.state != null && node.state.equals(state))
                {
                    this.remove(node);
                    return true;
                }
            }
            return false;
        }
    }
}
