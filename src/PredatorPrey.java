import java.util.ArrayList;


public class PredatorPrey {

	public static void main(String[] args) {
		ArrayList<Integer> steps = new ArrayList<Integer>();
		int nrOfRuns = 1;
		int sum = 0;
		
		for ( int i = 0; i < nrOfRuns; i++ ) {
			Predator pred = new Predator();
			Prey prey = new Prey();
			Game game = new Game( pred, prey );
			//game.policyEvaluation(0.8, pred.policy);
			//game.valueIteration(0.5);
			int step = game.start(true);
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