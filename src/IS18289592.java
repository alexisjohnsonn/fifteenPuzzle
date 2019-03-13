package is18289592;

import java.util.ArrayList;

public class IS18289592 {
	public static void main(String[] args){
		ArrayList<Integer> current = new ArrayList<>();
		ArrayList<Integer> goal = new ArrayList<>();
		current.add(3);
		current.add(2);
		current.add(1);
		current.add(4);
		current.add(5);
		current.add(7);
		current.add(0);
		current.add(8);
//		current.add(9);
//		current.add(0);
//		current.add(11);
//		current.add(12);
//		current.add(13);
//		current.add(14);
//		current.add(15);
		current.add(6);
		
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
		
		// assuming the player can only have square shaped puzzles,
		// dimension is the number of columns and rows in the puzzle
		int dimension = (int) Math.sqrt(current.size());

		System.out.println("Initial:");
		for(int i = 0; i <= current.size() - dimension; i+=dimension){
			System.out.print(current.get(i) + "  ");
			System.out.print(current.get(i+1) + "  ");
			System.out.println(current.get(i+2) + "  ");
//			System.out.println(current.get(i+3));
		}
		
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
		
		System.out.println("\nTotal Distance: " + totalDist);
	}
}
