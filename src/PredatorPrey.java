
public class PredatorPrey {

	public static void main(String[] args) {
		
		int nrOfRuns = 100;
		long totalTime = 0;
		
		for ( int i = 0; i < nrOfRuns; i++ ) {
			long startTime = System.currentTimeMillis();
			Predator pred = new Predator();
			Prey prey = new Prey();
			Game game = new Game( pred, prey );
			game.start();
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			totalTime += time;
			System.out.printf("Time: %d\n",time);
		}
		double averageTimeMillis = (double)totalTime/nrOfRuns;
		System.out.printf("Average time over %d iterations is: %f milliseconds\n",
				nrOfRuns, averageTimeMillis);
		
	}
	
	
	
	
}
