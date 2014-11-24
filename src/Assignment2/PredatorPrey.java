package Assignment2;
import java.util.ArrayList;


public class PredatorPrey {

	public static void main( String[] args ) {
		double discountFactor = 0.8;
		double learningRate = 0.1;
		int nEpisodes = 100;
		// boolean if the game is run
		boolean playGame = false;
		// Booleans determining which algorithms to run
		boolean qlearning = false;
		boolean sarsa = true;
		boolean useGreedy = false;

		if( !playGame ) {
			Predator pred = new Predator();
			Prey prey = new Prey();
			Game game = new Game( pred, prey );
			game.Sarsa( discountFactor, learningRate, nEpisodes, useGreedy );
			
		}
		else if( playGame ) {
			ArrayList<Integer> steps = new ArrayList<Integer>();
			int nrOfRuns = 100;
			int sum = 0;
			
			boolean useRandomPolicy = true;
			
			for( int i = 0; i < nrOfRuns; i++ ) {
				Predator pred = new Predator();
				Prey prey = new Prey();
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
}