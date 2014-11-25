package Assignment2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Game {
	// Class variables for game
	private Predator pred;
	private Prey prey;
	private boolean endState;
	// Class variables for non reduced algorithms
	public List<Point> allPredPos;
	// Class variables for reduced algorithms
	public Map<Point, Map<Point, Double>> Qvalues;
	public Map<Point, String> bestPolicy;
	private Point[] statesArray;
	
	// Constructor for a game object
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
		// Initialize all data structures to be used
		Qvalues = new HashMap<Point, Map<Point, Double>>();
		allPredPos = new ArrayList<Point>();
		bestPolicy = new HashMap<Point, String>();
	}
	
	// Function to run a single game, each turn starting by a move of the prey
	// then a move of the predator and then a check to see if the predator
	// caught the prey. 
	// Input: boolean randomPolicy - true for selecting random moves
	//								 false for good policy moves
	// 		  boolean reduction - true to use the reduced state space
	//							  false to use full state space
	public int start( boolean randomPolicy) {
		int counter = 0;
		printGameState( pred.pos, prey.pos );
		while( endState == false ) {			
			// Prey move
			String predNear = prey.checkPred( pred );
			String preyMove = prey.getMove( predNear );
			Point moveCoordsPrey = move( preyMove );
			prey.newPos( moveCoordsPrey );
			// Uncomment below for print statement to see movement of prey
			//printGameState(pred.pos, prey.pos);
			
			// Pred move
			String predMove;
			if( randomPolicy ) {
				// random move
				predMove = pred.getMove();
			}
			else {
				Point state = new Point();
				state.x = prey.pos.x - pred.pos.x;
				state.y = prey.pos.y - pred.pos.y;
				state = pred.checkDirections( state );
				predMove = bestPolicy.get( state );		
				}
			
			Point moveCoordsPred = move( predMove );
			pred.newPos( moveCoordsPred );
			// nr of steps
			counter += 1;
			
			//Uncomment below for print statement to see movement of predator
			//printGameState( pred.pos, prey.pos );
			
			//check if end of game
			checkStatus();
		}
		System.out.printf( "Game ended in %d steps\n", counter );
		return counter;
	}
	
	// Function to print the Point pred and Point prey locations in a board
	public void printGameState( Point pred, Point prey ) {
		String[][] board = new String[11][11];
		board[prey.y][prey.x] = "q";
		board[pred.y][pred.x] = "P";
		for( String[] row : board ) {
	        for( String r : row ) {
	        	if( r == null )
	        		System.out.printf("-\t");
	        	else System.out.printf("%s\t", r);
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	// Function to obtain a Point representation of a String move
	public Point move( String move ) {
		Point moveCoords = new Point();
		switch( move ) {
		case "NORTH":
			moveCoords.x = 0;
			moveCoords.y = -1;
			break;
		case "EAST":
			moveCoords.x = 1;
			moveCoords.y = 0;
			break;
		case "SOUTH":
			moveCoords.x = 0;
			moveCoords.y = 1;
			break;
		case "WEST":
			moveCoords.x = -1;
			moveCoords.y = 0;
			break;
		case "WAIT":
			moveCoords.x = 0;
			moveCoords.y = 0;
			break;
		default:
			break;
		}
		return moveCoords;
	}
	
	// Check if game should end
	public void checkStatus() {
		if( pred.pos.equals( prey.pos ) ) {
			endState = true;
		}
	}
	
	// Calculate the manhattan distance given the reduced state space
	public int calcDistance( Point state ) {
		return Math.abs( state.x ) + Math.abs( state.y );
	}
	
	// Check if a point is inside the board, if not alter the coordinates to
	// match the toroidal environment
	public Point checkLoc( Point loc ) {
		if( !( loc.x >= 0 ) || !( loc.x < 11 ) ) {
			loc.x = ( loc.x + 11 ) % 11;
		}
		if( !( loc.y >= 0 ) || !( loc.y < 11 ) ) {
				loc.y = ( loc.y + 11 ) % 11;
		}
		return loc;
	}
	
	// Function for reduced initialization of arbitrary function
	// ( all states, "west" )
	public Map<Point, String> reductionInitPolicy() {
		Map<Point, String> initialPolicy = new HashMap<Point, String>();
		String move = "WEST";
		for( int i = -5; i < 6; i++ ) {
			for( int j = -5; j < 6; j++ ) {
				Point state = new Point( i, j );
				initialPolicy.put( state, move );
			}
		}
		return initialPolicy;
	}
	
	// Function to print a board with actions on its coordinates
	public void printBoardActions( Map<Point, String> map, 
			Point preyLoc ) {
		String[][] board = new String[11][11];
		for( Map.Entry<Point, String> entry : map.entrySet() ) {
			Point state = entry.getKey();
			String action = map.get( state );
			Point newState = (Point) preyLoc.clone();
			newState.x -= state.x;
			newState.y -= state.y;
			newState = checkLoc( newState );
			board[newState.y][newState.x] = action;
		}
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	public void printBoardQActions( Map<Point, Map<Point, Double>> map, 
			Point preyLoc ) {
		String[][] board = new String[11][11];
		for( Map.Entry<Point, Map<Point, Double>> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Map<Point, Double> actions = map.get( state );
			double max = 0;
			int bestAct = 0;
			int actionCount = 0;
			for( int i = 0; i < entry.getValue().entrySet().size(); i++ ) {
				double value = entry.getValue().get(move(pred.actions[i]));
				if( value > max ) {
					bestAct = actionCount;
					max = value;
				}
				actionCount++;
			}
			Point newState = (Point) preyLoc.clone();
			newState.x -= state.x;
			newState.y -= state.y;
			newState = checkLoc( newState );
			if( newState.equals( preyLoc ) ) {
				board[preyLoc.y][preyLoc.x] = "";
				continue;
			}
			board[newState.y][newState.x] = pred.actions[bestAct];
		}
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	// Function to print the values for all states, based on reduced state space
	public void printBoard( Map<Point, Double> map, Point preyLoc ) {
		double[][] board = new double[11][11];
		for( Map.Entry<Point, Double> entry : map.entrySet() ) {
			Point state = entry.getKey();
			double value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			board[nextLoc.y][nextLoc.x] = value;
		}
		for( double[] row : board ) {
	        for( double r : row ) {
	        	if( r == 0 )
	        		System.out.print( "0         " );
	        	else
	        		System.out.printf( "%.9f", r );
	            System.out.print( "\t" );
	        }
	        System.out.println();
	    }
		System.out.println();
	}	
	
	public void printBoardQ( Map<Point, Map<Point, Double>> map, Point preyLoc ) {
		double[][][] board = new double[11][11][5];
		for( Map.Entry<Point, Map<Point, Double>> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Map<Point, Double> value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			int counter = 0;
			for( int i = 0; i < pred.actions.length; i++ ) {
				double value1 = value.get(move(pred.actions[i]));
				board[nextLoc.y][nextLoc.x][counter] = value1;
				counter++;
			}
		
		}
		for( int r = 0; r < 11; r++ ) {
			for( int i = 0; i < 5; i++ ) {
				for( int c = 0; c < 11; c++ ) {
//					if( board[r][c][i] == 0 ) {
//		        		System.out.print( "0         " );
//					} else {
		        		System.out.printf( "%.9f", board[r][c][i] );
		        		System.out.print( "\t" );
		        	//}
				}
				System.out.print("\n");
			}
			System.out.println();
		}
	}
//		
//		for( int m = 0; m < 11; m++ ) {
//	        for( int i = 0; i < 5; i++ ) {
//		        for( int n = 0; n < 11; n++ ) {
//		        	if( board[m][n][i] == 0 )
//		        		System.out.print( "0         " );
//		        	else {
//		        		System.out.printf( "%.9f", board[m][n][i] );
//		        		System.out.print( "\t" );
//		        	}
//		        }
//	        }
//	        System.out.print("\n");
//	    }
//		System.out.println();
//	}	
		
	public void qlearning(double discountFactor, int nEpisodes){
		initQvalues( 15.0 );
		for( int i = 0; i < nEpisodes; i++){
			Point s = initS();
			boolean terminalState = false;
			while(!terminalState){
				Point action = getActionGreedy(s);
				Point sPrime = (Point) s.clone();
				sPrime.translate(-1*action.x, -1*action.y);
				sPrime = checkLoc(sPrime);
				// Observe reward and compute Q value
				// s is sprime
			}
		}
	}
	
	private Point getActionGreedy(Point state){
		double epsilon = 0.1;
		Random rand = new Random();
		double chance = rand.nextDouble();
		Point action = new Point();
		// Select random action with probability epsilon
		if(chance < epsilon){
			action = move(pred.actions[rand.nextInt(pred.actions.length)]);
		}
		// Select optimal action
		else{
			double maxVal = 0;
			Map<Point, Double> valActFromState = Qvalues.get(state);
			for(Point act : valActFromState.keySet()){
				if(valActFromState.get(act) >= maxVal){
					action = act;
				}
			}
		}
		Point actionGr = action;
		
		return actionGr;
	}
	
	
	private Point getActionSoftmax( Point state ) {
		Random rand = new Random();
		double chance = rand.nextDouble();
		ArrayList<Double> softmaxProbs = softmaxProbabilities( state );
		int softmaxMove = -1;
		for( int i = 0; i < softmaxProbs.size(); i++ ) {
			if( chance < softmaxProbs.get(i) ) {
				softmaxMove = i;
				break;
			}
		}
		Point move = move( pred.actions[softmaxMove] );
		return move;
	}
	
	private ArrayList<Double> softmaxProbabilities( Point state ) {
		ArrayList<Double> softmaxProbs = new ArrayList<Double>();
		Map<Point, Double> valActFromState = Qvalues.get( state );
		double temperature = 10;
		double sum = 0;
		for( Point act : valActFromState.keySet() ) {
			sum += Math.exp( valActFromState.get( act ) / temperature );
		}
		
		//for( Point act : valActFromState.keySet() ) {
		for( int i = 0; i < valActFromState.entrySet().size(); i++ ) {
			double actVal = valActFromState.get(move(pred.actions[i]));
			double prob = Math.exp( actVal / temperature ) / sum;
			softmaxProbs.add( prob );
		}
		
		for( int i = 1; i < softmaxProbs.size(); i++ ) {
			softmaxProbs.set( i, softmaxProbs.get(i-1) + softmaxProbs.get(i) );
		}
		return softmaxProbs;		
	}
	
	private Point initS(){
		Point initialState = new Point();
		boolean nonTerminalInit = false;
		while( !nonTerminalInit ) {
			Random rand = new Random();
			initialState = statesArray[rand.nextInt(statesArray.length)];
			if( !initialState.equals(new Point(0,0) )) {
				nonTerminalInit = true;
			}
		}
		return initialState;
	}
	
	// Initialize reduced state space
	private void initQvalues( double qInitvals ) {
		int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
		Map<Point, Double> actionVal = new HashMap<Point, Double>();
		Map<Point, Double> terminalActionVal = new HashMap<Point, Double>();
		for(int i = 0; i < pred.actions.length; i++){
			actionVal.put(move(pred.actions[i]), qInitvals );
			terminalActionVal.put(move(pred.actions[i]), 0.0);
		}
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point directionVector = 
						new Point( directionValues[i], directionValues[j] );
				Map<Point, Double> actionQVal;
				if( !directionVector.equals( new Point(0,0) ) ) {
					actionQVal = new HashMap<Point, Double>(actionVal);
				} else {
					actionQVal = new HashMap<Point, Double>(terminalActionVal);
				}
				Qvalues.put( directionVector, actionQVal );
			}
		}
		Set<Point> states = Qvalues.keySet();
		statesArray = states.toArray( new Point[states.size()] );
	}
	/*	
	// Function to find the best actions for all states ( reduced state space )
	public Map<Point, String> findBestPolicy( double discountFactor,
			Map<Point, Double> finalValueMap ) {
		for( Point key : stateSpace.keySet() ) {
			String bestAction = findBestAction( discountFactor, key );
			bestPolicy.put( key, bestAction );
		}
		return bestPolicy;
	}
	*/
	/*
	// Function to find the best action given a certain state
	public String findBestAction( double discountFactor, Point state ) {
		double value = 0;
		String bestAction = "";
		ArrayList<Point> posNextDir = pred.nextPosDirections.get( state );
		int distance = calcDistance( state );
		if( distance != 0 ) {
			for( int i = 0; i < posNextDir.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDist = calcDistance( nextPos );
				if( nextDist == 0 ) {
					reward = 10;
				}
				double valueA = reward + discountFactor * 
						stateSpace.get( nextPos );
				if( valueA >= value ) {
					value = valueA;
					bestAction = pred.actions[i];
				}
			}	
		}
		return bestAction;
	}*/
	
	
	public void Sarsa( double discountFactor, double learningRate, int nEpisodes,
			boolean useGreedy ) {
		initQvalues( 15.0 );
		for( int i = 0; i < nEpisodes; i++ ) {
			Point s = initS();
			Point action;
			if( useGreedy )
				action = getActionGreedy( s );
			else
				action = getActionSoftmax( s );
			boolean terminalState = false;
			int nrOfSteps = 0;
			while( !terminalState ) {
				Point sPrime = (Point) s.clone();
				sPrime.translate( -1*action.x, -1*action.y );
				sPrime = pred.checkDirections( sPrime );
				nrOfSteps++;
				
				int reward = 0;
				if( sPrime.equals( new Point(0,0) ) ) {
					reward = 10;
					terminalState = true;
				} else {
					sPrime = doPreyMove( sPrime );
					sPrime = pred.checkDirections( sPrime );
				}
				Point actionPrime;
				if( useGreedy )
					actionPrime = getActionGreedy( sPrime );
				else
					actionPrime = getActionSoftmax( sPrime );
				Map<Point, Double> currentStateQvals = Qvalues.get( s );
				double currentQval = currentStateQvals.get( action );
				Map<Point, Double> newStateQvals = Qvalues.get( sPrime );
				double newQval = newStateQvals.get( actionPrime );
				double updatedValue = currentQval + 
						learningRate * ( reward + discountFactor * newQval - currentQval );
				currentStateQvals.put( action, updatedValue );
				Qvalues.put(s, currentStateQvals);
				s = (Point) sPrime.clone();
				action = (Point) actionPrime.clone();
				//printBoardQ( Qvalues, s );
			}
			System.out.println( nrOfSteps );
			
		}
		printBoardQ( Qvalues, new Point(5,5) );
		printBoardQActions( Qvalues, new Point(5,5) );
	}
	
	public boolean checkTerminalState( Point state ) {
		if ( calcDistance( state ) == 0 ) {
			return true;
		}
		return false;
	}
	
	public Point doPreyMove( Point state ) {
		Point predNear = prey.predNear( state );
		Point preyMove = prey.getMove( predNear );
		Point returnState = (Point) state.clone();
		returnState.translate(preyMove.x, preyMove.y);
		return returnState;
	}
	
}