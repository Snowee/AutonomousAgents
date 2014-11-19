package Assignment1;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	// Class variables for game
	private Predator pred;
	private Prey prey;
	private boolean endState;
	// Class variables for non reduced algorithms
	public Map<List<Point>, Double> valueMap;
	public List<Point> allPredPos;
	public Map<List<Point>, String> bestPolicy;
	// Class variables for reduced algorithms
	public Map<Point, Double> stateSpace;
	public Map<Point, String> reductionBestPolicy;
	private double theta = 0.00000001;
	
	// Constructor for a game object
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
		// Initialize all data structures to be used
		valueMap = new HashMap<List<Point>, Double>();
		stateSpace = new HashMap<Point, Double>();
		allPredPos = new ArrayList<Point>();
		bestPolicy = new HashMap<List<Point>, String>();
		reductionBestPolicy = new HashMap<Point, String>();
	}
	
	// Function to run a single game, each turn starting by a move of the prey
	// then a move of the predator and then a check to see if the predator
	// caught the prey. 
	// Input: boolean randomPolicy - true for selecting random moves
	//								 false for good policy moves
	// 		  boolean reduction - true to use the reduced state space
	//							  false to use full state space
	public int start( boolean randomPolicy, boolean reduction ) {
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
				if( !reduction )
					// policy move without reduction
					predMove = bestPolicy.get(
							Arrays.asList( pred.pos, prey.pos ));
				else {
					// policy move with reduction
					Point state = new Point();
					state.x = prey.pos.x - pred.pos.x;
					state.y = prey.pos.y - pred.pos.y;
					state = pred.checkDirections( state );
					predMove = reductionBestPolicy.get( state );
				}
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
	
	// Function to implement policy evaluation algorithm
	public Map<List<Point>, Double> policyEvaluation( double discountFactor, 
			double[] policy ) {
		System.out.println( "Starting policy evaluation..." );
		System.out.println( "Initialize values for all states" );
		initValueMap();
		double deltaV = theta * 2;
		int counter = 0;
		// run until convergence
		while( deltaV > theta ) {
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = new HashMap<List<Point>, 
					Double>( valueMap );
			for( List<Point> key : valueMap.keySet() ) {
				double oldValue = valueMap.get( key );
			    double newValue = computeValue( discountFactor, key.get(0),
			    		key.get(1), policy );
			    newValueMap.put( key, newValue );
			    // compute the max difference over each sweep
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
			counter = counter + 1;
		}
		// Print the values for the states listed below (asked in assignment)
		List<Point> states = Arrays.asList( new Point(0,0), new Point(5,5), 
				new Point(3,2), new Point(4,5), new Point(10,2), 
				new Point(0,10), new Point(10,10), new Point(0,0));
		for( int i = 0; i < states.size(); i = i + 2 ) {
			List<Point> state = Arrays.asList( states.get(i), 
					states.get( i + 1 ) );
			double value = valueMap.get( state );
			System.out.printf( "Value of state Predator(%d,%d), Prey(%d,%d): %f\n",
					states.get(i).y, states.get(i).x, states.get( i + 1 ).y,
					states.get( i + 1 ).x, value );
		}	
		System.out.printf( "Policy evaluation converged in %d iterations\n",
				counter );
		return valueMap;
	}
	
	// Function implementing the policy iteration algorithm
	public void policyIteration( double discountFactor ) {
		int counter = 0;
		Map<List<Point>, String> policy = initPolicy();
		initValueMap();
		boolean policyStable = false;
		// As long as no stable policy has been achieved, continue
		while( !policyStable ) {
			// do policy evaluation
			policyEvaluationForIt( discountFactor, policy );
			// do policy improvement
			Map<List<Point>, String> newPolicy = policyImprovement( 
					discountFactor, policy );
			// If no changes in policy: stable policy and stop
			if( policy.entrySet().equals( newPolicy.entrySet() ) ) {
				policyStable = true;
			}
			else {
				// Continue with the newly found policy
				policy = newPolicy;
			}
			counter = counter + 1;
		}
		bestPolicy = policy;
		// Print the best found policy for the prey being at 5, 5
		// and print all values for prey being at 5, 5
		printBoardActions( bestPolicy, new Point(5,5) );
		printBoard( valueMap, new Point(5,5) );
		System.out.printf( "Policy iteration converged in %d iterations\n", 
				counter );
	}
	
	// Function implementing policy evaluation used in the policy iteration algorithm
	public void policyEvaluationForIt( double discountFactor, Map<List<Point>,
			String> policy ) {
		double deltaV = 2 * theta;
		int counter = 1;
		// run until convergence
		while( deltaV > theta ) {
			counter = counter + 1;
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = 
					new HashMap<List<Point>, Double>( valueMap );
			// For all initialized values
			for( List<Point> key : valueMap.keySet() ) {
				double oldValue = valueMap.get( key );
				// compute the new value
			    double newValue = computeValueByPol( discountFactor, key.get(0), 
			    		key.get(1), policy.get( key ) );
			    newValueMap.put( key, newValue );
			    // compute the max difference over sweep
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
		}
	}
	
	// Function implementing policy improvement algorithm
	public Map<List<Point>, String> policyImprovement( double discountFactor,
			Map<List<Point>, String> policy) {
		Map<List<Point>, String> newPolicy = new HashMap<List<Point>, String>();
		// For the values of all states
		for( List<Point> key : valueMap.keySet() ) {
			// find the best action
			String bNewPolicy = findBestAction( discountFactor, 
					key.get(0), key.get(1) );
			// store found action
			newPolicy.put( key, bNewPolicy );
		}
		return newPolicy;
	}
	
	// Function to compute the value using one specific action/policy
	public double computeValueByPol( double discountFactor, Point predLoc, 
			Point preyLoc, String policyMove ) {
		double newValue = 0;
		// If the predator is not yet on prey location
		if( !predLoc.equals( preyLoc ) ) {
			double reward = 0;
			// apply the specified move to the predator Point
			Point move = move( policyMove );
			Point newPredLoc = (Point) predLoc.clone();
			newPredLoc.x = newPredLoc.x + move.x;
			newPredLoc.y = newPredLoc.y + move.y;
			newPredLoc = checkLoc( newPredLoc );
			// If new position is catching prey, reward is 10
			if( newPredLoc.equals( preyLoc ) ) {
				reward = 10;
			}
			// compute the new value
			newValue = reward + discountFactor * valueMap.get(
					Arrays.asList( newPredLoc, preyLoc ) );
		}
		return newValue;
	}
	
	// Function to initialize a random policy (every state "west")
	public Map<List<Point>, String> initPolicy() {
		Map<List<Point>, String> initialPolicy = 
				new HashMap<List<Point>, String>();
		String move = "WEST";
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point predXY = new Point( i, j );
				allPredPos.add( predXY );
				for( int k = 0; k < 11; k++ ) {
					for( int l = 0; l < 11; l++ ) {
						Point preyXY = new Point( k, l );
						List<Point> state = Arrays.asList( predXY, preyXY );
						initialPolicy.put( state, move );
					}
				}
			}
		}
		
		return initialPolicy;
	}
	
	// Function implementing value iteration algorithm
	public void valueIteration( double discountFactor ) {
		System.out.println( "Starting value iteration..." );
		System.out.println( "Initialize values for all states" );
		// initialize values (all 0)
		initValueMap();
		double deltaV = theta * 2;
		int counter = 0;
		// run until convergence
		while( deltaV > theta ) {
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = 
					new HashMap<List<Point>, Double>( valueMap );
			// For the values of all states
			for( List<Point> key : valueMap.keySet() ) {
				double oldValue = valueMap.get( key );
				// update the values
			    double newValue = computeValue( discountFactor, 
			    		key.get(0), key.get(1) );
			    newValueMap.put( key, newValue );
			    // compute the max difference over full sweep
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
			counter = counter + 1;
		}
		// Output the values of all states in which the prey is located at (5,5)
		printBoard( valueMap, new Point(5,5) );
		// Find best policies for each state
		Map<List<Point>,String> bestPolMap = 
				findBestPolicy( discountFactor, valueMap );
		// Print found best policies in a board
		printBoardActions( bestPolMap, new Point(5,5) );
		System.out.printf( "Value iteration converged in %d iterations\n", 
				counter );
	}
	
	// Value function for policy evaluation algorithm 
	// (takes additional policy probabilities)
	public double computeValue( double discountFactor, Point predXY, 
			Point preyXY, double[] policy ) {
		double value = 0;
		// get reachable positions from current point
		ArrayList<Point> posNextPos = pred.nextPosPositions.get( predXY );
		// if predator not on same location prey
		if( !predXY.equals( preyXY ) ) {
			// sum the found values for all possible moves
			for( int i = 0; i < posNextPos.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if( nextPos.equals( preyXY ) ) {
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList( nextPos, preyXY );
				double valueA = policy[i] * 
						( reward + discountFactor * valueMap.get( statePrime ) );
				value = value + valueA;
			}	
		}
		return value;
	}
	
	// Value function (Bellman equation) for value iteration algorithm
	public double computeValue( double discountFactor, Point predXY, 
			Point preyXY ) {
		double value = 0;
		// get reachable positions from current point
		ArrayList<Point> posNextPos = pred.nextPosPositions.get( predXY );
		// if predator not on same location prey
		if( !predXY.equals( preyXY ) ) {
			// Find the maximum value out of all possible moves
			for( int i = 0; i < posNextPos.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if( nextPos.equals( preyXY ) ) {
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList( nextPos, preyXY );
				double valueA = reward + discountFactor * 
						valueMap.get( statePrime );
				if( valueA > value ) {
					value = valueA;
				}
			}	
		}
		return value;
	}
	
	// Initialize the values for all states ( all 0 )
	private void initValueMap() {
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point predXY = new Point( i, j );
				allPredPos.add( predXY );
				for( int k = 0; k < 11; k++ ) {
					for( int l = 0; l < 11; l++ ) {
						Point preyXY = new Point( k, l );
						List<Point> state = Arrays.asList( predXY, preyXY );
						valueMap.put( state, 0.0 );
					}
				}
			}
		}
	}
	
	// Function to find the best policy for each state
	public Map<List<Point>, String> findBestPolicy( double discountFactor,
			Map<List<Point>, Double> finalValueMap ) {
		// For the values of all states find what is the best move in that state
		for( List<Point> key : valueMap.keySet() ) {
			String bestAction = findBestAction( discountFactor, 
					key.get(0), key.get(1) );
			// Store found best move
			bestPolicy.put( key, bestAction );
		}
		return bestPolicy;
	}
	
	// Function to find the best action in a specific state
	public String findBestAction( double discountFactor, Point predXY,
			Point preyXY ) {
		double value = 0;
		String bestAction = "";
		ArrayList<Point> posNextPos = pred.nextPosPositions.get( predXY );
		if( !predXY.equals( preyXY ) ) {
			// For all possible moves and new states resulting from current state
			for( int i = 0; i < posNextPos.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if( nextPos.equals( preyXY ) ) {
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList( nextPos, preyXY );
				// Compute the value
				double valueA = reward + discountFactor * 
						valueMap.get( statePrime );
				// If better value, store the corresponding move as best move
				if( valueA >= value ) {
					value = valueA;
					bestAction = pred.actions[i];
				}
			}	
		}
		return bestAction;
	}
	
	// Function to print a board with the best action in each location
	public void printBoardActions( Map<List<Point>, String> map,
			Point preyLoc ) {
		String[][] board = new String[11][11];
		for( int i = 0; i < allPredPos.size(); i++ ) {
			List<Point> state = Arrays.asList( allPredPos.get(i), preyLoc );
			String action = map.get( state );
			board[allPredPos.get(i).y][allPredPos.get(i).x] = action;
		}
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	// Function to print the values of all states, given a certain prey location
	public void printBoard( Map<List<Point>, Double> map, Point preyLoc ) {
		double[][] board = new double[11][11];
		for( int i = 0; i < allPredPos.size(); i++ ) {
			List<Point> state = Arrays.asList( allPredPos.get(i), preyLoc );
			double value = map.get( state );
			board[allPredPos.get(i).y][allPredPos.get(i).x] = value;
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
	
	
	// Begin Reduced Functions
	// Function for reduced version of policy evaluation
	public Map<Point, Double> reductionPolicyEvaluation( double discountFactor,
			double[] policy ) {
		System.out.println( "Starting reduced policy evaluation..." );
		System.out.println( "Initialize values for all states" );
		initStateSpace();
		double deltaV = theta * 2;
		int counter = 0;
		// run until convergence
		while( deltaV > theta ) {
			deltaV = 0;
			Map<Point, Double> newValueMap = 
					new HashMap<Point, Double>(stateSpace);
			// For all states in reduced state space
			for( Point key : stateSpace.keySet() ) {
				double oldValue = stateSpace.get( key );
				// compute the new value
			    double newValue = 
			    		reductionComputeValue( discountFactor, key, policy );
			    newValueMap.put( key, newValue );
			    // compute the max difference
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
			counter = counter + 1;
		}
		
		System.out.printf( "Reduced policy evaluation converged in %d iterations\n",
				counter );
		return stateSpace;
	}
	
	// Function for reduced version of Policy iteration algorithm
	public void reductionPolicyIteration( double discountFactor ) {
		int counter = 0;
		Map<Point, String> policy = reductionInitPolicy();
		// initialize values for all states
		initStateSpace();
		boolean policyStable = false;
		// until stable policy
		while( !policyStable ) {
			// Perform reduced version of policy evaluation
			reductionPolicyEvaluationForIt( discountFactor, policy );
			// perform reduced version of policy improvement
			Map<Point, String> newPolicy = 
					reductionPolicyImprovement( discountFactor, policy );
			// If no changes between old and new policy: stable
			if( policy.entrySet().equals( newPolicy.entrySet() ) ) {
				policyStable = true;
			}
			else {
				policy = newPolicy;
			}
			counter = counter + 1;
		}
		reductionBestPolicy = policy;
		// Print the best policy actions for each state where the prey is at 5, 5
		reductionPrintBoardActions( reductionBestPolicy, new Point(5,5) );
		// print the values for each state where the prey is at 5, 5
		reductionPrintBoard( stateSpace, new Point(5,5) );
		System.out.printf( "Policy iteration with state space reduction converged in %d iterations\n",
				counter );
	}
	
	// Function for the reduced policy evaluation algorithm
	// used in the policy iteration algorithm
	public void reductionPolicyEvaluationForIt( double discountFactor, 
			Map<Point, String> policy ) {
		double deltaV = 2 * theta;
		int counter = 1;
		// run until convergence
		while( deltaV > theta ) {
			counter = counter + 1;
			deltaV = 0;
			Map<Point, Double> newValueMap = 
					new HashMap<Point, Double>( stateSpace );
			// For all states
			for( Point key : stateSpace.keySet() ) {
				double oldValue = stateSpace.get( key );
				// Compute new value
			    double newValue = reductionComputeValueByPol(
			    		discountFactor, key, policy.get( key ) );
			    newValueMap.put( key, newValue );
			    // compute max difference in value
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
		}
	}
	
	// Function for reduced version of policy improvement
	public Map<Point, String> reductionPolicyImprovement( double discountFactor,
			Map<Point, String> policy ) {
		Map<Point, String> newPolicy = new HashMap<Point, String>();
		// For all states, find the best actions
		for( Point key : stateSpace.keySet() ) {
			String bNewPolicy = reductionFindBestAction( discountFactor, key );
			newPolicy.put( key, bNewPolicy );
		}
		return newPolicy;
	}
	
	// Function for reduced version of computing a value 
	// for a specific action/policy
	public double reductionComputeValueByPol( double discountFactor, Point state,
			String policyMove ) {
		double newValue = 0;
		int distance = calcDistance( state );
		if( distance != 0 ) {
			double reward = 0;
			// Apply the specific move to the state
			Point move = move( policyMove );
			Point newState = (Point) state.clone();
			newState.x = newState.x - move.x;
			newState.y = newState.y - move.y;
			// check the state if it should be different direction(toroidal)
			newState = pred.checkDirections( newState );
			int newDistance = calcDistance( newState );
			if( newDistance == 0 ) {
				reward = 10;
			}
			// compute value
			newValue = reward + discountFactor * stateSpace.get( newState );
		}
		return newValue;
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
	
	// Function for reduced value iteration algorithm
	public void reductionValueIteration( double discountFactor ) {
		System.out.println( "Starting reduced value iteration..." );
		System.out.println( "Initialize values for all states" );
		initStateSpace();
		double deltaV = theta * 2;
		int counter = 0;
		// run until convergence
		while( deltaV > theta ) {
			deltaV = 0;
			Map<Point, Double> newValueMap = 
					new HashMap<Point, Double>( stateSpace );
			// for all states
			for( Point key : stateSpace.keySet() ) {
				double oldValue = stateSpace.get( key );
				// compute a new value
			    double newValue = reductionComputeValue( discountFactor, key );
			    newValueMap.put( key, newValue );
			    // compute the max difference
			    double diffVal = Math.abs( oldValue - newValue );
			    if( diffVal > deltaV ) {
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
			counter = counter + 1;
		}
		// Output the values of all states in which the prey is located at (5,5)
		reductionPrintBoard( stateSpace, new Point(5,5) );
		// Print the best policy actions for each state where the prey is at 5, 5
		Map<Point,String> bestPolMap = 
				reductionFindBestPolicy( discountFactor, stateSpace );
		reductionPrintBoardActions( bestPolMap, new Point(5,5) );
		System.out.printf("Value iteration with state space reduction converged in %d iterations\n",
				counter);
	}
	
	// reduced version of computing value for value iteration
	public double reductionComputeValue( double discountFactor, Point state ) {
		double value = 0;
		ArrayList<Point> posNextDir = pred.nextPosDirections.get( state );
		int distance = calcDistance( state );
		if( distance != 0 ) {
			// For all possible moves, compute their resulting values
			// pick the move maximizing the value
			for( int i = 0; i < posNextDir.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDistance = calcDistance( nextPos );
				if( nextDistance == 0 ) {
					reward = 10;
				}
				double valueA = reward + discountFactor * 
						stateSpace.get( nextPos );
				if( valueA > value ) {
					value = valueA;
				}
			}	
		}
		return value;
	}
	
	// reduced version of computing the value for policy evaluation
	// using an additional policy input (probabilities of actions)
	public double reductionComputeValue( double discountFactor, Point state,
			double[] policy ) {
		double value = 0;
		ArrayList<Point> posNextDir = pred.nextPosDirections.get( state );
		int distance = calcDistance( state );
		if( distance != 0 ) {
			// Sum the values resulting from all possible moves
			for( int i = 0; i < posNextDir.size(); i++ ) {
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDistance = calcDistance( nextPos );
				if( nextDistance == 0 ) {
					reward = 10;
				}
				double valueA = policy[i] * 
						( reward + discountFactor * stateSpace.get( nextPos ) );
				value = value + valueA;
			}	
		}
		return value;
	}
	
	// Function to print a board with actions on its coordinates
	public void reductionPrintBoardActions( Map<Point, String> map, 
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
	
	// Function to print the values for all states, based on reduced state space
	public void reductionPrintBoard( Map<Point, Double> map, Point preyLoc ) {
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
		
	// Initialize reduced state space
	private void initStateSpace() {
		int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point directionVector = 
						new Point( directionValues[i], directionValues[j] );
				stateSpace.put( directionVector, 0.0 );
			}
		}
	}
		
	// Function to find the best actions for all states ( reduced state space )
	public Map<Point, String> reductionFindBestPolicy( double discountFactor,
			Map<Point, Double> finalValueMap ) {
		for( Point key : stateSpace.keySet() ) {
			String bestAction = reductionFindBestAction( discountFactor, key );
			reductionBestPolicy.put( key, bestAction );
		}
		return reductionBestPolicy;
	}
	
	// Function to find the best action given a certain state
	public String reductionFindBestAction( double discountFactor, Point state ) {
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
	}
}