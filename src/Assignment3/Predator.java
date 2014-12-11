package Assignment3;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Predator {
	// Initialize datastructures for possible next states
	public Map<Point, ArrayList<Point>> nextPosPositions;
	public Map<Point, ArrayList<Point>> nextPosDirections;
	public Point pos;
	// random policy, each action same probability
	public double[] policy = { 0.2, 0.2, 0.2, 0.2, 0.2 }; // Wait,N,E,S,West,
	public String[] actions = { "WAIT", "NORTH", "EAST", "SOUTH", "WEST" };
	public double[] probRange = { 0.2, 0.4, 0.6, 0.8, 1 };
	public Point[] predStartLocSt = { new Point(5,5), new Point(-5,-5), new Point(5,-5), new Point(-5,5) };
	public Point[] predStartLocs = { new Point(0,0), new Point(10,10), 
			new Point(0,10), new Point(10,0) };
	public List<Point> possMoves = new ArrayList<Point>( Arrays.asList( new Point(0,0), new Point(0,-1), new Point(1,0), new Point(0,1), new Point(-1,0)));
	
	// init predator either random or static at 0,0
	public Predator(boolean random, int startLoc ) {
		pos = new Point();
		Random rand = new Random();
		if(random){
			pos.x = rand.nextInt(11);
			pos.y = rand.nextInt(11);
		}else{
		pos.x = predStartLocs[startLoc].x;
		pos.y = predStartLocs[startLoc].y;
		}
		nextPosDirections = new HashMap<Point, ArrayList<Point>>();
		initMap();
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
	
	// Function to initialize all possible next states for each state
	// for the reduced state space
	private void initMap() {
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