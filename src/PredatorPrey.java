import java.util.ArrayList;


public class PredatorPrey {

	public static void main(String[] args) {
		double discountFactor = 0.8;
		boolean playGame = false;
		boolean policyIteration = true;
		boolean valueIteration = false;
		boolean policyEvaluation = false;
		boolean useReduction = true;
		
		if(policyIteration){
			valueIteration = false;
			policyEvaluation = false;
		}
		if(policyEvaluation){
			playGame = false;
		}
		
		if ( !useReduction ) {			
			if(!playGame){
				Predator pred = new Predator( useReduction );
				Prey prey = new Prey();
				Game game = new Game(pred, prey);
				if(policyEvaluation){
					game.policyEvaluation(discountFactor, pred.policy);
				}
				else if(policyIteration){
					game.policyIteration(discountFactor);
				}
				else if(valueIteration){
					game.valueIteration(discountFactor);
				}
			}
			else if(playGame){
				ArrayList<Integer> steps = new ArrayList<Integer>();
				int nrOfRuns = 1;
				int sum = 0;
				
				boolean useRandomPolicy = true;
				if(policyIteration || valueIteration){
					useRandomPolicy = false;
				}
				for ( int i = 0; i < nrOfRuns; i++ ) {
					Predator pred = new Predator( useReduction );
					Prey prey = new Prey();
					Game game = new Game( pred, prey );
					if(policyIteration){
						game.policyIteration(discountFactor);
					}
					else if(valueIteration){
						game.valueIteration(discountFactor);
						
					}
					int step = game.start(useRandomPolicy);
					sum = sum + step;
					steps.add(step);
				}	
				double averageIt = sum/nrOfRuns;
				System.out.printf("Average number of iterations to complete game: %f iterations", averageIt);
				if ( nrOfRuns > 1 ) {
					double stddev = std( steps );
					System.out.println("STD:" + stddev );
				}
				
			}
		} else {
			if(!playGame){
				Predator pred = new Predator( useReduction );
				Prey prey = new Prey();
				Game game = new Game(pred, prey);
				if(policyEvaluation){
					game.policyEvaluation(discountFactor, pred.policy);
				}
				else if(policyIteration){
					game.reductionPolicyIteration(discountFactor);
				}
				else if(valueIteration){
					game.reductionValueIteration(discountFactor);
				}
			}
			else if(playGame){
				ArrayList<Integer> steps = new ArrayList<Integer>();
				int nrOfRuns = 1;
				int sum = 0;
				
				boolean useRandomPolicy = true;
				if(policyIteration || valueIteration){
					useRandomPolicy = false;
				}
				for ( int i = 0; i < nrOfRuns; i++ ) {
					Predator pred = new Predator( useReduction );
					Prey prey = new Prey();
					Game game = new Game( pred, prey );
					if(policyIteration){
						game.reductionPolicyIteration(discountFactor);
					}
					else if(valueIteration){
						game.reductionValueIteration(discountFactor);
						
					}
					int step = game.start(useRandomPolicy);
					sum = sum + step;
					steps.add(step);
				}	
				double averageIt = sum/nrOfRuns;
				System.out.printf("Average number of iterations to complete game: %f iterations", averageIt);
				if ( nrOfRuns > 1 ) {
					double stddev = std( steps );
					System.out.println("STD:" + stddev );
				}
				
			}
			
		}
	}

	public static double std( ArrayList<Integer> array ) {
		double average = 0.0;
		
		for ( int i = 0; i < array.size(); i++ ) {
			average += array.get(i);
		}
		average = average / array.size();
		
		double std;
		int sum = 0;
		for ( int i = 0; i < array.size(); i++ ) {
			sum += Math.pow( (array.get(i) - average), 2 );
		}
		std = Math.sqrt( sum / (array.size() - 1) );
		return std;
	}
}