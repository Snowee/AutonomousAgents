package Assignment3;
import java.util.ArrayList;


public class PredatorPrey {
/*
	public static void main( String[] args ) {
		double discountFactor = 0.9;
		double learningRate = 0.1;
		double epsilon = 0.1;
		double temperature = 0.5;
		double initQvalue = 15.0;
		int nEpisodes = 10000;
		// boolean if the game is run
		boolean playGame = false;
		// Booleans determining which algorithms to run
		// Turn only one on with true
		boolean qlearning = false;
		boolean sarsa = false;
		boolean offPolMC = true;
		boolean onPolMC = false;
		boolean useGreedy = false;
		


		if( !playGame ) {
			Predator pred = new Predator(false);
			Prey prey = new Prey(false);
			Game game = new Game( pred, prey );
			// run the algorithm that is turned on with the booleans above
			if( sarsa ) {
				game.Sarsa( discountFactor, learningRate, nEpisodes, useGreedy, epsilon, temperature );
			} else {
				if( qlearning ) {
					game.qlearning( learningRate, discountFactor, nEpisodes, useGreedy, initQvalue, epsilon, temperature );
				} else {
					if( offPolMC ) {
						game.offPolicyMonteCarlo( initQvalue, discountFactor, nEpisodes );
					} else {
						if( onPolMC ) {
							game.onPolicyMC(epsilon, discountFactor, nEpisodes);
						}
					}
				}
			}
				
		}
		else if( playGame ) {
			ArrayList<Integer> steps = new ArrayList<Integer>();
			int nrOfRuns = 100;
			int sum = 0;
			
			boolean useRandomPolicy = true;
			
			for( int i = 0; i < nrOfRuns; i++ ) {
				Predator pred = new Predator(false);
				Prey prey = new Prey(false);
				Game game = new Game( pred, prey );
				int step = game.start( useRandomPolicy );
				sum = sum + step;
				steps.add( step );
			}	
			double averageIt = sum / nrOfRuns;
			System.out.printf( "Average number of steps to complete game: %f steps", 
					averageIt );
			if( nrOfRuns > 1 ) {
				double stddev = std( steps );
				System.out.println( "STD:" + stddev );
			}	
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
	*/
}