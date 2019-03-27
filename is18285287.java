/**************************************
 * 
 * N-Puzzle Solver
 * 
 * By: Alexis Johnson		18285287
 *     Victor Guerra		18289592
 *     Sabelle O'Connell	
 * 
 * For CS4006 Intelligent Systems
 *     University of Limerick
 *     March 27 2019
 * 
 * This is the interim submission for the N-Puzzle Solver
 * This version accepts user input of an initial and goal state,
 * 	and validates the inputs as unique and consecutive starting at 0.
 * The size of the puzzle is declared as an instance variable of the
 * 	class is18285287, and is initialized by default as an 8-puzzle,
 * 	but can be extended to represent a 15 puzzle by initializing the value
 * 	of size to 15.
 * From the user inputted states, this class identifies all possible
 * 	moves which can be made next, and calculates the heuristic value for h
 * 	for each possible move, where h represents the sum of the distances of
 * 	each tile out of place.
 * 
 * For the user inputted states, 0 represents the gap tile in the N-puzzle.
 * 
 **************************************/

import javax.swing.*; // JOptionPane library
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class is18285287 {


	    private static JFrame frame;
	    // note: size can be changed to 15 to extend to 15 puzzle
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

	            // parse the first size+1 integers in the string into retVal (this should be all of them if the input is valid)
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
	            	// prompt user to input new board state
	                input = JOptionPane.showInputDialog(frame, "Your input was not valid. Please re-enter the state. " + instructions);
	            }

	            else
	            {
	            	// each piece is validated to be unique, loop will end
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
	    	// HashSet set used to maintain which array values have already been found
	        HashSet<Integer> set = new HashSet<>();
	        
	        int i = 0;
	        
	        // board state assumed to be valid until repeat elements found
	        boolean isValid = true;
	        
	        // iterate through arr
	        while (i<size+1 && isValid)
	        {
	            Integer cur = arr.get(i);
	            // isValid becomes false when a repeated or invalid integer (too high/too low) is found
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
			// totalDist accumulates the distance of the tiles, giving h
			int totalDist = 0;
			
			// value of i in loop represents tile number
			// loop starts at 1 since 0 is not a tile whose distance contributes to h
			for(int i = 1; i < testState.size(); i++){
				// find the index of tile i in the current state
				int curIdx = testState.indexOf(i);
				
				// find the index of tile i in the goal state
				int goalIdx = goal.indexOf(i);
				
				// horizontal moves is given by the difference between the current and goal columns
				int horizMoves = Math.abs((getCol(curIdx)) - (getCol(goalIdx)));
				
				// vertical moves is given by the difference between the current and goal rows
				int vertMoves = Math.abs((getRow(curIdx)) - (getRow(goalIdx)));
				
				// total distance is the sum of horizontal and vertical moves
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
	    	// move is legal if index of row above >= 0
	    	if(emptyRow - 1 >= 0){
	    		System.out.println(currentState.get(idx - dimension) + " to the south");
	    		ArrayList<Integer> potentialState = switchTiles(currentState, idx, idx - dimension);
	    		System.out.println("h: " + calculateH(potentialState));
	    	}
	    	
	    	// below
	    	// move is legal if index of row below < dimension
	    	if(emptyRow + 1 < dimension){
	    		System.out.println(currentState.get(idx + dimension) + " to the north");
	    		ArrayList<Integer> potentialState = switchTiles(currentState, idx, idx + dimension);
	    		System.out.println("h: " + calculateH(potentialState));
	    	}
	    	
	    	// left
	    	// move is legal if index of column to left >= 0
	    	if(emptyCol - 1 >= 0){
	    		System.out.println(currentState.get(idx-1) + " to the east");
	    		ArrayList<Integer> potentialState = switchTiles(currentState, idx, idx - 1);
	    		System.out.println("h: " + calculateH(potentialState));
	    	}
	    	
	    	// right
	    	// move is legal if index of column to right < dimension
	    	if(emptyCol + 1 < dimension){
	    		System.out.println(currentState.get(idx+1) + " to the west");
	    		ArrayList<Integer> potentialState = switchTiles(currentState, idx, idx + 1);
	    		System.out.println("h: " + calculateH(potentialState));
	    	}
	    }

	    /*****************************
	     * 
	     * switchTiles performs the game move on the board state currentMove
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
	    private static ArrayList<Integer> switchTiles(ArrayList<Integer> currentMove, int curIdx, int newIdx)
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
