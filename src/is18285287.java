package is18289592;



import javax.swing.*; // JOptionPane library
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class is18285287{

    private static JFrame frame;
    private static int size = 8;
    private static ArrayList<Integer> init;
    private static ArrayList<Integer> goal;
    private static int dimension;

    /*****************************
     * 
     * setUpGame begins the game, calls getStateFromUserInput to populate initial and goal states
     * 
     *****************************/
    private static void setUpGame()
    {
        frame = new JFrame();
        String initMessage = "Hello, please enter a start state.";
        init = getStateFromUserInput(initMessage);
        String goalMessage = "Now, enter a final state.";
        goal = getStateFromUserInput(goalMessage);
		dimension = (int) Math.sqrt(size+1);
        
    }
    
    /*****************************
     * 
     * getStateFromUserInput instantiates JFrame windows and retrieves user input
     * 
     * @Parameters:
     * 	message: String indicating what the user should enter. Shows error message if invalid input was entered
     * 
     * @Returns:
     * 	retVal: ArrayList populated by user input
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
            // if scanner.hasNextInt(), then there are too many values in the input string
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
     * uniqueIntegers validates that the arr is an ArrayList of unique Integers.
     * Called in getStateFromUserInput
     * 
     * @Parameters:
     * 	arr: Board state to test uniqueness of
     * 
     * @Returns:
     * 	isValid: boolean - true if arr only contains unique integers, false otherwise
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
     * calculateH performs calculation on given testState to estimate the heuristic value h,
     * 	which is the total distance of all pieces in their current state to their goal state
     * 
     * @Parameters:
     * 	testState: the given board state for which h will be calculated
     * 
     * @Returns:
     * 	totalDist: heuristic h, the total distance of all pieces to their goal state from state testState
     * 
     *****************************/
    private static int calculateH(ArrayList<Integer> testState){
		// totalDist cumulates the distance of each tile, giving h
		int totalDist = 0;
		
		for(int i = 1; i < testState.size(); i++){
			// find the index of tile i in the current state
			int curIdx = testState.indexOf(i);
			
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
     * @Parameters:
     * 	idx: the index of the tile to be calculated
     * 
     * @Returns:
     * 	row of idx, assuming topmost row is 0
     * 
     *****************************/
    public static int getRow(int idx){
    	return idx / dimension;
    }
    
    /*****************************
     * 
     * getCol calculates the column of the given index idx based on the dimensions of the game
     * 
     * @Parameters:
     * 	idx: the index of the tile to be calculated
     * 
     * @Returns:
     * 	column of idx, assuming leftmost column is 0
     * 
     *****************************/
    public static int getCol(int idx){
    	return idx % dimension;
    }
    
    /*****************************
     * 
     * getMoves takes a given board state and identifies legal moves
     * then calls calculateH to find the heuristic h for the move
     * based on the global goal state, goal
     * 
     * @Parameters:
     * 	currentState: the state whose legal moves will be identified
     * 
     *****************************/
    public static void getMoves(ArrayList<Integer> currentState){
    	int idx = currentState.indexOf(0);
    	int emptyRow = getRow(idx);
    	int emptyCol = getCol(idx);
    	
    	// above
    	int above = emptyRow - 1; // move is legal if above >= 0
    	if(above >= 0){
    		System.out.println(currentState.get(idx - dimension) + " to the south");
    		ArrayList<Integer> heuristicList = move(currentState, idx, idx - dimension);
    		System.out.println("h: " + calculateH(heuristicList));
    	}
    	
    	// below
    	int below = emptyRow + 1; // move is legal if below < dimension
    	if(below < dimension){
    		System.out.println(currentState.get(idx + dimension) + " to the north");
    		ArrayList<Integer> heuristicList = move(currentState, idx, idx + dimension);
    		System.out.println("h: " + calculateH(heuristicList));
    	}
    	
    	// left
    	int left = emptyCol - 1; // move is legal if left >= 0
    	if(left >= 0){
    		System.out.println(currentState.get(idx-1) + " to the east");
    		ArrayList<Integer> heuristicList = move(currentState, idx, idx - 1);
    		System.out.println("h: " + calculateH(heuristicList));
    	}
    	
    	// right
    	int right = emptyCol +1; // move is legal if right < dimension
    	if(right < dimension){
    		System.out.println(currentState.get(idx+1) + " to the west");
    		ArrayList<Integer> heuristicList = move(currentState, idx, idx + 1);
    		System.out.println("h: " + calculateH(heuristicList));
    	}
    }

    /*****************************
     * 
     * move performs the game move on the board state currentMove
     * 
     * @Parameters:
     * 	currentMove: the current board state
     * 	curIdx: the current index of the empty space
     * 	newIdx: the index of the piece which is being moved
     * 
     * @Returns:
     * 	nextMove: a boardstate where the move indicated by curIdx and newIdx was made
     * 
     *****************************/
    // curIdx is the current index of the 0 tile. newIdx is the new index of the 0 tile in the next move
    private static ArrayList<Integer> move(ArrayList<Integer> currentMove, int curIdx, int newIdx)
    {
        ArrayList<Integer> nextMove = new ArrayList<>(currentMove);
        Collections.swap(nextMove, curIdx, newIdx);
        return nextMove;
    }

    public static void main(String args[])
    {
        setUpGame();
		
        getMoves(init);
    }
}