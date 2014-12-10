package Assignment2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PredatorPrey {

	public static void main( String[] args ) throws IOException {
		/*double discountFactor = 0.8;
		// boolean if the game is run
		boolean playGame = true;
		// Booleans determining which algorithms to run
		boolean qlearning = true;

		if( !playGame ) {
			Predator pred = new Predator();
			Prey prey = new Prey();
			Game game = new Game( pred, prey );
			
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
		}	*/
		Predator pred = new Predator(false);
		Prey prey = new Prey(false);
		double discountFactor = 0.5;
		double learningRate = 0.1;
		double qIntValues = 15.0;
		int nEpisodes = 5000;
		boolean greedy = true;
		//{0.1, 0.5, 0.7, 0.9};
		double[] learningR = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		double[] discountF = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		double[] qVals = {0.0, 5.0, 10.0, 15.0, 20.0};
		double[] epsilon = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		double[] temperature = {0.5, 5, 50, 500};
		
		List<double[]> plotData = new ArrayList<double[]>();
		int sampleSize = 100;
		
		Plot_results plot = new Plot_results("Comparison on- and off-policy Monte-Carlo ", "Number of episodes", "Average number of steps");
		/*
		for(int i = 0; i < discountF.length; i++){
			
			Game game = new Game( pred, prey );
			//plotData = game.qlearning(learningR[i], 0.8, nEpisodes, greedy, 15, 0.1, 10);
			//plotData = game.Sarsa(0.8, 0.1, nEpisodes, greedy, 15, 0.1, temperature[i]);
			//plotData = game.onPolicyMC(0.1, discountF[i], nEpisodes, -1);
			//System.out.println("ervoor");
			plotData = game.offPolicyMonteCarlo(0.8, nEpisodes, 0.0);	
			//System.out.println("klaar");
			String name = "Discount factor: ";
			/*
			int c = 0;
			double[] yData = new double[plotData.get(0).length];
			double[] xData = new double[plotData.get(0).length];
			for(int k = 1; k < plotData.get(0).length; k++){
				yData[c] = plotData.get(0)[k];
				xData[c] = k;
				c++;
			}
			//plot.add_data_series(xData, yData, name.concat(String.valueOf(learningR[i])) );
			plot.averaged_data(plotData.get(1), plotData.get(0), name.concat(String.valueOf(discountF[i])), sampleSize, nEpisodes);
		}
		*/
		
		Game game = new Game( pred, prey );
		//plotData = game.qlearning(0.1, 0.8, nEpisodes, greedy, -1.0, 0.1, 10);
		plotData = game.onPolicyMC(0.1, 0.2, nEpisodes, -1.0);
		//plotData = game.onPolicyMC(0.1, 0.8, nEpisodes, -1.0);
		
		plot.averaged_data(plotData.get(1), plotData.get(0), "On-policy Monte-Carlo", sampleSize, nEpisodes);
		
		Game game2 = new Game( pred, prey );
		//plotData = game.qlearning(0.1, 0.8, nEpisodes, greedy, -1.0, 0.1, 10);
		plotData = game2.offPolicyMonteCarlo(0.8, nEpisodes, 0.0);
		//plotData = game.onPolicyMC(0.1, 0.8, nEpisodes, -1.0);
		
		plot.averaged_data(plotData.get(1), plotData.get(0), "Off-policy Monte-Carlo", sampleSize, nEpisodes);
		/*
		Game game3 = new Game( pred, prey );
		//plotData = game.qlearning(0.1, 0.8, nEpisodes, greedy, -1.0, 0.1, 10);
		plotData = game3.qlearning(0.1, 0.8, nEpisodes, true, 5, 0.1, 10);
		//plotData = game.onPolicyMC(0.1, 0.8, nEpisodes, -1.0);
		
		plot.averaged_data(plotData.get(1), plotData.get(0), "Q-learning with epsilon-greedy action selection", sampleSize, nEpisodes);
		
		Game game4 = new Game( pred, prey );
		//plotData = game.qlearning(0.1, 0.8, nEpisodes, greedy, -1.0, 0.1, 10);
		plotData = game4.qlearning(0.1, 0.8, nEpisodes, false, 5, 0.1, 0.5);
		//plotData = game.onPolicyMC(0.1, 0.8, nEpisodes, -1.0);
		
		plot.averaged_data(plotData.get(1), plotData.get(0), "Q-learning with softmax action selection", sampleSize, nEpisodes);
		
		
		//plot.export_image("test.png", 500, 500, sampleSize, nEpisodes);
		
		//game.qlearning(discountFactor, learningRate, qIntValues, nEpisodes);*/
		plot.export_image("test.png", 500, 500, sampleSize, nEpisodes);
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