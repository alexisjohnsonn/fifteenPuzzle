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

    private static void setUpGame()
    {
        frame = new JFrame();
        String initMessage = "Hello, please enter a start state.";
        init = getStateFromUserInput(initMessage);
        String goalMessage = "Now, enter a final state.";
        goal = getStateFromUserInput(goalMessage);
		dimension = (int) Math.sqrt(size+1);
        
    }

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
            if (count != size + 1 || scanner.hasNextInt() || !uniqueIntegers(retVal))
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
    
    public static int getRow(int idx){
    	return idx / dimension;
    }
    
    public static int getCol(int idx){
    	return idx % dimension;
    }
    
    public static void getMoves(ArrayList<Integer> currentState){
    	int idx = currentState.indexOf(0);
    	int emptyRow = getRow(idx);
    	int emptyCol = getCol(idx);
    	
    	// above
    	int above = emptyRow - 1; // move is legal if above > 0
    	if(above > 0){
    		System.out.println(currentState.get(idx - dimension) + " to the south");
    	}
    	
    	// below
    	int below = emptyRow + 1; // move is legal if below < dimension
    	if(below < dimension){
    		System.out.println(currentState.get(idx + dimension) + " to the north");
    	}
    	
    	// left
    	int left = emptyCol - 1; // move is legal if left > 0
    	if(left > 0){
    		System.out.println(currentState.get(idx-1) + " to the east");
    	}
    	
    	// right
    	int right = emptyCol +1; // move is legal if right < dimension
    	if(right < dimension){
    		System.out.println(currentState.get(idx+1) + " to the west");
    	}
    }

    private static int calculateH(ArrayList<Integer> current, ArrayList<Integer> goal){
        // assuming the puzzle is a square, the vertical and horizontal dimensions are the same
        int dimension = (int) Math.sqrt(current.size());

        // totalDist accumulates the distance of each tile, giving h
        int totalDist = 0;

        for(int i = 1; i < current.size(); i++){
            // find the index of tile i in the current state
            int curIdx = current.indexOf(i);

            // find the index of tile i in the goal state
            int goalIdx = goal.indexOf(i);

            // a piece's column is given by its index in the array % the number of columns,
            // so number of horizontal moves is given by the difference between the current and goal columns
            int horizMoves = Math.abs((curIdx % dimension) - (goalIdx % dimension));

            // a piece's row is given by its index in the array / the number of rows,
            // so number of vertical moves is given by the difference between the current and goal rows
            int vertMoves = Math.abs((curIdx / dimension) - (goalIdx / dimension));

            totalDist += horizMoves + vertMoves;

        }

        return totalDist;
    }

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
    }
}

