package Assignment2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class Prey {
	
	public Point pos;
	// Initialize moves and their probability
	String[] possibleMoves = {"WAIT","NORTH","EAST","SOUTH","WEST"};
	Point[] possibleMovesPoint = 
		{new Point(0,0), new Point(0,-1), new Point(1,0), new Point(0,1), new Point(-1,0)};
	double[] probRange = {0.8, 0.85, 0.9, 0.95, 1};
		
	public Prey (boolean random) {
		pos = new Point();
		Random rand = new Random();
		if(random){
			pos.x = rand.nextInt(11);
			pos.y = rand.nextInt(11);
		}else{
		pos.x = 5;
		pos.y = 5;
		}
		// starting point of prey (5,5)
		//pos.x = 5;
		//pos.y = 5;
	}
	
	// Function to determine a random move, based on the probabilities in probRange
	public String getMove( String predNear ) {
		String move =  "";
		double chance = Math.random();
		// if the predator is not in adjacent location
		if ( predNear.equals("CLEAR") ) {
			for( int i = 0; i < probRange.length; i++ ) {
				if( chance <= probRange[i] ) {
					move = possibleMoves[i];
					break;
				}
			}
		} else {
			// if predator is in adjacent location
			// remove that location from the possibilities and give the
			// remaining three directions 1/3 of 0.2 of happening
			List<String> remainingMoves = 
					new ArrayList<String>( Arrays.asList( possibleMoves ) );
			List<Double> remainingMoveProbs = Arrays.asList(0.8, 
					0.2 + 1 * ( 0.20 / 3 ), 0.2 + 2 * ( 0.20 / 3 ), 
					0.2 + 3 * ( 0.20 / 3 ) );
			remainingMoves.remove( predNear );
			for( int i = 0; i < remainingMoves.size(); i++ ) {
				if( chance < remainingMoveProbs.get(i) ) {
					move = remainingMoves.get(i);
					break;
				}
			}
		}
	return move;
		
	}
	
	// Function applying a move to the current position
	public void newPos( Point move ) {
		pos.x = pos.x + move.x;
		pos.y = pos.y + move.y;
		if( !(pos.x >= 0) || !(pos.x < 11) ) {
			pos.x = (pos.x+11) % 11;
		}
		else {
			if( !(pos.y >= 0) || !(pos.y < 11) ) {
				pos.y = (pos.y+11) % 11;
			}
		}
	}
	
	// Function to check whether the predator is in a location
	// adjacent to the prey, and if so the returns direction of the predator
	public String checkPred( Predator pred ) {		
		String predLocation = "CLEAR";
		int xDifference, yDifference;
		
		xDifference = pos.x - pred.pos.x;
		yDifference = pos.y - pred.pos.y;
		
		if((xDifference == 1 || xDifference == -10) && yDifference == 0 ) {
			predLocation = "WEST";
		}
		else if((yDifference == 1 || yDifference == -10) && xDifference == 0 ) {
			predLocation = "NORTH";
		}
		else if((xDifference == -1 || xDifference == 10) && yDifference == 0 ) {
			predLocation = "EAST";
		}
		else if((yDifference == -1 || yDifference == 10) && xDifference == 0 ) {
			predLocation = "SOUTH";
		}
		return predLocation;
	}
	
	public Point getMove( Point predNear ) {
		Point move = new Point();
		double chance = Math.random();
		// if the predator is not in adjacent location
		if ( predNear.equals( new Point(-2,-2) ) ) {
			for( int i = 0; i < probRange.length; i++ ) {
				if( chance <= probRange[i] ) {
					move = possibleMovesPoint[i];
					break;
				}
			}
		} else {
			// if predator is in adjacent location
			// remove that location from the possibilities and give the
			// remaining three directions 1/3 of 0.2 of happening
			List<Point> remainingMoves = 
					new ArrayList<Point>( Arrays.asList( possibleMovesPoint ) );
			List<Double> remainingMoveProbs = Arrays.asList(0.8, 
					0.2 + 1 * ( 0.20 / 3 ), 0.2 + 2 * ( 0.20 / 3 ), 
					0.2 + 3 * ( 0.20 / 3 ) );
			remainingMoves.remove( predNear );
			for( int i = 0; i < remainingMoves.size(); i++ ) {
				if( chance < remainingMoveProbs.get(i) ) {
					move = remainingMoves.get(i);
					break;
				}
			}
		}
		return move;
		
	}
	
	public Point predNear( Point state ) {
		if( Math.abs(state.x) + Math.abs(state.y) == 1 ) {
			Point point = new Point();
			if( state.x == 1 ) {
				point.setLocation(-1, 0);
			} else
				if( state.x == -1 ) {
					point.setLocation(1, 0);
				} else
					if( state.y == 1) {
						point.setLocation(0,-1);
					} else 
						if( state.y == -1 ) {
							point.setLocation(0,1);
						}
			return point;
		}
		else 
			return new Point(-2, -2);
	}
	
	
	// print the location of the prey
	public void to_String() {
		System.out.printf( "Prey(%d,%d)\n", pos.x, pos.y );
	}
}

