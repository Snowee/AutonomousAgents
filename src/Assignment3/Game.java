package Assignment3;
import java.awt.Point; 
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Game {
	private Predator pred;
	private Prey prey;
	private boolean endState;
	//public List<Map<String, Map<Point, Double>>> QvalList;
	public Map<String, Integer> stateIndex;
	public List<double[][]> QvalList;
	//public Map<String, Map<Point, Double>> QvalPrey;
	public double[][] QvalPrey;
	public int numPreds;
	public List<List<Point>> statesArray;
	public ArrayList<Predator> preds;
	public static List<List<Point>> statePermutations;
	public static List<Point> singleStatePoints;
	int predWins;
	int preyWins;
	
	//private List<Double> rewardList = new ArrayList<Double>();
	//private Map<List<Point>, Boolean> appearanceList = 
	//		new HashMap<List<Point>, Boolean>();
	//private Map<Point, Point> appearanceListS = 
	//		new HashMap<Point, Point>();
	//Map<Point, Integer> stateCounts;
	
	// Constructor for a game object
	public Game( int numPreds ) {
		if( numPreds > 4 || numPreds < 1 ){
			System.err.println("Number of predators not appropriate");
			System.exit(0);
		} else{
			this.numPreds = numPreds;
			endState = false;
			// Initialize all data structures to be used
			//QvalList = new ArrayList<Map<String, Map<Point, Double>>>();
			QvalList = new ArrayList<double[][]>();
			/*
			for( int i = 0; i < numPreds; i++ ){
				QvalList.add(new HashMap<String, Map<Point, Double>>());
			}*/
			//QvalPrey = new HashMap<String, Map<Point, Double>>();
			
			statesArray = new ArrayList<List<Point>>();
			pred = new Predator(false, 0);
			preds = new ArrayList<Predator>();
			prey = new Prey(false);
			predWins = 0;
			preyWins = 0;
			stateIndex = new HashMap<String, Integer>();
		}
	}
	
	// Function to run a single game, each turn starting by a move of the prey
	// then a move of the predator and then a check to see if the predator
	// caught the prey. 
	// Input: boolean randomPolicy - true for selecting random moves
	//								 false for good policy moves
	// 		  boolean reduction - true to use the reduced state space
	//							  false to use full state space
	public int start( boolean randomPolicy ) {
		endState = false;
		Prey prey = new Prey( false );
		
		for( int i = 0; i < numPreds; i++ ) {
			preds.add( new Predator( false, i ) );
		}
		
		int counter = 0;
		printGameState( preds, prey.pos );
		String wonBy = "";
		while( endState == false ) {
			
			// Predator moves
			for( int i = 0; i < preds.size(); i++ ) {
				String move = preds.get(i).getMove();
				Point movePoint = move(move);
				preds.get(i).newPos( movePoint );
			}
			//Uncomment below for print statement to see movement of predator(s)
			//System.out.println("\nPredator move(s)");
			//printGameState( preds, prey.pos );
			
			// Prey move
			String preyMove = prey.getMove();
			Point moveCoordsPrey = move( preyMove );
			prey.newPos( moveCoordsPrey );
			// Uncomment below for print statement to see movement of prey
			//System.out.println("\nPrey move");
			//printGameState( preds, prey.pos);

			//Uncomment below for print statement to see result of movements
			// of both prey and predator(s)
			printGameState(preds, prey.pos);
			
			// nr of steps
			counter += 1;
			
			//check if end of game
			wonBy = checkStatus(preds, prey);
		}
		System.out.printf( "Game ended in %d steps\n", counter );
		System.out.println( "Game won by: " + wonBy );
		if( wonBy.endsWith("Pred") ) {
			predWins++;
		} else {
			preyWins++;
		}
		return counter;
	}
	
	/*
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
	}*/
	// Function to print the Point pred and Point prey locations in a board
		public void printGameState( List<Predator> preds, Point prey ) {
			String[][] board = new String[11][11];
			board[prey.y][prey.x] = "q";
			for( int i = 0; i < preds.size(); i++ ) {
				board[preds.get(i).pos.y][preds.get(i).pos.x] = "P" + i;
			}
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
	public String checkStatus( ArrayList<Predator> preds, Prey prey) {
		for( int i = 0; i < preds.size(); i++ ) {
			if( preds.get(i).pos.equals( prey.pos ) ) {
				endState = true;
				return "Pred";
			}
		}
		
		for( int i = 0; i < preds.size(); i++ ) {
			for( int j = i + 1; j < preds.size(); j++ ) {
				if( preds.get(i).pos.equals(preds.get(j).pos) ) {
					endState = true;
					return "Prey";
				}
			}
		}
		return "";
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
	
	// Function to print the maximum Qvalue for each state
	public void printboardQmax(Map<Point, Map<Point, Double>> map, Point preyLoc){
		double[][] board = new double[11][11];
		for( Map.Entry<Point,Map<Point, Double>> entry : map.entrySet() ) {
			Point state = entry.getKey();
			Map<Point, Double> value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			double max = 0;
			// loop through the Q values per state to find the max
			for(int i = 0; i < pred.actions.length; i++){
				if(value.get(move(pred.actions[i])) > max ){
					max = value.get(move(pred.actions[i]));
				}
			}
			board[nextLoc.y][nextLoc.x] = max;
		}
		// print found values
		for( double[] row : board ) {
	        for( double r : row ) {
	        	System.out.printf( "%f\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	// Function to print the action that corresponds to the maximum
	// Q value in each state
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
				// get the max Q value and use that index to 
				// find the corresponding move
				for(int i = 0; i < pred.actions.length; i++){
					if(value.get(move(pred.actions[i])) > max ){
						bestAction = pred.actions[i];
						max = value.get(move(pred.actions[i]));
					}
				}
			}
			board[nextLoc.y][nextLoc.x] = bestAction;
		}
		// print all found moves
		for( String[] row : board ) {
	        for( String r : row ) {
	        	System.out.printf( "%s\t", r );
	        }
	        System.out.println();
	    }
		System.out.println();
	}

	// Print all five Qvalues per state, corresponding to the possible
	// actions of the predator in the order: WAIT, NORTH, EAST, SOUTH, WEST
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
		// print all values
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
	
	
	/*
	// Get the maximizing action in state s
	private Point getArgmaxActionQval(Point s){
		Map<Point, Double> actVals = Qvalues.get(s);
		double maxVal = 0;
		int bestActionInd = -1;
		for(int i = 0; i < pred.actions.length; i++){
			double val = actVals.get(move(pred.actions[i]));
			if( val >= maxVal ){
				maxVal = val;
				bestActionInd = i;
			}
		}
		Point bestAction = move(pred.actions[bestActionInd]);
		return bestAction;
	}*/
	
	
	
	/*
	// function to select an action from the epsilon soft policy
	// given a certain state
	private Point getActionSoftPol(Map<Point, Map<Point, Double>> policy, Point s){
		Random rand = new Random();
		double chance = rand.nextDouble();
		Map<Point, Double> actionProbs = policy.get(s);
		List<Double> probs = new ArrayList<Double>();
		// get the probabilities for each action from the policy
		// and sum them cumulatively
		probs.add(actionProbs.get(move(pred.actions[0])));
		for( int i = 1; i < pred.actions.length; i++ ) {
			probs.add(probs.get(i-1) + actionProbs.get(move(pred.actions[i])));
		}
		// use the random double [0,1) to choose an action
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
	
	
	// Function to initialize an epsilon soft policy
	// assigning random probabilities (> 1) to each action
	// summing probabilities for all actions to 1
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
	
	// Function to get normalized probabilities (summing to 1)
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
	*/
	
	private double computeQvalueSarsa(List<Point> s, Point action, double alpha, 
			double discountFactor, List<Point> sPrime, Point actionPrime, int reward, double[][] Qvalues){
		double qval;
		double oldqval = Qvalues[stateIndex.get(s.toString())][pred.possMoves.indexOf(action)];
		double sPrimeQval = Qvalues[stateIndex.get(sPrime.toString())][pred.possMoves.indexOf(actionPrime)];
		qval = oldqval + alpha*(reward + (discountFactor*sPrimeQval) - oldqval);
		
		return qval;
	}
	
	// Function implementing the Sarsa algorithm
		public List<double[]> Sarsa(double alpha, double alphaPrey, double discountFactor, double discountFactorPrey, int nEpisodes, boolean greedy,
				double initQval, double epsilon, double epsilonPrey, double temperature, double temperaturePrey, boolean randomInitState){
			List<double[]> data = new ArrayList<double[]>();
			double[] epCount = new double[nEpisodes];
			double[] preyRew = new double[nEpisodes];
			double[] predsRew = new double[nEpisodes];
			List<Double> stepsBump = new ArrayList<Double>();
			List<Double> epIndBump = new ArrayList<Double>();
			List<Double> stepsCatch = new ArrayList<Double>();
			List<Double> epIndCatch = new ArrayList<Double>();
			double preyCumRew = 0;
			double predsCumRew = 0;
			double[] predWins = new double[nEpisodes];
			double[] preyWins = new double[nEpisodes];
			double predWinsCum = 0;
			double preyWinsCum = 0;
			
			for( int i = 0; i < numPreds; i++ ){
				QvalList.add(initQvaluesMA(initQval, numPreds));
			}
			// Also initialize Qvalues for prey
			QvalPrey = initQvaluesMA(initQval, numPreds);
			System.out.println("Initialized Q-values for Predator(s) and Prey");
			
			for( int i = 0; i < nEpisodes; i++){
				List<Point> state;
				if(randomInitState){
					state = initS(numPreds);
				} else{
					state = new ArrayList<Point>();
					for( int p = 0; p < numPreds; p++ ){
						state.add( (Point) pred.predStartLocSt[p].clone());
					}
				}
				System.out.printf("Initial state for episode %d initialized\n", i+1);
				boolean inTerminalState = false;
				int stepCounter = 0;
				List<Point> actions = new ArrayList<Point>();
				Point actionPrey;
				if( greedy ){
					actionPrey = getActionGreedyPrey(epsilonPrey, state, QvalPrey);
				} else{
					actionPrey = getActionSoftmaxPrey(temperaturePrey, state, QvalPrey);
				}
				for( int a = 0; a < numPreds; a++ ){
					Point action = new Point(7,7);
					if(greedy){
						action = getActionGreedy(epsilon, state, QvalList.get(a));
					} else{
						action = getActionSoftmax(temperature, state, QvalList.get(a));
					}
					actions.add(action);
				}
				while(!inTerminalState){
					stepCounter = stepCounter + 1;
					
					List<Point> sPrime = new ArrayList<Point>();
					// Observe reward and state s' after taking action a
					for( int a = 0; a < numPreds; a++ ){
						Point dTemp = (Point) state.get(a).clone();
						dTemp.translate(actionPrey.x, actionPrey.y); // influence of prey move on pred direction vector
						dTemp.translate(-1*actions.get(a).x, -1*actions.get(a).y); // influence of pred move on own direction vector
						sPrime.add(pred.checkDirections(dTemp));
					}
					int[] rewardsAndTerminal = interactWithEnvRew(sPrime);
					int rewardPred = rewardsAndTerminal[0];
					int rewardPrey = rewardsAndTerminal[1];
					if( rewardsAndTerminal[2] == 1 || rewardsAndTerminal[2] == 2 ){
						inTerminalState = true;
						epCount[i] = i;
						preyCumRew += rewardPrey;
						predsCumRew += rewardPred;
						preyRew[i] = preyCumRew;
						predsRew[i] = predsCumRew;
						// Predators caught prey
						if( rewardsAndTerminal[2] == 1){
							predWinsCum++;
							predWins[i] = predWinsCum;
							preyWins[i] = preyWinsCum;
							stepsCatch.add((double)stepCounter);
							epIndCatch.add((double)i);
						} else{ // Predators bumped into each other
							preyWinsCum++;
							predWins[i] = predWinsCum;
							preyWins[i] = preyWinsCum;
							stepsBump.add((double) stepCounter);
							epIndBump.add((double) i);
						}
					}
					// Choose a' from s' using policy derived from Q
					List<Point> actionsP = new ArrayList<Point>();
					Point actionPreyP; 
					if( greedy ){
						actionPreyP = getActionGreedyPrey(epsilonPrey, state, QvalPrey);
					} else{
						actionPreyP = getActionSoftmaxPrey(temperaturePrey, state, QvalPrey);
					}
					for( int a = 0; a < numPreds; a++ ){
						Point action = new Point(7,7);
						if(greedy){
							action = getActionGreedy(epsilon, state, QvalList.get(a));
						} else{
							action = getActionSoftmax(temperature, state, QvalList.get(a));
						}
						actionsP.add(action);
					}
					
					// Compute Q-value for current state-action s,a for each predator
					for( int a = 0; a < numPreds; a++ ){
						double newQval = computeQvalueSarsa(state, actions.get(a), 
								alpha, discountFactor, sPrime, actionsP.get(a), rewardPred, QvalList.get(a) );
						QvalList.get(a)[stateIndex.get(state.toString())][pred.possMoves.indexOf(actions.get(a))] = newQval;
					}
					// Compute Q-value for current state-action s,a for prey
					double newQvalPrey = computeQvalueSarsa(state, actionPrey, alphaPrey, 
							discountFactorPrey, sPrime, actionPreyP, rewardPrey, QvalPrey );
					QvalPrey[stateIndex.get(state.toString())][prey.possMoves.indexOf(actionPrey)] = newQvalPrey;
					
					// State = State'
					// Joint action = Joint action'
					state.clear();
					state.addAll(sPrime);
					actions.clear();
					actions.addAll(actionsP);
				}	
			}
			double[] stepsCatchArray = new double[stepsCatch.size()];
			double[] epIndCatchArray = new double[epIndCatch.size()];
			double[] stepsBumpArray = new double[stepsBump.size()];
			double[] epIndBumpArray = new double[epIndBump.size()];
			for(int i = 0; i < stepsCatch.size(); i++ ){
				stepsCatchArray[i] = stepsCatch.get(i);
				epIndCatchArray[i] = epIndCatch.get(i);
			}
			for(int i = 0; i < stepsBump.size(); i++ ){
				stepsBumpArray[i] = stepsBump.get(i);
				epIndBumpArray[i] = epIndBump.get(i);
			}
			data.add(epCount);
			data.add(preyRew);
			data.add(predsRew);
			data.add(preyWins);
			data.add(predWins);
			data.add(epIndCatchArray);
			data.add(stepsCatchArray);
			data.add(epIndBumpArray);
			data.add(stepsBumpArray);
			
			return data;
		}
		
	
	private Point getActionSoftmaxPrey(double temperature,
				List<Point> state, double[][] Qvalues) {
		Random rand = new Random();
		Point action = new Point();
		double trip = rand.nextDouble();
		// Prey trips, so action = wait
		if( trip < 0.2 ){
			action = new Point(0,0);
		} else{
			action = getActionSoftmax(temperature, state, Qvalues);
		}
		return action;
	}

	private Point getActionGreedyPrey(double epsilon, List<Point> state,
				double[][] Qvalues) {
		Random rand = new Random();
		Point action = new Point();
		double trip = rand.nextDouble();
		// Prey trips, so action = wait
		if( trip < 0.2 ){
			action = new Point(0,0);
		} else{
			action = getActionGreedy(epsilon, state, Qvalues);
		}
		
		return action;
	}

	// Function implementing the Q-learning algorithm
	public List<double[]> qlearning(double alpha, double alphaPrey, double discountFactor, double discountFactorPrey, int nEpisodes, boolean greedy,
			double initQval, double epsilon, double epsilonPrey, double temperature, double temperaturePrey, boolean randomInitState){
		List<double[]> data = new ArrayList<double[]>();
		double[] epCount = new double[nEpisodes];
		double[] preyRew = new double[nEpisodes];
		double[] predsRew = new double[nEpisodes];
		double[] predWins = new double[nEpisodes];
		double[] preyWins = new double[nEpisodes];
		double predWinsCum = 0;
		double preyWinsCum = 0;
		List<Double> stepsBump = new ArrayList<Double>();
		List<Double> epIndBump = new ArrayList<Double>();
		List<Double> stepsCatch = new ArrayList<Double>();
		List<Double> epIndCatch = new ArrayList<Double>();
		double preyCumRew = 0;
		double predsCumRew = 0;
		
		System.out.println("Initializing Q-values for Predator(s) and Prey....");
		for( int i = 0; i < numPreds; i++ ){
			QvalList.add(initQvaluesMA(initQval, numPreds));
		}
		// Also initialize Qvalues for prey
		QvalPrey = initQvaluesMA(initQval, numPreds);
		System.out.println("Initialized Q-values for Predator(s) and Prey");
		
		for( int i = 0; i < nEpisodes; i++){
			List<Point> state;
			if(randomInitState){
				state = initS(numPreds);
			} else{
				state = new ArrayList<Point>();
				for( int p = 0; p < numPreds; p++ ){
					state.add( (Point) pred.predStartLocSt[p].clone());
				}
			}
			System.out.printf("Initial state for episode %d initialized\n", i+1);
			boolean inTerminalState = false;
			int stepCounter = 0;
			while(!inTerminalState){
				stepCounter = stepCounter + 1;
				List<Point> actions = new ArrayList<Point>();
				Point actionPrey;
				if( greedy ){
					actionPrey = getActionGreedyPrey(epsilonPrey, state, QvalPrey);
				} else{
					actionPrey = getActionSoftmaxPrey(temperaturePrey, state, QvalPrey);
				}
				List<Point> sPrime = new ArrayList<Point>();
				for( int a = 0; a < numPreds; a++ ){
					Point action = new Point(7,7);
					if(greedy){
						action = getActionGreedy(epsilon, state, QvalList.get(a));
					} else{
						action = getActionSoftmax(temperature, state, QvalList.get(a));
					}
					actions.add(action);
					Point dTemp = (Point) state.get(a).clone();
					dTemp.translate(actionPrey.x, actionPrey.y); // influence of prey move on pred direction vector
					dTemp.translate(-1*action.x, -1*action.y); // influence of pred move on own direction vector
					sPrime.add(pred.checkDirections(dTemp));
				}
				int[] rewardsAndTerminal = interactWithEnvRew(sPrime);
				int rewardPred = rewardsAndTerminal[0];
				int rewardPrey = rewardsAndTerminal[1];
				if( rewardsAndTerminal[2] == 1 || rewardsAndTerminal[2] == 2 ){
					inTerminalState = true;
					epCount[i] = i;
					preyCumRew += rewardPrey;
					predsCumRew += rewardPred;
					preyRew[i] = preyCumRew;
					predsRew[i] = predsCumRew;
					// Predators caught prey
					if( rewardsAndTerminal[2] == 1){
						predWinsCum++;
						predWins[i] = predWinsCum;
						preyWins[i] = preyWinsCum;
						stepsCatch.add((double)stepCounter);
						epIndCatch.add((double)i);
					} else{ // Predators bumped into each other
						preyWinsCum++;
						predWins[i] = predWinsCum;
						preyWins[i] = preyWinsCum;
						stepsBump.add((double) stepCounter);
						epIndBump.add((double) i);
					}
				}
				
				// Compute Q-value for current state-action s,a for each predator
				for( int a = 0; a < numPreds; a++ ){
					double newQval = computeQvalueQL(state, actions.get(a), 
							alpha, discountFactor, sPrime, rewardPred, QvalList.get(a) );
					QvalList.get(a)[stateIndex.get(state.toString())][pred.possMoves.indexOf(actions.get(a))] = newQval;
				}
				// Compute Q-value for current state-action s,a for prey
				double newQvalPrey = computeQvalueQL(state, actionPrey, alphaPrey, 
						discountFactorPrey, sPrime, rewardPrey, QvalPrey );
				QvalPrey[stateIndex.get(state.toString())][prey.possMoves.indexOf(actionPrey)] = newQvalPrey;
				
				// State = State'
				state.clear();
				state.addAll(sPrime);
			}
			
			//System.out.println(stepCounter);
		}
		double[] stepsCatchArray = new double[stepsCatch.size()];
		double[] epIndCatchArray = new double[epIndCatch.size()];
		double[] stepsBumpArray = new double[stepsBump.size()];
		double[] epIndBumpArray = new double[epIndBump.size()];
		for(int i = 0; i < stepsCatch.size(); i++ ){
			stepsCatchArray[i] = stepsCatch.get(i);
			epIndCatchArray[i] = epIndCatch.get(i);
		}
		for(int i = 0; i < stepsBump.size(); i++ ){
			stepsBumpArray[i] = stepsBump.get(i);
			epIndBumpArray[i] = epIndBump.get(i);
		}
		data.add(epCount);
		data.add(preyRew);
		data.add(predsRew);
		data.add(preyWins);
		data.add(predWins);
		data.add(epIndCatchArray);
		data.add(stepsCatchArray);
		data.add(epIndBumpArray);
		data.add(stepsBumpArray);
		return data;
	}
	
	private int[] interactWithEnvRew( List<Point> state){
		int[] rewardsAndTerminal = new int[3];
		boolean terminalSt = false;
		boolean bump = false;
		int rewardPreds = 0;
		int rewardPrey = 0;
		
		for(int i = 0; i < state.size(); i++){
			// If two predators bump into each other the episode ends and 
			// the prey gets away even when it was caught by another agent
			if( Collections.frequency(state, state.get(i)) > 1 ){
				terminalSt = true;
				bump = true;
				rewardPreds = -10;
				rewardPrey = 10;
				break;
			}
			if( state.get(i).equals( new Point(0,0) ) ){
				terminalSt = true;
				rewardPreds = 10;
				rewardPrey = -10;
			}
		}
		
		if(terminalSt){
			if(!bump){ // Predator(s) caught prey
				rewardsAndTerminal[2] = 1;
			} else{
				rewardsAndTerminal[2] = 2;
			}
		}
		
		rewardsAndTerminal[0] = rewardPreds;
		rewardsAndTerminal[1] = rewardPrey;
		return rewardsAndTerminal;
	}
	/*
	private String computePreyState( List<Point> sPreds ){
		String preyState = null;
		
		return preyState;
	}*/
	
	/*
	private Map<String, Map<Point, Double>> initQvalPrey(){
		Map<String, Map<Point, Double>> initQ = new HashMap<String, Map<Point, Double>>();
		
		return initQ;
	}*/
	 /*
	// Compute new Q-value for state-action pair s,a for prey
	private double computeQvalueQLPrey(String s, Point action, double alpha, 
			double discountFactor, String sPrime, int reward, Map<String, Map<Point, Double>> Qvalues ){
		double oldqval = Qvalues.get(s.toString()).get(action);
		Set<Point> sPrimeActions = Qvalues.get(sPrime.toString()).keySet();
		double maxPrimeQval = 0;
		for(Point actionPrime : sPrimeActions){
			if(Qvalues.get(sPrime.toString()).get(actionPrime) >= maxPrimeQval){
				maxPrimeQval = Qvalues.get(sPrime.toString()).get(actionPrime);
			}
		}
		double qval = oldqval + alpha*(reward + 
				(discountFactor*maxPrimeQval) - oldqval);
		
		return qval;
	}*/
	
	// Compute Q-value in Qlearning for state s (for predator)
	private double computeQvalueQL(List<Point> s, Point action, double alpha, 
			double discountFactor, List<Point> sPrime, int reward, double[][] Qvalues){
		double oldqval = Qvalues[stateIndex.get(s.toString())][pred.possMoves.indexOf(action)];
		double[] sPrimeActVal = Qvalues[stateIndex.get(sPrime.toString())];
		double maxPrimeQval = 0;
		for(int i = 0; i < sPrimeActVal.length; i++){
			if(sPrimeActVal[i] >= maxPrimeQval){
				maxPrimeQval = sPrimeActVal[i];
			}
		}
		double qval = oldqval + alpha*(reward + 
				(discountFactor*maxPrimeQval) - oldqval);
		
		return qval;
	}
	
	// Get epsilon-greedy action for predator
	//private Point getActionGreedy(double epsilon, List<Point> state, Map<String, Map<Point, Double>> Qvalues){
	private Point getActionGreedy(double epsilon, List<Point> state, double[][] Qvalues){
		Random rand = new Random();
		double chance = rand.nextDouble();
		Point action = new Point();

		// Select random action with probability epsilon
		if(chance < epsilon){
			//System.out.println("Random action");
			action = pred.possMoves.get(rand.nextInt(pred.possMoves.size()));
		}
		// Select optimal action
		else{
			double maxVal = 0;
			//Map<Point, Double> valActFromState = Qvalues.get(state.toString());
			double[] valActFromState = Qvalues[stateIndex.get(state.toString())];
			for(int i = 0; i < valActFromState.length; i ++){
				if( valActFromState[i] >= maxVal ){
					action = pred.possMoves.get(i);
					maxVal = valActFromState[i];
				}
			}
		}
		
		return action;
	}
		
	// Function to select an action according to softmax algorithm
	// dependent on the temperature term the Q values will have 
	// more (lower temperature) or less (higher temperature) influence
	// on the probabilities of the corresponding actions
	//private Point getActionSoftmax(double temperature,  List<Point> state, Map<String, Map<Point, Double>> Qvalues ) {
	private Point getActionSoftmax(double temperature,  List<Point> state, double[][] Qvalues ) {
		Random rand = new Random();
		double chance = rand.nextDouble();
		ArrayList<Double> softmaxProbs = softmaxProbabilities( temperature, state, Qvalues );
		int softmaxMove = -1;
		for( int i = 0; i < softmaxProbs.size(); i++ ) {
			if( chance < softmaxProbs.get(i) ) {
				softmaxMove = i;
				break;
			}
		}
		//Point move = move( pred.actions[softmaxMove] );
		Point move = pred.possMoves.get(softmaxMove);
		return move;
	}
	
	// Function to compute the softmax probabilities for a certain state
	private ArrayList<Double> softmaxProbabilities( double temperature, List<Point> state,  double[][] Qvalues ) {
		ArrayList<Double> softmaxProbs = new ArrayList<Double>();
		double[] valActFromState = Qvalues[stateIndex.get(state.toString())];
		double sum = 0;
		for(int i = 0; i < valActFromState.length; i++ ){
			sum += Math.exp( valActFromState[i] / temperature );
		}
		
		// compute each individual softmax probability for actions
		for(int i = 0; i < valActFromState.length; i++){
			double actVal = valActFromState[i];
			double prob = Math.exp(actVal/ temperature) / sum;
			softmaxProbs.add(prob);
		}

		// sum cumulatively to get chance boundaries
		for( int i = 1; i < softmaxProbs.size(); i++ ) {
			softmaxProbs.set( i, softmaxProbs.get(i-1) + softmaxProbs.get(i) );
		}
		return softmaxProbs;		
	}
	
	// get a state (=list of arbitrary starting positions for preds), which does
	// not equal the terminal state.
		private List<Point> initS( int numPreds ){
			List<Point> initState = new ArrayList<Point>();
			boolean validState = false;
			Random rand = new Random();
			while(!validState){
				List<Point> posState = statesArray.get(rand.nextInt(statesArray.size()));
				if( !posState.contains(new Point(0,0)) ){
					validState = true;
					initState.clear();
					initState.addAll(posState);
					boolean duplicate = true;
					while( duplicate ){
						duplicate = false;
						List<Point> removedDuplicate = new ArrayList<Point>();
						for(int i = 0; i < posState.size(); i++){
							removedDuplicate.add((Point) posState.get(i).clone());
							if(Collections.frequency(posState, posState.get(i)) > 1 && !duplicate ){
								validState = false;
								duplicate = true;
								Point temp = removedDuplicate.get(i);
								temp.translate((rand.nextInt(3)-1), (rand.nextInt(3)-1));
								removedDuplicate.set(i, pred.checkDirections(temp));
							}
						}
						if(!duplicate){
							validState = true;
						}
						posState = removedDuplicate;
					}
				}
			}
			
			return initState;
		}
		
		public void addToPermSet(int nAg, int callCount ){
			int indexPermList = 0;
			while(indexPermList < Math.pow(singleStatePoints.size(), nAg)){
				for(int j = 0; j < singleStatePoints.size(); j++ ){
					for(int r = 0; r < (int) Math.pow(singleStatePoints.size(), (nAg - callCount)); r++){
						statePermutations.get(indexPermList).add(singleStatePoints.get(j));
						indexPermList++;
					}
				}
			}
			/*
			for( int i = 0; i < (int) Math.pow(singleStatePoints.size(), numag); i++){
				for(int j = 0; j < singleStatePoints.size(); j++ ){
					for(int r = 0; r < (int) Math.pow(singleStatePoints.size(), (numag - callCount)); r++){
						statePermutations.get(i).add(c)
					}	
				}
			}*/
			
			/*
			for( int i = 0; i < (int) Math.pow(singleStatePoints.size(), callCount); i++ ) {
				for( int j = 0; j < (int)Math.pow(singleStatePoints.size(), layerNr-1); j++ ) {
					int index = i * (int)Math.pow(singleStatePoints.size(), layerNr-1) + j;
					statePermutations.get(index).add(singleStatePoints.get((int) Math.pow(i, (1/callCount))));
				}
			}
			*/
		}
		
		public void addFinalLayer() {
			for( int j = 0; j < statePermutations.size()/singleStatePoints.size(); j++ ) {
				for( int i = 0; i < singleStatePoints.size(); i++ ) {
					int index = j * singleStatePoints.size() + i;
					statePermutations.get(index).add(singleStatePoints.get(i));
				}
			}
		}
		
		public void initStateSpace( int nrPreds ) {
			for( int i = 0; i < (int) Math.pow(singleStatePoints.size(), nrPreds); i++ ) {
				statePermutations.add(new ArrayList<Point>());
			}
		}
		// Initialize reduced state space
		//public Map<String, Map<Point, Double>> initQvaluesMA(double initialQvalues, int nrPreds) {
		public double[][] initQvaluesMA(double initialQvalues, int nrPreds) {
			double[][] Qvalues;
			//Map<String, Map<Point,Double>> Qvalues = new HashMap<String, Map<Point,Double>>();
			singleStatePoints = new ArrayList<Point>();
			statePermutations = new ArrayList<List<Point>>();
			
			int[] directionValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
			double[] actionValterm = new double[5];
			double[] actionVal = new double[5];

		    if( statesArray.isEmpty() ) {
				for( int i = 0; i < 11; i++ ) {
					for( int j = 0; j < 11; j++ ) {
						Point directionVector = 
								new Point( directionValues[i], directionValues[j] );
						singleStatePoints.add(directionVector);
					}
				}
				
				initStateSpace( nrPreds );
				int callCount = 1;
				//for( int i = nrPreds; i > 1; i-- ) {
				for(int i = 0; i < nrPreds; i++ ){
					//addToPermSet( i, callCount );
					addToPermSet(nrPreds, callCount);
					callCount++;
				}
				
			//	addFinalLayer();
				
//				for( int i = 0; i < singleStatePoints.size(); i++ ) {
//					for( int j = 0; j < singleStatePoints.size(); j++ ) {
//						for( int k = 0; k < singleStatePoints.size(); k++ ) {
//							for( int m = 0; m < singleStatePoints.size(); m++ ) {
//								List<Point> permutation = new ArrayList<Point>();
//								permutation.add( singleStatePoints.get(i) );
//								permutation.add( singleStatePoints.get(j) );
//								permutation.add( singleStatePoints.get(k) );
//								permutation.add( singleStatePoints.get(m) );
//								statePermutations.add(permutation);
//							}
//						}
//					}
//				}
				statesArray = statePermutations;

		    } else{
		    	statePermutations = statesArray;
		    }
		    
		    	/*else {
		    	for( List<Point> state : statesArray ) {
		    		List<Point> copyState = new ArrayList<Point>();
		    		for( Point stateElem : state ) {
		    			copyState.add( (Point) stateElem.clone() );
		    		}
		    		statePermutations.add(copyState);
		    	}
		    }*/
			Qvalues = new double[statePermutations.size()][5];
			for(int k = 0; k < pred.actions.length; k++ ){
				actionValterm[k] = 0.0;
			}
			/*
			for(int k = 0; k < pred.actions.length; k++){
				actionValterm.put(move(pred.actions[k]), 0.0);
			}*/
			// Random initial Q values
			if(initialQvalues < 0.0){
				int stateIndexCount = 0;
				Random rand = new Random();
				for( int i = 0; i < statePermutations.size(); i++ ) {
					List<Point> state = statePermutations.get(i);
					//Map<Point, Double> actionQVal;
					double[] actionQVal;
					for( int j = 0; j < state.size(); j++ ) {
						if( Collections.frequency( state, state.get(j) ) > 1 
								|| state.get(j).equals(new Point(0,0) ) ) {
						//	actionQVal = new HashMap<Point,Double>(actionValterm);
							actionQVal = (double[]) actionValterm.clone();
							stateIndex.put(state.toString(), stateIndexCount);
							Qvalues[stateIndexCount] = (double[]) actionQVal.clone();
							stateIndexCount++;
							break;
						} else {
							for( int k = 0; k < pred.actions.length; k++ ) {
								actionVal[k] = (double) rand.nextInt(21);
							}
							actionQVal = (double[]) actionVal.clone();
							stateIndex.put(state.toString(), stateIndexCount);
							Qvalues[stateIndexCount] = (double[]) actionQVal.clone();
							stateIndexCount++;
							break;
						}	
					}
				}
			} else{
				int stateIndexCount = 0;
				for(int i = 0; i < pred.actions.length; i++){
					actionVal[i] = initialQvalues;
				}
				for( int i = 0; i < statePermutations.size(); i++ ) {
						List<Point> state = statePermutations.get(i);
						//Map<Point, Double> actionQVal;
						boolean zeroValue = false;
						for( int j = 0; j < state.size(); j++ ) {
							if( Collections.frequency( state, state.get(j) ) > 1 
									|| state.get(j).equals(new Point(0,0) ) ) {
								//actionQVal = new HashMap<Point,Double>(actionValterm);
								//Qvalues.put(state.toString(), actionQVal);
								zeroValue = true;
								break;
							}
						}
						if(zeroValue){
							Qvalues[stateIndexCount] = (double[]) actionValterm.clone();
							//Qvalues.put(state.toString(), new HashMap<Point, Double>(actionValterm));
						} else {
							Qvalues[stateIndexCount] = (double[]) actionVal.clone();
							//actionQVal = new HashMap<Point, Double>(actionVal);
							//Qvalues.put(state.toString(), actionQVal);
							//Qvalues.put(state.toString(), new HashMap<Point, Double>(actionVal));
						}
						stateIndex.put(state.toString(), stateIndexCount);
						stateIndexCount++;
				}
			}
			return Qvalues;
		}
}