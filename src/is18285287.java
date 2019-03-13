

import javax.swing.*; // JOptionPane library
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class is18285287{

    private static JFrame frame;
    private static int size = 8;
    private static ArrayList<Integer> init;
    private static ArrayList<Integer> goal;

    private static void setUpGame()
    {
        frame = new JFrame();
        String initMessage = "Hello, please enter a start state.";
        init = getStateFromUserInput(initMessage);
        String goalMessage = "Now, enter a final state.";
        goal = getStateFromUserInput(goalMessage);

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
        }

        return retVal;
    }

    private static boolean uniqueIntegers(ArrayList<Integer> arr)
    {
        HashSet set = new HashSet<Integer>();
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

    private static int calculateH(ArrayList<Integer> current, ArrayList<Integer> goal){
        // assuming the puzzle is a square, the vertical and horizontal dimensions are the same
        int dimension = (int) Math.sqrt(current.size());

        // totalDist cumulates the distance of each tile, giving h
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

    public static void main(String args[])
    {
        setUpGame();
    }

}

