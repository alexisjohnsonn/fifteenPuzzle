import javax.swing.*; // JOptionPane library
import java.util.*;

public class potentialSolution{

    private static JFrame frame;
    private static int size = 8;
    private static int dimension;
    private static ArrayList<Integer> init;
    private static ArrayList<Integer> goal;

    private static void setUpGame()
    {
        // get initial and goal states from user input
        frame = new JFrame();
        String initMessage = "Hello, please enter a start state.";
        init = getStateFromUserInput(initMessage);
        String goalMessage = "Now, enter a final state.";
        goal = getStateFromUserInput(goalMessage);

        // define dimension based on size
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

    // determine whether the integers in the user input are unique
    // these integers should also be >=0 and <=size
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

    private static int getRow(int idx){
        return idx / dimension;
    }

    private static int getCol(int idx){
        return idx % dimension;
    }

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
            // iterator over successors
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

    private static String printState(BoardStateNode node)
    {
        String result = "H: " + node.h + ", G: " + node.g + ", F: " + node.getF() + "\n";
        for (int i = 0; i<size+1; i+= dimension)
        {
            for (int j=i; j<i + dimension; j++)
            {
                int val = node.state.get(j);
                if ( val / 10 == 0)
                {
                    result += val + "   ";
                }
                else
                {
                    result += val + "  ";
                }

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
            while(current!= null)
            {
                printedSolution = printState(current) + printedSolution;
                current = current.prev;
            }

            System.out.println("Results: \n" + printedSolution);
        }
    }


    // for each state, maintain an ArrayList with the integers in the state and the state's h, g, and f values
    // BoardStateNode must implement Comparable for use in the PriorityQueue
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

        public List<ArrayList<Integer>> getSuccessors()
        {
            int emptyIdx = state.indexOf(0);
            int emptyRow = getRow(emptyIdx);
            int emptyCol = getCol(emptyIdx);

            List<ArrayList<Integer>> list = new ArrayList<>();

            // above
            if(emptyRow - 1 >= 0)
            {
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx - dimension);
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // below
            if(emptyRow + 1 < dimension)
            {
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx + dimension);
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // left
            if(emptyCol - 1 >= 0)
            {
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx - 1);
                if (this.prev== null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            // right
            if(emptyCol +1 < dimension)
            {
                ArrayList<Integer> succ = switchTiles(state, emptyIdx, emptyIdx + 1);
                if (this.prev == null || !this.prev.state.equals(succ))
                {
                    list.add(succ);
                }
            }

            return list;
        }

        public int getF()
        {
            return h+g;
        }

        // when comparing two BoardStateNode objects, just compare their f values
        // this sets up the PriorityQueue to sort based on f(n)
        public int compareTo(Object x)
        {

            return Integer.compare(this.getF(), ((BoardStateNode)x).getF());
        }

        // curIdx is the current index of the 0 tile. newIdx is the new index of the 0 tile in the next move
        // used to get the state for a new move
        private ArrayList<Integer> switchTiles(ArrayList<Integer> currentMove, int curIdx, int newIdx)
        {
            ArrayList<Integer> nextMove = new ArrayList<>(currentMove);
            Collections.swap(nextMove, curIdx, newIdx);
            return nextMove;
        }
    }

    // create a priority queue class for BoardStateNode objects
    public static class BSNPriorityQueue extends PriorityQueue<BoardStateNode>
    {

        // constructor
        public BSNPriorityQueue()
        {
            new PriorityQueue<>();
        }

        // see if any of the BoardStateNodes in the PriorityQueue contain the given state
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

        // given a state, return the associated BoardStateNode in the PriorityQueue
        // if no such node exists, return null
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

        // remove the node containing the given state
        // return a boolean to indicate success
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
