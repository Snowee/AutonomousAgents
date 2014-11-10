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
	public List<Point> allPredPos;
	public Map<List<Point>, String> bestPolicy;
	
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
		valueMap = new HashMap<List<Point>, Double>();
		allPredPos = new ArrayList<Point>();
		bestPolicy = new HashMap<List<Point>, String>();
	}
	
	public int start(boolean randomPolicy) {
		int counter = 0;
		printGameState(pred.pos, prey.pos);
		while( endState == false ) {			
			String predNear = prey.checkPred( pred );
			String preyMove = prey.getMove( predNear );
			Point moveCoordsPrey = move( preyMove );
			prey.newPos( moveCoordsPrey );
			//printGameState(pred.pos, prey.pos);
			
			String predMove;
			if(randomPolicy){
				predMove = pred.getMove();
			}
			else predMove = bestPolicy.get(Arrays.asList(pred.pos, prey.pos));
			
			Point moveCoordsPred = move( predMove );
			pred.newPos( moveCoordsPred );
			counter += 1;
			//printGameState(pred.pos, prey.pos);
			checkStatus();
		}
		System.out.printf("Game ended in %d steps\n", counter);
		return counter;
	}
	
	public void printGameState(Point pred, Point prey){
		String[][] board = new String[11][11];
		board[prey.y][prey.x] = "q";
		board[pred.y][pred.x] = "P";
		for(String[] row : board){
	        for (String r : row) {
	        	if(r == null)
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
		if ( pred.pos.equals( prey.pos ) ) {
			endState = true;
		}
	}
	/*
	public void policyIteration(double discountFactor){
		double[] initialPolicy = pred.policy;
		Map<List<Point>, Double> valueMap = new HashMap<List<Point>, Double>();
		valueMap = policyEvaluation(discountFactor, initialPolicy, valueMap );
		
	}
	
	public Map<List<Point>, String> policyImprovement(double discountFactor, Map<List<Point>, Double> valueMap, Map<List<Point>> ){
		boolean policystable = true;
		Map<List<Point>, String> newPolicy = new HashMap<List<Point>, String>();
		for (List<Point> key : valueMap.keySet()) {
			String b = 
			String bestAction = findBestAction(discountFactor, key.get(0), key.get(1));
			
		}
		Map<List<Point>, String> newPolicy = findBestPolicy(discountFactor, valueMap);
		return newPolicy;
	}
	*/
	
	
	public Map<List<Point>, Double> policyEvaluation(double discountFactor, double[] policy){
		System.out.println("Starting policy evaluation...");
		System.out.println("Initialize values for all states");
		initValueMap();
		double theta = 0.0000001;
		double deltaV = theta*2;
		int counter = 1;
		
		while( deltaV > theta ){
			System.out.printf("Iteration %d\n", counter);
			counter = counter + 1;
			deltaV = 0;
			Map<List<Point>, Double> newValueMap = new HashMap<List<Point>, Double>(valueMap);
			for (List<Point> key : valueMap.keySet()) {
				double oldValue = valueMap.get(key);
			    double newValue = computeValue(discountFactor, key.get(0), key.get(1), policy);
			    newValueMap.put(key, newValue);
			    double diffVal = Math.abs(oldValue - newValue);
			    if(diffVal > deltaV){
			    	deltaV = diffVal;
			    }
			}
			System.out.printf("DeltaV is currently %.4f\n", deltaV);
			valueMap = newValueMap;
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
		
		return valueMap;
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
	
	
	public void valueIteration(double discountFactor){
		System.out.println("Starting value iteration...");
		System.out.println("Initialize values for all states");
		initValueMap();
		double theta = 0.000000000001;
		double deltaV = theta*2;
		int counter = 1;
		
		while( deltaV > theta ){
			System.out.printf("Iteration %d\n", counter);
			counter = counter + 1;
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
			System.out.printf("DeltaV is currently %.4f\n", deltaV);
			valueMap = newValueMap;
		}
		// Output the values of all states in which the prey is located at (5,5)
		printBoard(valueMap, new Point(5,5));
		Map<List<Point>,String> bestPolMap = findBestPolicy(discountFactor, valueMap);
		printBoardActions(bestPolMap, new Point(8,3));
		
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
				if(nextPos.equals(preyXY) && !predXY.equals(preyXY)){
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
				if(nextPos.equals(preyXY) && !predXY.equals(preyXY)){
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
}
