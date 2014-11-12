import java.util.ArrayList;


public class PredatorPrey {

	public static void main( String[] args ) {
		double discountFactor = 0.8;
		// boolean if the game is run
		boolean playGame = false;
		// Booleans determining which algorithms to run
		boolean policyIteration = false;
		boolean valueIteration = true;
		boolean policyEvaluation = false;
		// Boolean if reduced state space is used
		boolean useReduction = true;
		
		// if policy iteration is true, it overrides all other algorithms
		if( policyIteration ) {
			valueIteration = false;
			policyEvaluation = false;
		}
		if( policyEvaluation ) {
			playGame = false;
		}
		
		// If non reduced state space is used
		if ( !useReduction ) {
			// if the game is not played
			if( !playGame ) {
				Predator pred = new Predator( useReduction );
				Prey prey = new Prey();
				Game game = new Game( pred, prey );
				// Perform the algorithms that are given by the booleans above
				if( policyEvaluation ) {
					game.policyEvaluation( discountFactor, pred.policy );
				}
				else if( policyIteration ) {
					game.policyIteration( discountFactor );
				}
				else if( valueIteration ) {
					game.valueIteration( discountFactor );
				}
			}
			else if( playGame ) {
				// play the game after running the algorithms
				ArrayList<Integer> steps = new ArrayList<Integer>();
				int nrOfRuns = 100;
				int sum = 0;
				
				boolean useRandomPolicy = true;
				// if either iterations algorithms are used
				// never use randompolicy in the game
				if( policyIteration || valueIteration ) {
					useRandomPolicy = false;
				}
				for( int i = 0; i < nrOfRuns; i++ ) {
					// repeat running the game N times for average step size
					Predator pred = new Predator( useReduction );
					Prey prey = new Prey();
					Game game = new Game( pred, prey );
					// Perform policy iteration with highest priority
					// after that value iteration
					if( policyIteration ) {
						game.policyIteration( discountFactor );
					}
					else if( valueIteration ) {
						game.valueIteration( discountFactor );
						
					}
					// run the game, return nr of steps used
					int step = game.start( useRandomPolicy, useReduction );
					sum = sum + step;
					steps.add( step );
				}	
				// compute average steps used
				double averageIt = sum / nrOfRuns;
				System.out.printf( "Average number of iterations to complete game: %f iterations", 
						averageIt );
				// if more than 1 run, compute standard deviation
				if( nrOfRuns > 1 ) {
					double stddev = std( steps );
					System.out.println( "STD:" + stddev );
				}
				
			}
		} else {
			// Else, if the reduced state space is used:
			// Do exactly the same as above, however now with the 
			// functions for the reduced state space
			if( !playGame ) {
				Predator pred = new Predator( useReduction );
				Prey prey = new Prey();
				Game game = new Game( pred, prey );
				if( policyEvaluation ) {
					game.reductionPolicyEvaluation( discountFactor, pred.policy );
				} else if( policyIteration ) {
					game.reductionPolicyIteration( discountFactor );
				}
				else if( valueIteration ) {
					game.reductionValueIteration( discountFactor );
				}
			}
			else if( playGame ) {
				ArrayList<Integer> steps = new ArrayList<Integer>();
				int nrOfRuns = 100;
				int sum = 0;
				
				boolean useRandomPolicy = true;
				if( policyIteration || valueIteration ) {
					useRandomPolicy = false;
				}
				for( int i = 0; i < nrOfRuns; i++ ) {
					Predator pred = new Predator( useReduction );
					Prey prey = new Prey();
					Game game = new Game( pred, prey );
					if( policyIteration ) {
						game.reductionPolicyIteration( discountFactor );
					}
					else if( valueIteration ) {
						game.reductionValueIteration( discountFactor );
						
					}
					int step = game.start( useRandomPolicy, useReduction );
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