import java.util.ArrayList;


public class PredatorPrey {

	public static void main(String[] args) {
		
		int nrOfRuns = 10000;
		long totalSteps= 0;
		ArrayList<Integer> steps = new ArrayList<Integer>();
		
		for ( int i = 0; i < nrOfRuns; i++ ) {
			Predator pred = new Predator();
			Prey prey = new Prey();
			Game game = new Game( pred, prey );
			int step = game.start();
			totalSteps += step;
			steps.add(step);
		}
		double averageSteps = (double)totalSteps/nrOfRuns;
		System.out.printf("Average steps over %d iterations is: %f \n",
				nrOfRuns, averageSteps);
		double stddev = std( steps );
		System.out.println("STD:" + stddev );
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
