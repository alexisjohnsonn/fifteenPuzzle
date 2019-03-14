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

    // curIdx is the current index of the 0 tile. newIdx is the new index of the 0 tile in the next move
    private static ArrayList<Integer> move(ArrayList<Integer> currentMove, int curIdx, int newIdx)
    {
        ArrayList<Integer> nextMove = new ArrayList<>(currentMove);
        Collections.swap(nextMove, curIdx, newIdx);
        return nextMove;
    }

    private static void printState(ArrayList<Integer> current){
        // TODO seriously
        System.out.println("Initial:");
        for(int i = 0; i <= current.size() - dimension; i+=dimension){
            System.out.print(current.get(i) + "  ");
            System.out.print(current.get(i+1) + "  ");
            System.out.println(current.get(i+2) + "  ");
//			System.out.println(current.get(i+3));
        }
        System.out.println();
    }


    public static void main(String args[])
    {
        // setUpGame();

        dimension = 3;

        goal = new ArrayList<>();
        goal.add(1);
        goal.add(2);
        goal.add(3);
        goal.add(4);
        goal.add(5);
        goal.add(6);
        goal.add(7);
        goal.add(8);
//		goal.add(9);
//		goal.add(10);
//		goal.add(11);
//		goal.add(12);
//		goal.add(13);
//		goal.add(14);
//		goal.add(15);
        goal.add(0);

        ArrayList<Integer> current = new ArrayList<>();
        current.add(4);
        current.add(2);
        current.add(3);
        current.add(1);
        current.add(5);
        current.add(9);
        current.add(7);
        current.add(8);
        current.add(0);
//		current.add(11);
//		current.add(10);
//		current.add(12);
//		current.add(13);
//		current.add(15);
//		current.add(14);
//		current.add(0);

        printState(current);

        getMoves(current);
    }
}