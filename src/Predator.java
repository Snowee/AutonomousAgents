import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Predator {
	public Map<Point, ArrayList<Point>> nextPosPositions;
	public Point pos;
	public double[] policy = {0.2,0.2,0.2,0.2,0.2}; // Wait,N,E,S,West,
	public String[] actions = {"WAIT", "NORTH", "EAST", "SOUTH", "WEST"};
	public double[] probRange = {0.2, 0.4, 0.6, 0.8, 1};
	
	public Predator () {
		pos = new Point();
		pos.x = 0;
		pos.y = 0;
		nextPosPositions = new HashMap<Point, ArrayList<Point>>();
		initMap();
	}
	
	public String getMove () {
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
	
	public void to_String() {
		System.out.printf("Predator(%d,%d)\n", pos.x, pos.y);
	}
	
	// Initialize map with the predator position (Point object) as key and an
	// arraylist with possible next positions of the predator (Point objects)
	// corresponding to states that are reached with the following actions:
	// Wait, North, East, South, West
	private void initMap(){
		int[] moves = {0,-1, 1,0, 0,1, -1,0};
		for(int i = 0; i < 11; i++){
			for(int j = 0; j < 11; j++ ){
				Point currentPos = new Point(i, j);
				ArrayList<Point> nextPoss = new ArrayList<Point>(5);
				nextPoss.add(currentPos);
				for(int p = 0; p < 8; p=p+2 ){
					Point newPos = (Point) currentPos.clone();
					newPos.translate(moves[p], moves[p+1]);
					if( !(newPos.x >= 0) || !(newPos.x < 11) ){
						newPos.x = (newPos.x+11) % 11;
					}
					else{
						if( !(newPos.y >= 0) || !(newPos.y < 11) ){
							newPos.y = (newPos.y+11) % 11;
						}
					}
					nextPoss.add(newPos);
				}
				nextPosPositions.put(currentPos, nextPoss);
			}
		}
	}
}
