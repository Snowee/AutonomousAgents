import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Predator {
	// Initialize datastructures for possible next states
	public Map<Point, ArrayList<Point>> nextPosPositions;
	public Map<Point, ArrayList<Point>> nextPosDirections;
	public Point pos;
	// random policy, each action same probability
	public double[] policy = { 0.2, 0.2, 0.2, 0.2, 0.2 }; // Wait,N,E,S,West,
	public String[] actions = { "WAIT", "NORTH", "EAST", "SOUTH", "WEST" };
	public double[] probRange = { 0.2, 0.4, 0.6, 0.8, 1 };
	
	public Predator( boolean useReduction ) {
		pos = new Point();
		pos.x = 0;
		pos.y = 0;
		nextPosPositions = new HashMap<Point, ArrayList<Point>>();
		nextPosDirections = new HashMap<Point, ArrayList<Point>>();
		// if non reduced state space initialize for normal state space
		if ( !useReduction )
			initMap();
		else
			// else initialize for reduced state space
			reductionInitMap();
	}
	
	// function to determine a random action for the predator
	// move according to random policy
	public String getMove() {
		double chance = Math.random();
		String move =  "";
		for(int i = 0; i < probRange.length; i++){
			if(chance <= probRange[i]){
				move = actions[i];
				break;
			}
		}
		return move;
	}
	
	// function to apply a move to the current position of the predator
	public void newPos( Point move ) {
		pos.x = pos.x + move.x;
		pos.y = pos.y + move.y;
		
		if( !(pos.x >= 0) || !(pos.x < 11) ){
			pos.x = (pos.x+11) % 11;
		}
		else{
			if( !(pos.y >= 0) || !(pos.y < 11) ){
				pos.y = (pos.y+11) % 11;
			}
		}
	}
	
	// print predator location
	public void to_String() {
		System.out.printf("Predator(%d,%d)\n", pos.x, pos.y);
	}
	
	// Initialize map with the predator position (Point object) as key and an
	// arraylist with possible next positions of the predator (Point objects)
	// corresponding to states that are reached with the following actions:
	// Wait, North, East, South, West
	private void initMap() {
		int[] moves = { 0,-1, 1,0, 0,1, -1,0 };
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point currentPos = new Point( i, j );
				ArrayList<Point> nextPoss = new ArrayList<Point>(5);
				nextPoss.add( currentPos );
				for( int p = 0; p < 8; p = p + 2 ) {
					Point newPos = (Point) currentPos.clone();
					newPos.translate( moves[p], moves[p+1] );
					if( !(newPos.x >= 0) || !(newPos.x < 11) ) {
						newPos.x = (newPos.x+11) % 11;
					}
					else {
						if( !(newPos.y >= 0) || !(newPos.y < 11) ) {
							newPos.y = (newPos.y+11) % 11;
						}
					}
					nextPoss.add( newPos );
				}
				nextPosPositions.put( currentPos, nextPoss );
			}
		}
	}
	
	// Function to initialize all possible next states for each state
	// for the reduced state space
	private void reductionInitMap() {
		int[] moves = {0,1, -1,0, 0,-1, 1,0};
		for(int i = -5; i < 6; i++){
			for(int j = -5; j < 6; j++ ){
				Point currentState = new Point(i, j);
				ArrayList<Point> nextPoss = new ArrayList<Point>(5);
				nextPoss.add(currentState);
				for(int p = 0; p < 8; p=p+2 ){
					Point newPos = (Point) currentState.clone();
					newPos.translate(moves[p], moves[p+1]);
					newPos = checkDirections( newPos );
					nextPoss.add(newPos);
				}
				nextPosDirections.put(currentState, nextPoss);
			}
		}
	}
	
	// Function to check if state from reduced state space should
	// be different due to the toroidal environment.
	public Point checkDirections( Point nextPos ) {
		if( nextPos.x > 5 ) {
			nextPos.x = nextPos.x - 11;
		} else {
			if( nextPos.x < -5 ) {
				nextPos.x = 11 + nextPos.x;
			}
		}
		if( nextPos.y > 5 ) {
			nextPos.y = nextPos.y - 11;
		} else {
			if( nextPos.y < -5 ) {
				nextPos.y = 11 + nextPos.y;
			}
		}
		return nextPos;
	}
}