/*package Assignment2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Prey {
	
	public Point pos;
	// Initialize moves and their probability
	String[] possibleMoves = {"WAIT","NORTH","EAST","SOUTH","WEST"};
	Point[] possibleMovesPoint = 
		{new Point(0,0), new Point(0,-1), new Point(1,0), new Point(0,1), new Point(-1,0)};
	double[] probRange = {0.8, 0.85, 0.9, 0.95, 1};
		
	public Prey () {
		pos = new Point();
		// starting point of prey (5,5)
		pos.x = 5;
		pos.y = 5;
	}
	
	// Function to determine a random move, based on the probabilities in probRange
	public String getMove( String predNear ) {
		String move =  "";
		double chance = Math.random();
		// if the predator is not in adjacent location
		if ( predNear.equals("CLEAR") ) {
			for( int i = 0; i < probRange.length; i++ ) {
				if( chance <= probRange[i] ) {
					move = possibleMoves[i];
					break;
				}
			}
		} else {
			// if predator is in adjacent location
			// remove that location from the possibilities and give the
			// remaining three directions 1/3 of 0.2 of happening
			List<String> remainingMoves = 
					new ArrayList<String>( Arrays.asList( possibleMoves ) );
			List<Double> remainingMoveProbs = Arrays.asList(0.8, 
					0.2 + 1 * ( 0.20 / 3 ), 0.2 + 2 * ( 0.20 / 3 ), 
					0.2 + 3 * ( 0.20 / 3 ) );
			remainingMoves.remove( predNear );
			for( int i = 0; i < remainingMoves.size(); i++ ) {
				if( chance < remainingMoveProbs.get(i) ) {
					move = remainingMoves.get(i);
					break;
				}
			}
		}
	return move;
		
	}
	
	// Function applying a move to the current position
	public void newPos( Point move ) {
		pos.x = pos.x + move.x;
		pos.y = pos.y + move.y;
		if( !(pos.x >= 0) || !(pos.x < 11) ) {
			pos.x = (pos.x+11) % 11;
		}
		else {
			if( !(pos.y >= 0) || !(pos.y < 11) ) {
				pos.y = (pos.y+11) % 11;
			}
		}
	}
	
	// Function to check whether the predator is in a location
	// adjacent to the prey, and if so the returns direction of the predator
	public String checkPred( Predator pred ) {		
		String predLocation = "CLEAR";
		int xDifference, yDifference;
		
		xDifference = pos.x - pred.pos.x;
		yDifference = pos.y - pred.pos.y;
		
		if((xDifference == 1 || xDifference == -10) && yDifference == 0 ) {
			predLocation = "WEST";
		}
		else if((yDifference == 1 || yDifference == -10) && xDifference == 0 ) {
			predLocation = "NORTH";
		}
		else if((xDifference == -1 || xDifference == 10) && yDifference == 0 ) {
			predLocation = "EAST";
		}
		else if((yDifference == -1 || yDifference == 10) && xDifference == 0 ) {
			predLocation = "SOUTH";
		}
		return predLocation;
	}
	
	public Point getMove( Point predNear ) {
		Point move = new Point();
		double chance = Math.random();
		// if the predator is not in adjacent location
		if ( predNear.equals( new Point(-2,-2) ) ) {
			for( int i = 0; i < probRange.length; i++ ) {
				if( chance <= probRange[i] ) {
					move = possibleMovesPoint[i];
					break;
				}
			}
		} else {
			// if predator is in adjacent location
			// remove that location from the possibilities and give the
			// remaining three directions 1/3 of 0.2 of happening
			List<Point> remainingMoves = 
					new ArrayList<Point>( Arrays.asList( possibleMovesPoint ) );
			List<Double> remainingMoveProbs = Arrays.asList(0.8, 
					0.2 + 1 * ( 0.20 / 3 ), 0.2 + 2 * ( 0.20 / 3 ), 
					0.2 + 3 * ( 0.20 / 3 ) );
			remainingMoves.remove( predNear );
			for( int i = 0; i < remainingMoves.size(); i++ ) {
				if( chance < remainingMoveProbs.get(i) ) {
					move = remainingMoves.get(i);
					break;
				}
			}
		}
		return move;
		
	}
	
	public Point predNear( Point state ) {
		if( Math.abs(state.x) + Math.abs(state.y) == 1 ) {
			Point point = new Point();
			if( state.x == 1 ) {
				point.setLocation(-1, 0);
			} else
				if( state.x == -1 ) {
					point.setLocation(1, 0);
				} else
					if( state.y == 1) {
						point.setLocation(0,-1);
					} else 
						if( state.y == -1 ) {
							point.setLocation(0,1);
						}
			return point;
		}
		else 
			return new Point(-2, -2);
	}
	
	
	// print the location of the prey
	public void to_String() {
		System.out.printf( "Prey(%d,%d)\n", pos.x, pos.y );
	}
}
*/
