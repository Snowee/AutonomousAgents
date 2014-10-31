import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
public class Prey {
	
	public Point pos;
		
	public Prey () {
		pos = new Point();
		pos.x = 5;
		pos.y = 5;
	}
	
	public String move ( String predNear ) {
		String move =  "";
		double policy = Math.random();

		if ( predNear.equals("CLEAR") ) {
			// Wait
			if ( policy < 0.80 ) {
				move = "WAIT";			
			} else {
				// East
				if ( policy >= 0.80 && policy < 0.85 ) {
					move = "EAST";
				} else {
					// South
					if ( policy >= 0.85 && policy < 0.90 ) {
						move = "SOUTH";
					} else {
						// West
						if ( policy >= 0.90 && policy < 0.95 ) {
							move = "WEST";
						} else {
							// Stay
							move = "NORTH";
						}
					}
				}
			}
		} else {
			ArrayList<String> possibleMoves = 
					new ArrayList<String>(Arrays.asList("NORTH","EAST","SOUTH","WEST"));
			ArrayList<Double> remainingMoveProbs = 
					new ArrayList<Double>(Arrays.asList(1*(0.80/3), 2*(0.80/3), 3*(0.80/3)));
			possibleMoves.remove( predNear );
			for ( int i = 0; i < possibleMoves.size(); i++ ) {
				if ( policy < remainingMoveProbs.get(i) ) {
					move = possibleMoves.get(i);
				}
			}
			if ( move.equals("") ) {
				move = "WAIT";
			}
		}
	return move;
		
	}
	
	public void newPos( Point move ) {
		pos.x = pos.x + move.x;
		pos.y = pos.y + move.y;
		if ( pos.x > 10 ) 
			pos.x = 0;
		if ( pos.y > 10 )
			pos.y = 0;
		if ( pos.x < 0 )
			pos.x = 10;
		if ( pos.y < 0 )
			pos.y = 10;
	}
	
	public String checkPred( Predator pred ) {
		Point checkPos = new Point();
		
		//North
		checkPos = pos;
		checkPos.y = checkPos.y - 1;
		if ( checkPos.y < 0 ) 
			checkPos.y = 10;
		if ( checkPos.equals(pred.pos) ) {
			return "NORTH";
		} else {
			//East
			checkPos = pos;
			checkPos.x = checkPos.x + 1;
			if ( checkPos.x > 10 ) 
				checkPos.x = 0;
			if ( checkPos.equals(pred.pos) ) {
				return "EAST";
			} else {
				//South
				checkPos = pos;
				checkPos.y = checkPos.y + 1;
				if ( checkPos.y > 10 ) 
					checkPos.y = 0;
				if ( checkPos.equals(pred.pos) ) {
					return "SOUTH";
				} else {
					//West
					checkPos = pos;
					checkPos.x = checkPos.x - 1;
					if ( checkPos.x < 0 ) 
						checkPos.x = 10;
					if ( checkPos.equals(pred.pos) ) {
						return "WEST";
					} else {
						return "CLEAR";
					}
				}
			}
		}
		
	}
	
	public void to_String() {
		System.out.printf( "Prey(%d,%d)\n", pos.x, pos.y );
	}
}

