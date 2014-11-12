import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	private Predator pred;
	private Prey prey;
	private boolean endState;
	public Map<List<Point>, Double> valueMap;
	public Map<Point, Double> stateSpace;
	public List<Point> allPredPos;
	public Map<List<Point>, String> bestPolicy;
	public Map<Point, String> reductionBestPolicy;
	private double theta = 0.00001;
	
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
		valueMap = new HashMap<List<Point>, Double>();
		stateSpace = new HashMap<Point, Double>();
		allPredPos = new ArrayList<Point>();
		bestPolicy = new HashMap<List<Point>, String>();
		reductionBestPolicy = new HashMap<Point, String>();
	}
	
	public int start( boolean randomPolicy, boolean reduction ) {
		int counter = 0;
		printGameState( pred.pos, prey.pos );
		while( endState == false ) {			
			String predNear = prey.checkPred( pred );
			String preyMove = prey.getMove( predNear );
			Point moveCoordsPrey = move( preyMove );
			prey.newPos( moveCoordsPrey );
			//printGameState(pred.pos, prey.pos);
			
			String predMove;
			if( randomPolicy ) {
				predMove = pred.getMove();
			}
			else {
				if( !reduction )
					predMove = bestPolicy.get(
							Arrays.asList( pred.pos, prey.pos ));
				else {
					Point state = new Point();
					state.x = prey.pos.x - pred.pos.x;
					state.y = prey.pos.y - pred.pos.y;
					state = pred.checkDirections(state);
					predMove = reductionBestPolicy.get( state );
				}
			}
			
			Point moveCoordsPred = move( predMove );
			pred.newPos( moveCoordsPred );
			counter += 1;
			//printGameState( pred.pos, prey.pos );
			checkStatus();
		}
		System.out.printf( "Game ended in %d steps\n", counter );
		return counter;
	}
	
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
	
	public void checkStatus() {
		if( pred.pos.equals( prey.pos ) ) {
			endState = true;
		}
	}
	
	public int calcDistance( Point state ) {
		return Math.abs( state.x ) + Math.abs( state.y );
	}
	
	public Point checkLoc( Point loc ) {
		if( !( loc.x >= 0 ) || !( loc.x < 11 ) ) {
			loc.x = ( loc.x + 11 ) % 11;
		}
		if( !( loc.y >= 0 ) || !( loc.y < 11 ) ) {
				loc.y = ( loc.y + 11 ) % 11;
		}
		return loc;
	}
	
	public Map<List<Point>, Double> policyEvaluation( double discountFactor, double[] policy ) {
		System.out.println( "Starting policy evaluation..." );
		System.out.println( "Initialize values for all states" );
		initValueMap();
		double deltaV = theta*2;
		int counter = 0;
		
		while( deltaV > theta ) {
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = new HashMap<List<Point>, Double>( valueMap );
			for (List<Point> key : valueMap.keySet()) {
				double oldValue = valueMap.get(key);
			    double newValue = computeValue(discountFactor, key.get(0), key.get(1), policy);
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
			counter = counter + 1;
		}
		
		List<Point> states = Arrays.asList(new Point(0,0), new Point(5,5), 
				new Point(3,2), new Point(4,5), new Point(10,2), 
				new Point(0,10), new Point(10,10), new Point(0,0));
		for(int i = 0; i < states.size(); i=i+2){
			List<Point> state = Arrays.asList(states.get(i), states.get(i+1));
			double value = valueMap.get(state);
			System.out.printf("Value of state Predator(%d,%d), Prey(%d,%d): %f\n",
					states.get(i).y, states.get(i).x, states.get(i+1).y, states.get(i+1).x, value );
		}	
		System.out.printf("Policy evaluation converged in %d iterations\n", counter);
		return valueMap;
	}
	
	public void policyIteration(double discountFactor){
		int counter = 0;
		Map<List<Point>, String> policy = initPolicy();
		initValueMap();
		boolean policyStable = false;
		while(!policyStable){
			policyEvaluationForIt(discountFactor, policy);
			Map<List<Point>, String> newPolicy = policyImprovement(discountFactor, policy);
			if(policy.entrySet().equals(newPolicy.entrySet())){
				policyStable = true;
			}
			else{
				policy = newPolicy;
			}
			counter = counter + 1;
		}
		bestPolicy = policy;
		printBoardActions(bestPolicy, new Point(5,5));
		printBoard(valueMap, new Point(5,5));
		System.out.printf("Policy iteration converged in %d iterations\n", counter);
	}
	
	public void policyEvaluationForIt(double discountFactor, Map<List<Point>, String> policy ){
		double deltaV = 2*theta;
		int counter = 1;
		while( deltaV > theta ){
			counter = counter + 1;
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = new HashMap<List<Point>, Double>(valueMap);
			for (List<Point> key : valueMap.keySet()) {
				double oldValue = valueMap.get(key);
			    double newValue = computeValueByPol(discountFactor, key.get(0), 
			    		key.get(1), policy.get(key));
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
		}
	}
	
	public Map<List<Point>,String> policyImprovement(double discountFactor, Map<List<Point>, String> policy){
		Map<List<Point>, String> newPolicy = new HashMap<List<Point>, String>();
		for(List<Point> key : valueMap.keySet()){
			String bNewPolicy = findBestAction(discountFactor, key.get(0), key.get(1));
			newPolicy.put(key, bNewPolicy);
		}
		return newPolicy;
	}
	
	public double computeValueByPol(double discountFactor, Point predLoc, 
			Point preyLoc, String policyMove){
		double newValue = 0;
		if(!predLoc.equals(preyLoc)){
			double reward = 0;
			Point move = move(policyMove);
			Point newPredLoc = (Point) predLoc.clone();
			newPredLoc.x = newPredLoc.x + move.x;
			newPredLoc.y = newPredLoc.y + move.y;
			if( !(newPredLoc.x >= 0) || !(newPredLoc.x < 11) ){
				newPredLoc.x = (newPredLoc.x+11) % 11;
			}
			else{
				if( !(newPredLoc.y >= 0) || !(newPredLoc.y < 11) ){
					newPredLoc.y = (newPredLoc.y+11) % 11;
				}
			}
			if(newPredLoc.equals(preyLoc)){
				reward = 10;
			}
			newValue = reward + discountFactor*valueMap.get(Arrays.asList(newPredLoc, preyLoc));
		}
		return newValue;
	}
	
	public Map<List<Point>, String> initPolicy(){
		Map<List<Point>, String> initialPolicy = new HashMap<List<Point>,String>();
		String move = "WEST";
		for( int i = 0; i < 11; i++ ){
			for( int j = 0; j < 11; j++ ){
				Point predXY = new Point(i, j);
				allPredPos.add(predXY);
				for( int k = 0; k < 11; k++ ){
					for( int l = 0; l < 11; l++ ){
						Point preyXY = new Point(k,l);
						List<Point> state = Arrays.asList(predXY, preyXY);
						initialPolicy.put(state, move);
					}
				}
			}
		}
		
		return initialPolicy;
	}
	
	public void valueIteration(double discountFactor){
		System.out.println("Starting value iteration...");
		System.out.println("Initialize values for all states");
		initValueMap();
		double deltaV = theta*2;
		int counter = 0;
		
		while( deltaV > theta ){
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = new HashMap<List<Point>, Double>(valueMap);
			for (List<Point> key : valueMap.keySet()) {
				double oldValue = valueMap.get(key);
			    double newValue = computeValue(discountFactor, key.get(0), key.get(1));
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			valueMap = newValueMap;
			counter = counter + 1;
		}
		// Output the values of all states in which the prey is located at (5,5)
		printBoard(valueMap, new Point(5,5));
		Map<List<Point>,String> bestPolMap = findBestPolicy(discountFactor, valueMap);
		printBoardActions(bestPolMap, new Point(5,5));
		System.out.printf("Value iteration converged in %d iterations\n", counter);
	}
	
	// Value function for policy evaluation algorithm
	public double computeValue(double discountFactor, Point predXY, Point preyXY, double[] policy){
		double value = 0;
		ArrayList<Point> posNextPos = pred.nextPosPositions.get(predXY);
		if(!predXY.equals(preyXY)){
			for(int i = 0; i < posNextPos.size(); i++){
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if(nextPos.equals(preyXY)){
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList(nextPos, preyXY);
				double valueA = policy[i]*(reward + discountFactor*valueMap.get(statePrime));
				value = value + valueA;
			}	
		}
		return value;
	}
	
	// Value function (Bellman equation) for value iteration algorithm
	public double computeValue(double discountFactor, Point predXY, Point preyXY){
		double value = 0;
		ArrayList<Point> posNextPos = pred.nextPosPositions.get(predXY);
		if(!predXY.equals(preyXY)){
			for(int i = 0; i < posNextPos.size(); i++){
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if(nextPos.equals(preyXY)){
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList(nextPos, preyXY);
				double valueA = reward + discountFactor*valueMap.get(statePrime);
				if( valueA > value ){
					value = valueA;
				}
			}	
		}
		return value;
	}
	
	private void initValueMap(){
		for( int i = 0; i < 11; i++ ){
			for( int j = 0; j < 11; j++ ){
				Point predXY = new Point(i, j);
				allPredPos.add(predXY);
				for( int k = 0; k < 11; k++ ){
					for( int l = 0; l < 11; l++ ){
						Point preyXY = new Point(k,l);
						List<Point> state = Arrays.asList(predXY, preyXY);
						valueMap.put(state, 0.0);
					}
				}
			}
		}
	}
	
	public Map<List<Point>, String> findBestPolicy(double discountFactor, Map<List<Point>, Double> finalValueMap){
		for (List<Point> key : valueMap.keySet()) {
			String bestAction = findBestAction(discountFactor, key.get(0), key.get(1));
			bestPolicy.put(key, bestAction);
		}
		return bestPolicy;
	}
	
	// Find best action after value iteration
	public String findBestAction(double discountFactor, Point predXY, Point preyXY){
		double value = 0;
		String bestAction = "";
		ArrayList<Point> posNextPos = pred.nextPosPositions.get(predXY);
		if(!predXY.equals(preyXY)){
			for(int i = 0; i < posNextPos.size(); i++){
				double reward = 0;
				Point nextPos = posNextPos.get(i);
				if(nextPos.equals(preyXY)){
					reward = 10;
				}
				List<Point> statePrime = Arrays.asList(nextPos, preyXY);
				double valueA = reward + discountFactor*valueMap.get(statePrime);
				if( valueA > value ){
					value = valueA;
					bestAction = pred.actions[i];
				}
			}	
		}
		return bestAction;
	}
	
	public void printBoardActions(Map<List<Point>, String> map, Point preyLoc){
		String[][] board = new String[11][11];
		for(int i = 0; i < allPredPos.size(); i++){
			List<Point> state = Arrays.asList(allPredPos.get(i), preyLoc);
			String action = map.get(state);
			board[allPredPos.get(i).y][allPredPos.get(i).x] = action;
		}
		for(String[] row : board){
	        for (String r : row) {
	        	System.out.printf("%s\t", r);
	        }
	        System.out.println();
	    }
		System.out.println();
	}

	public void printBoard(Map<List<Point>, Double> map, Point preyLoc){
		double[][] board = new double[11][11];
		for(int i = 0; i < allPredPos.size(); i++){
			List<Point> state = Arrays.asList(allPredPos.get(i), preyLoc);
			double value = map.get(state);
			board[allPredPos.get(i).y][allPredPos.get(i).x] = value;
		}
		for(double[] row : board){
	        for (double r : row) {
	        	if(r == 0)
	        		System.out.print("0         ");
	        	else
	        		System.out.printf("%.9f", r);
	            System.out.print("\t");
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	
	// Begin Reduced Functions
	public Map<Point, Double> reductionPolicyEvaluation(double discountFactor, double[] policy){
		System.out.println("Starting reduced policy evaluation...");
		System.out.println("Initialize values for all states");
		initStateSpace();
		double deltaV = theta*2;
		int counter = 0;
		
		while( deltaV > theta ){
			deltaV = 0;
			Map<Point, Double> newValueMap = new HashMap<Point, Double>(stateSpace);
			for (Point key : stateSpace.keySet()) {
				double oldValue = stateSpace.get(key);
			    double newValue = reductionComputeValue(discountFactor, key, policy);
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
			counter = counter + 1;
		}
		
		System.out.printf("Reduced policy evaluation converged in %d iterations\n", counter);
		return stateSpace;
	}
	
	public void reductionPolicyIteration(double discountFactor){
		int counter = 0;
		Map<Point, String> policy = reductionInitPolicy();
		initStateSpace();
		boolean policyStable = false;
		while(!policyStable){
			reductionPolicyEvaluationForIt(discountFactor, policy);
			Map<Point, String> newPolicy = reductionPolicyImprovement(discountFactor, policy);
			if(policy.entrySet().equals(newPolicy.entrySet())){
				policyStable = true;
			}
			else{
				policy = newPolicy;
			}
			counter = counter + 1;
		}
		reductionBestPolicy = policy;
		reductionPrintBoardActions(reductionBestPolicy, new Point(5,5));
		reductionPrintBoard(stateSpace, new Point(5,5));
		System.out.printf("Policy iteration with state space reduction converged in %d iterations\n", counter);
	}
	
	public void reductionPolicyEvaluationForIt(double discountFactor, Map<Point, String> policy ){
		double deltaV = 2*theta;
		int counter = 1;
		while( deltaV > theta ){
			counter = counter + 1;
			deltaV = 0;
			Map<Point, Double> newValueMap = new HashMap<Point, Double>(stateSpace);
			for (Point key : stateSpace.keySet()) {
				double oldValue = stateSpace.get(key);
			    double newValue = reductionComputeValueByPol(discountFactor, key, policy.get(key));
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
		}
	}
	
	public Map<Point,String> reductionPolicyImprovement(double discountFactor, Map<Point, String> policy){
		Map<Point, String> newPolicy = new HashMap<Point, String>();
		for(Point key : stateSpace.keySet()){
			String bNewPolicy = reductionFindBestAction(discountFactor, key);
			newPolicy.put(key, bNewPolicy);
		}
		return newPolicy;
	}
	
	public double reductionComputeValueByPol(double discountFactor, Point state, 
			String policyMove){
		double newValue = 0;
		int distance = calcDistance( state );
		if( distance != 0 ){
			double reward = 0;
			Point move = move(policyMove);
			Point newState = (Point) state.clone();
			newState.x = newState.x - move.x;
			newState.y = newState.y - move.y;
			newState = pred.checkDirections(newState);
			int newDistance = calcDistance( newState );
			if( newDistance == 0 ){
				reward = 10;
			}
			newValue = reward + discountFactor*stateSpace.get( newState );
		}
		return newValue;
	}
	
	public Map<Point, String> reductionInitPolicy(){
		Map<Point, String> initialPolicy = new HashMap<Point,String>();
		String move = "WEST";
		for( int i = -5; i < 6; i++ ){
			for( int j = -5; j < 6; j++ ){
				Point state = new Point(i, j);
				initialPolicy.put(state, move);
			}
		}
		
		return initialPolicy;
	}
	
	public void reductionValueIteration( double discountFactor ) {
		System.out.println("Starting reduced value iteration...");
		System.out.println("Initialize values for all states");
		initStateSpace();
		double deltaV = theta*2;
		int counter = 0;
		
		while( deltaV > theta ){
			deltaV = 0;
			Map<Point, Double> newValueMap = new HashMap<Point, Double>(stateSpace);
			for (Point key : stateSpace.keySet()) {
				double oldValue = stateSpace.get(key);
			    double newValue = reductionComputeValue(discountFactor, key );
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			stateSpace = newValueMap;
			reductionPrintBoard(stateSpace, new Point(5,5));
			counter = counter + 1;
		}
		// Output the values of all states in which the prey is located at (5,5)
		reductionPrintBoard(stateSpace, new Point(5,5));
		Map<Point,String> bestPolMap = 	reductionFindBestPolicy( discountFactor, stateSpace );
		reductionPrintBoardActions(bestPolMap, new Point(5,5));
		System.out.printf("Value iteration with state space reduction converged in %d iterations\n", counter);
	}
	
	public double reductionComputeValue(double discountFactor, Point state){
		double value = 0;
		ArrayList<Point> posNextDir = pred.nextPosDirections.get(state);
		int distance = calcDistance( state );
		if( distance != 0 ){
			for(int i = 0; i < posNextDir.size(); i++){
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDistance = calcDistance( nextPos );
				if( nextDistance == 0 ){
					reward = 10;
				}
				double valueA = reward + discountFactor*stateSpace.get(nextPos);
				if( valueA > value ){
					value = valueA;
				}
			}	
		}
		return value;
	}
	
	public double reductionComputeValue(double discountFactor, Point state, double[] policy){
		double value = 0;
		ArrayList<Point> posNextDir = pred.nextPosDirections.get(state);
		int distance = calcDistance( state );
		if( distance != 0 ){
			for(int i = 0; i < posNextDir.size(); i++){
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDistance = calcDistance( nextPos );
				if( nextDistance == 0 ){
					reward = 10;
				}
				double valueA = policy[i]*(reward + discountFactor*stateSpace.get(nextPos));
				value = value + valueA;
			}	
		}
		return value;
	}
		
	public void reductionPrintBoardActions(Map<Point, String> map, Point preyLoc){
		String[][] board = new String[11][11];
		for( Map.Entry<Point, String> entry : map.entrySet() ){
			Point state = entry.getKey();
			String action = map.get(state);
			Point newState = (Point) preyLoc.clone();
			newState.x -= state.x;
			newState.y -= state.y;
			newState = checkLoc( newState );
			board[newState.y][newState.x] = action;
		}
		for(String[] row : board){
	        for (String r : row) {
	        	System.out.printf("%s\t", r);
	        }
	        System.out.println();
	    }
		System.out.println();
	}
	
	public void reductionPrintBoard(Map<Point, Double> map, Point preyLoc){
		double[][] board = new double[11][11];
		for( Map.Entry<Point, Double> entry : map.entrySet() ){
			Point state = entry.getKey();
			double value = entry.getValue();
			Point nextLoc = (Point) preyLoc.clone();
			nextLoc.x -= state.x;
			nextLoc.y -= state.y;
			nextLoc = checkLoc( nextLoc );
			board[nextLoc.y][nextLoc.x] = value;
		}
		for(double[] row : board){
	        for (double r : row) {
	        	if(r == 0)
	        		System.out.print("0         ");
	        	else
	        		System.out.printf("%.9f", r);
	            System.out.print("\t");
	        }
	        System.out.println();
	    }
		System.out.println();
	}	
		
	private void initStateSpace() {
		int[] directionValues = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5};
		for ( int i = 0; i < 11; i++ ) {
			for ( int j = 0; j < 11; j++ ) {
				Point directionVector = new Point( directionValues[i], directionValues[j]);
				stateSpace.put(directionVector, 0.0);
			}
		}
	}
		
	public Map<Point, String> reductionFindBestPolicy(double discountFactor, Map<Point, Double> finalValueMap){
		for (Point key : stateSpace.keySet()) {
			String bestAction = reductionFindBestAction(discountFactor, key);
			reductionBestPolicy.put(key, bestAction);
		}
		return reductionBestPolicy;
	}
	
	public String reductionFindBestAction( double discountFactor, Point state ){
		double value = 0;
		String bestAction = "";
		ArrayList<Point> posNextDir = pred.nextPosDirections.get(state);
		int distance = calcDistance( state );
		if( distance != 0 ){
			for(int i = 0; i < posNextDir.size(); i++){
				double reward = 0;
				Point nextPos = posNextDir.get(i);
				int nextDist = calcDistance( nextPos );
				if( nextDist == 0 ){
					reward = 10;
				}
				double valueA = reward + discountFactor*stateSpace.get(nextPos);
				if( valueA > value ){
					value = valueA;
					bestAction = pred.actions[i];
				}
			}	
		}
		return bestAction;
	}
}