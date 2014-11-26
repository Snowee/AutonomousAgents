package Assignment2;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	public Map<Point, Map<Point, Double>> Nvalues;
	public Map<Point, Map<Point, Double>> Dvalues;
	public Map<Point, Point> detPolicy;
	public Map<Point, String> bestPolicy;
	private Point[] statesArray;
	Map<Point, Integer> stateCounts;

	
	// Constructor for a game object
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
		// Initialize all data structures to be used
		Qvalues = new HashMap<Point, Map<Point, Double>>();
		Nvalues = new HashMap<Point, Map<Point, Double>>();
		Dvalues = new HashMap<Point, Map<Point, Double>>();
		detPolicy = new HashMap<Point, Point>();
		allPredPos = new ArrayList<Point>();
		bestPolicy = new HashMap<Point, String>();
		stateCounts = new HashMap<Point, Integer>();
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
	
	public void printboardQmax(Map<Point, Map<Point, Double>> map, Point preyLoc){
		double[][] board = new double[11][11];
		for( Map.Entry<Point,Map<Point, Double>> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Map<Point, Double> value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			//if(!nextLoc.equals(preyLoc)){
			double max = 0;
			for(int i = 0; i < pred.actions.length; i++){
				if(value.get(move(pred.actions[i])) > max ){
					max = value.get(move(pred.actions[i]));
				}
			}
			//}
			board[nextLoc.y][nextLoc.x] = max;
		}
		for( double[] row : board ) {
	        for( double r : row ) {
	        	System.out.printf( "%f\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	public void printboardQActions(Map<Point, Map<Point, Double>> map, Point preyLoc){
		String[][] board = new String[11][11];
		for( Map.Entry<Point,Map<Point, Double>> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Map<Point, Double> value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			String bestAction = "";
			if(!nextLoc.equals(preyLoc)){
				double max = 0;
				for(int i = 0; i < pred.actions.length; i++){
					if(value.get(move(pred.actions[i])) > max ){
						bestAction = pred.actions[i];
						max = value.get(move(pred.actions[i]));
					}
				}
			}
			board[nextLoc.y][nextLoc.x] = bestAction;
		}
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
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

			for(int i = 0; i < pred.actions.length; i++){
				double value1 = value.get(move(pred.actions[i]));
				board[nextLoc.y][nextLoc.x][counter] = value1;
				counter++;
			}
		}
	
		for(int r = 0; r < 11; r++){
			for(int i = 0; i < 5; i++){
				for( int c = 0; c < 11; c++){
					if( board[r][c][i] == 0 )
		        		System.out.print( "0         \t" );
		        	else{
		        		System.out.printf( "%.9f", board[r][c][i] );
		        		System.out.print( "\t" );
		        	}
				}
				System.out.print("\n");
			}
			System.out.println();
		}
	}


		
	public void qlearning(double alpha, double discountFactor, int nEpisodes, boolean greedy){
		initQvalues(15.0);
		Point terminalState = new Point(0,0);
		for( int i = 1; i <= nEpisodes; i++){
			Point s = initS();
			boolean inTerminalState = false;
			int stepCounter = 0;
			while(!inTerminalState){
				stepCounter = stepCounter + 1;
				Point action;
				if(greedy){
					action = getActionGreedy(s);
				}
				else{
					action = getActionSoftmax(s);
				}
				Point sTemp = (Point) s.clone();
				sTemp.translate(-1*action.x, -1*action.y);
				sTemp = pred.checkDirections(sTemp);
				Point sPrime = new Point(0,0);
				
				// Observe reward
				int reward = 0;
				// If predator move resulted in terminal state,
				// observe reward of 10, update Qvalue and end the episode
				if(sTemp.equals(terminalState)){
					reward = 10;
					inTerminalState = true;
					sPrime = sTemp;
				}
				else{
					sPrime = interactWithEnv(sTemp);
				}
				
				// Compute Q value for current state s
				double qval = computeQvalueQL(s, action, alpha, 
						discountFactor, sPrime, reward);
				Qvalues.get(s).put(action, qval);
				
				// s is sPrime
				s = sPrime;
			}
			//System.out.println("End of episode: "+i);
			System.out.printf("Episode ended in %d steps\n", stepCounter);
		}
		printBoardQ(Qvalues, new Point(5,5));
		printboardQActions(Qvalues, new Point(5,5));
		printboardQmax(Qvalues, new Point(5,5));
	}
	
	// Compute Q-value in Qlearning for state s
	private double computeQvalueQL(Point s, Point action, double alpha, 
			double discountFactor, Point sPrime, int reward){
		double oldqval = Qvalues.get(s).get(action);
		Set<Point> sPrimeActions = Qvalues.get(sPrime).keySet();
		double maxPrimeQval = 0;
		for(Point actionPrime : sPrimeActions){
			if(Qvalues.get(sPrime).get(actionPrime) >= maxPrimeQval){
				maxPrimeQval = Qvalues.get(sPrime).get(actionPrime);
			}
		}
		double qval = oldqval + alpha*(reward + 
				(discountFactor*maxPrimeQval) - oldqval);
		
		return qval;
	}
	
	// Retrieve next state when interacting with environment
	// (Prey does a move)
	private Point interactWithEnv(Point sTemp){
		Point sPrime;
		Point predNear = prey.predNear(sTemp);
		Point actionPrey = prey.getMove(predNear);
		sTemp.translate(actionPrey.x, actionPrey.y);
		sPrime = pred.checkDirections(sTemp);
		
		return sPrime;
	}
	
	// Get epsilon-greedy action for predator
	private Point getActionGreedy(Point state){
		double epsilon = 0.1;
		Random rand = new Random();
		double chance = rand.nextDouble();
		Point action = new Point();

		// Select random action with probability epsilon
		if(chance < epsilon){
			//System.out.println("Random action");
			action = move(pred.actions[rand.nextInt(pred.actions.length)]);
		}
		// Select optimal action
		else{
			double maxVal = 0;
			Map<Point, Double> valActFromState = Qvalues.get(state);
			for(Map.Entry<Point, Double> entry : valActFromState.entrySet()){
				if(entry.getValue() >= maxVal){
					action = entry.getKey();
					maxVal = entry.getValue();
				}
			}
		}
		
		return action;
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
	
	private void initQvalues(double initialQvalues) {
		int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
		Map<Point, Double> actionValterm = new HashMap<Point, Double>(); // for terminal state
		// Random initial Q values
		if(initialQvalues < 0.0){
			Random rand = new Random();
			for( int i = 0; i < 11; i++ ) {
				for( int j = 0; j < 11; j++ ) {
					Point directionVector = 
							new Point( directionValues[i], directionValues[j] );
					Map<Point, Double> actionQVal;
					if( directionVector.equals( new Point(0,0) ) ) {
						for(int k = 0; k < pred.actions.length; k++){
							actionValterm.put(move(pred.actions[k]), 0.0);
						}
						actionQVal = new HashMap<Point, Double>(actionValterm);
					} else {
						Map<Point, Double> actionVal = new HashMap<Point, Double>();
						for(int k = 0; k < pred.actions.length; k++){
							actionVal.put(move(pred.actions[k]), (double) rand.nextInt(11));
						}
						actionQVal = new HashMap<Point, Double>(actionVal);
					}
					Qvalues.put( directionVector, actionQVal );
				}
			}
		} else{
			Map<Point, Double> actionVal = new HashMap<Point, Double>();
			for(int i = 0; i < pred.actions.length; i++){
				actionValterm.put(move(pred.actions[i]), 0.0);
				actionVal.put(move(pred.actions[i]), initialQvalues);
			}
			for( int i = 0; i < 11; i++ ) {
				for( int j = 0; j < 11; j++ ) {
					Point directionVector = 
							new Point( directionValues[i], directionValues[j] );
					Map<Point, Double> actionQVal;
					if( directionVector.equals( new Point(0,0) ) ) {
						actionQVal = new HashMap<Point, Double>(actionValterm);
					} else {
						actionQVal = new HashMap<Point, Double>(actionVal);
					}
					Qvalues.put( directionVector, actionQVal );
				}
			}
		}
		Set<Point> states = Qvalues.keySet();
		statesArray = states.toArray( new Point[states.size()] );
	}
	
//	// Initialize reduced state space
//	private void initQvalues(double initialQvalues) {
//		int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
//		Map<Point, Double> actionValterm = new HashMap<Point, Double>(); // for terminal state
//		Map<Point, Double> actionVal = new HashMap<Point, Double>();
//		for(int i = 0; i < pred.actions.length; i++){
//			actionValterm.put(move(pred.actions[i]), 0.0);
//			actionVal.put(move(pred.actions[i]), initialQvalues);
//		}
//		for( int i = 0; i < 11; i++ ) {
//			for( int j = 0; j < 11; j++ ) {
//				Point directionVector = 
//						new Point( directionValues[i], directionValues[j] );
//				Map<Point, Double> actionQVal;
//				if( directionVector.equals( new Point(0,0) ) ) {
//					actionQVal = new HashMap<Point, Double>(actionValterm);
//				} else {
//					actionQVal = new HashMap<Point, Double>(actionVal);
//				}
//				Qvalues.put( directionVector, actionQVal );
//			}
//		}
//		Set<Point> states = Qvalues.keySet();
//		statesArray = states.toArray( new Point[states.size()] );
//	}
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
		printboardQmax( Qvalues, new Point(5,5));
		printboardQActions( Qvalues, new Point(5,5) );
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
	
	
	private Map<Point, Map<Point, Double>> initEpsilonSoftPolicy(){
		Map<Point, Map<Point, Double>> eSoftPol = new HashMap<Point, Map<Point, Double>>();
		for(Entry<Point, Map<Point, Double>> stateActVal : Qvalues.entrySet()){
			double[] probs = getNormalizedProbDist(stateActVal.getValue().size());
			int counter = 0;
			Map<Point, Double> actVal = new HashMap<Point, Double>();
			for(Point act : stateActVal.getValue().keySet() ){
				actVal.put(act, probs[counter]);
				counter++;
			}
			eSoftPol.put(stateActVal.getKey(), actVal);
		}
		
		return eSoftPol;
	}
	
	private double[] getNormalizedProbDist(int n){
		Random rand = new Random();
		double[] probDist = new double[n];
		double sum = 0;
		for(int i = 0; i < n; i++){
			probDist[i] = rand.nextDouble();
			sum = sum + probDist[i];
		}
		for(int i = 0; i < n; i++){
			probDist[i] = probDist[i]/sum;
		}
		
		return probDist;
	}
	
	private Point getActionSoftPol(Map<Point, Map<Point, Double>> policy, Point s){
		Random rand = new Random();
		double chance = rand.nextDouble();
		Map<Point, Double> actionProbs = policy.get(s);
		List<Double> probs = new ArrayList<Double>();
		
		probs.add(actionProbs.get(move(pred.actions[0])));
		for( int i = 1; i < pred.actions.length; i++ ) {
			probs.add(probs.get(i-1) + actionProbs.get(move(pred.actions[i])));
		}
		int actionInd = -1;
		for(int i = 0; i < probs.size(); i++){
			if( chance < probs.get(i) ){
				actionInd = i;
				break;
			}
		}
		Point action = move(pred.actions[actionInd]);

		return action;
	}
	
	public ArrayList<ArrayList<Point>> generateEpisode( Map<Point, Map<Point, Double>> eSoftPol ) {
		ArrayList<ArrayList<Point>> episode = new ArrayList<ArrayList<Point>>(); 
		pred = new Predator();
		prey = new Prey();
		
		Point state = new Point();
		state.x = prey.pos.x - pred.pos.x;
		state.y = prey.pos.y - pred.pos.y;
		state = pred.checkDirections( state );
		boolean notTerminal = true;
		while( notTerminal ) {
			ArrayList<Point> timeStep = new ArrayList<Point>();
			timeStep.add( state );

			Point moveCoordsPred = getActionSoftPol( eSoftPol, state );
			pred.newPos( moveCoordsPred );
			timeStep.add( moveCoordsPred );
			
			Point newState = new Point();
			newState.x = prey.pos.x - pred.pos.x;
			newState.y = prey.pos.y - pred.pos.y;
			newState = pred.checkDirections( newState );
			Point returnT = new Point(0,0);
			if( newState.equals(new Point(0,0) ) ) {
				returnT.x = 10;
				notTerminal = false;
			} else {
				String predNear = prey.checkPred( pred );
				String preyMove = prey.getMove( predNear );
				Point moveCoordsPrey = move( preyMove );
				prey.newPos( moveCoordsPrey );
			}
			
			timeStep.add( returnT );
			
			episode.add( timeStep ); 
			state = (Point) newState.clone();
		}
		return episode;
	}
	
	private void initNDvalues( ) {
		int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
		Map<Point, Double> actionVal = new HashMap<Point, Double>();
		for(int i = 0; i < pred.actions.length; i++){
			actionVal.put(move(pred.actions[i]), 0.0);
		}
		for( int i = 0; i < 11; i++ ) {
			for( int j = 0; j < 11; j++ ) {
				Point directionVector = 
						new Point( directionValues[i], directionValues[j] );
				Map<Point, Double> actionVals;
				actionVals = new HashMap<Point, Double>( actionVal );

				Nvalues.put( directionVector, actionVals );
				Dvalues.put( directionVector, actionVals );
			}
		}
	}
	
	private void initDetPolicy( ) {
		Random rand = new Random();
		for( Map.Entry<Point, Map<Point, Double>> entry : Qvalues.entrySet() ) {
			int move = rand.nextInt(5);
			Point state = entry.getKey();
			detPolicy.put( state, move( pred.actions[move] ) );
		}
	}
	
	private double computeW( ArrayList<ArrayList<Point>> episode, int firstOcc,
			Map<Point, Map<Point, Double>> eSoftPol ) {
		double w = 1;
		for( int i = firstOcc + 1; i < episode.size(); i++ ) {
			Point state = episode.get(i).get(0);
			Point action = episode.get(i).get(1);
			Map<Point, Double> actions = eSoftPol.get( state );
			double actionProb = actions.get( action );
			w *= 1 / actionProb;
		}
		return w;
	}
	
	private int getReturn( ArrayList<ArrayList<Point>> episode, int firstT, 
			double discountFactor ) {
		int returnT = 0;
		int count = 0;
		for( int i = firstT; i < episode.size(); i++ ) {
			ArrayList<Point> timeStep = episode.get(i);
			returnT += Math.pow(discountFactor, count) * timeStep.get(2).x;
			count++;
		}
		return returnT;
	}
	
	private void updateQMC( ArrayList<ArrayList<Point>> episode, int tau, 
			Map<Point, Map<Point, Double>> eSoftPol, double discountFactor ) {
		Map<List<Point>, Integer> firstOccurs = findFirstOccurs( episode, tau );
		
		for( Map.Entry<List<Point>, Integer> entry : firstOccurs.entrySet() ) {
			Point key = entry.getKey().get(0);
			if( !stateCounts.containsKey( key ) ) {
				stateCounts.put( key, 1 );
			} else {
				stateCounts.put( key, stateCounts.get(key) +  1 );
			}
		}
		
		for( int i = tau; i < episode.size(); i++ ) {
			Point state = episode.get(i).get(0);
			Point action = episode.get(i).get(1);
			List<Point> key = new ArrayList<Point>();
			key.add(state); key.add(action);
			int firstOcc = firstOccurs.get( key );
			if( firstOcc == i ) {
				int returnT = getReturn( episode, firstOcc, discountFactor );
				double w = computeW( episode, firstOcc, eSoftPol );
				Map<Point, Double> Nval = 
						new HashMap<Point, Double>( Nvalues.get( state ) );
				Nval.put( action, Nval.get(action) + (double) w*returnT );
				Nvalues.put( state, Nval );
				Map<Point, Double> Dval =
						new HashMap<Point, Double>( Dvalues.get( state ) );
				Dval.put(action, Dval.get(action) + w );
				Dvalues.put( state, Dval );
				
	//			Map<Point, Double> Qupd = Qvalues.get( state );
	//			Qupd.put( action, Nvalues.get( state ).get( action ) / 
	//					Dvalues.get( state ).get( action ) );
				Qvalues.get(state).put( action, Nvalues.get( state ).get( action ) / 
						Dvalues.get( state ).get( action )  );
			}
		}
	}
	
	private Map<List<Point>, Integer> findFirstOccurs( ArrayList<ArrayList<Point>> episode, int tau ) {
		Map<List<Point>, Integer> firstOccurs = new HashMap<List<Point>, Integer>();
		for( int i = tau; i < episode.size(); i++ ) {
			Point state = episode.get(i).get(0);
			Point action = episode.get(i).get(1);
			List<Point> key = new ArrayList<Point>();
			key.add(state); key.add(action);
			if( !firstOccurs.containsKey(key) ) {
				firstOccurs.put(key, i);
			}
		}
		return firstOccurs;
	}
	
	private void updateDetPolicy() {
		for( Map.Entry<Point, Map<Point, Double>> entry : Qvalues.entrySet() ) {
			Point state = entry.getKey();
			if( state.equals(new Point(0,0)))
				continue;
			Map<Point, Double> actions = entry.getValue();
			double bestVal = 0;
			int actionIndex = -1;
			for( int i = 0; i < actions.size(); i++ ) {
				double value = actions.get( move( pred.actions[i] ) );
				if( value > bestVal ) {
					bestVal = value;
					actionIndex = i;
				}
			}
			detPolicy.put( state, move(pred.actions[actionIndex] ));
		}
	}
	
	public void printDetPolicy( Map<Point, Point> map, Point preyLoc ) {
		String[][] board = new String[11][11];
		for( Map.Entry<Point, Point> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Point move = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			String bestAction = "";
			if(!nextLoc.equals(preyLoc)){
				if( move.equals(new Point(-1,0) ) ) {
					bestAction = "WEST";
				} else {
					if( move.equals(new Point(1,0) ) ) {
						bestAction = "EAST";
					} else {
						if( move.equals(new Point(0,-1) ) ) {
							bestAction = "NORTH";
						} else {
							if( move.equals(new Point(0,1) ) ) {
								bestAction = "SOUTH";
							} else {
								if( move.equals(new Point(0,0) ) ) {
									bestAction = "WAIT";
								}
							}
						}
					}
				}
			}
			board[nextLoc.y][nextLoc.x] = bestAction;
		}
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	
	public void offPolicyMonteCarlo( double discountFactor, int nEpisodes ) {
		initQvalues( -15.0 );
		initNDvalues();
		initDetPolicy();
		for( int k = 0; k < nEpisodes; k++ ) { 
			Map<Point, Map<Point, Double>> eSoftPol = initEpsilonSoftPolicy();
			ArrayList<ArrayList<Point>> episode = generateEpisode( eSoftPol );
			for( int i = episode.size() - 1; i >= 0; i-- ) {
				ArrayList<Point> timeStep = episode.get( i );
				Point state = timeStep.get( 0 );
				Point action = timeStep.get( 1 );
				Point detPolAction = detPolicy.get( state );
				
				if( !action.equals( detPolAction ) ) {
					System.out.println(episode.size() - i);
					updateQMC( episode, i, eSoftPol, discountFactor );
					break;
				}
			}
			
			updateDetPolicy();
			//printBoardQ( Qvalues, new Point(5,5) );
		}
		printDetPolicy( detPolicy, new Point(5,5) );

		
	}
	
	
}