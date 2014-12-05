package Assignment3;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class PredatorPrey {

	public static void main( String[] args ) {
		double discountFactor = 0.9;
		double learningRate = 0.1;
		double epsilon = 0.1;
		double temperature = 0.5;
		double initQvalue = 15.0;
		int nEpisodes = 10000;
		// boolean if the game is run
		boolean playGame = true;
		// Booleans determining which algorithms to run
		// Turn only one on with true
		boolean qlearning = false;
		boolean sarsa = false;
		boolean offPolMC = true;
		boolean onPolMC = false;
		boolean useGreedy = false;
		
		Game game1 = new Game( );
		double[][] qs1 = game1.initQvaluesMA(15.0, 4);
		double[][] qs2 = game1.initQvaluesMA(15.0, 4);
		double[][] qs3 = game1.initQvaluesMA(15.0, 4);
		double[][] qs4 = game1.initQvaluesMA(15.0, 4);

		
//		for( int i = 0; i < 1771561; i++ ) {
//			if(Collections.frequency(game1.statesArray, game1.statesArray.get(i)) > 1 ) {
//				System.out.println(Collections.frequency(game1.statesArray, game1.statesArray.get(i)));
//			}
//		}

//		if( !playGame ) {
//			Predator pred = new Predator(false, 1);
//			Prey prey = new Prey(false);
//			Game game = new Game( pred, prey );
//			// run the algorithm that is turned on with the booleans above
//			if( sarsa ) {
//				game.Sarsa( discountFactor, learningRate, nEpisodes, useGreedy, epsilon, temperature );
//			} else {
//				if( qlearning ) {
//					game.qlearning( learningRate, discountFactor, nEpisodes, useGreedy, initQvalue, epsilon, temperature );
//				} else {
//					if( offPolMC ) {
//						game.offPolicyMonteCarlo( initQvalue, discountFactor, nEpisodes );
//					} else {
//						if( onPolMC ) {
//							game.onPolicyMC(epsilon, discountFactor, nEpisodes);
//						}
//					}
//				}
//			}
//				
//		}
//		else 
		if( playGame ) {
			ArrayList<Integer> steps = new ArrayList<Integer>();
			int nrOfRuns = 10000;
			int sum = 0;
			
			boolean useRandomPolicy = true;
			int predWins = 0;
			int preyWins = 0;

			for( int i = 0; i < nrOfRuns; i++ ) {
				Game game = new Game( );
				int step = game.start( useRandomPolicy, 4 );
				sum = sum + step;
				steps.add( step );
				predWins += game.predWins;
				preyWins += game.preyWins;
			}	
			double averageIt = sum / nrOfRuns;
			System.out.printf( "Average number of steps to complete game: %f steps\n", 
					averageIt );
			if( nrOfRuns > 1 ) {
				double stddev = std( steps );
				System.out.println( "STD:" + stddev );
			}
			
			double percentagePred = (double) predWins / ( predWins + preyWins );
			double percentagePrey = (double) preyWins / ( predWins + preyWins );
			System.out.println( "Percentage predator wins: " + percentagePred );
			System.out.println( "Percentage prey wins: " + percentagePrey );
		}	
	}	

	
	// Function to compute the standard deviation of an array of integers
	public static double std( ArrayList<Integer> array ) {
		double average = 0.0;
		
		for( int i = 0; i < array.size(); i++ ) {
			average += array.get(i);
		}
		average = average / array.size();
		
		double std;
		int sum = 0;
		for( int i = 0; i < array.size(); i++ ) {
			sum += Math.pow( (array.get(i) - average), 2 );
		}
		std = Math.sqrt( sum / (array.size() - 1) );
		return std;
	}
}