package Assignment3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PredatorPrey {

	public static void main( String[] args ) throws IOException {
		//Algorithm to run
		//String algorithm = "Qlearning";
		//String algorithm = "Minimax";
		String algorithm = "Sarsa";
		
		double[] empty = {};

		//Minimax parameters
		boolean loopExplore = false;
		double[] explore = {0.2, 0.4, 0.6, 0.8};
		boolean loopInitV = false;
		double[] initV = {1.0, 5.0, 10.0}; //Nodig?

		//Independent Q-learning parameters
		boolean loopTemperature = false;
		double[] temperature = {0.5, 5, 50, 500};
		boolean greedy = false;
		boolean loopEpsilon = false;
		double[] epsilon = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};

		//Minimax and independent Q-learning parameters
		boolean loopInitQvals = false;
		double[] initQvals = {0.0, 5.0, 10.0, 15.0, 20.0};
		boolean loopDiscount = false;
		double[] discount = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		boolean loopAlpha = false;
		double[] alpha = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};

		if( loopExplore ){
			printData( algorithm, "Explore", explore, greedy );
		}
		else if( loopEpsilon ){
			printData( algorithm, "Epsilon", epsilon, greedy );
		}
		else if( loopInitV ){
			printData( algorithm, "Initial V values", initV, greedy );
		}
		else if( loopTemperature ){
			printData( algorithm, "Temperature", temperature, greedy );			
		}
		else if( loopInitQvals ){
			printData( algorithm, "Init Q values", initQvals, greedy );
		}
		else if( loopDiscount ){
			printData( algorithm, "Discount factor", discount, greedy );
		}
		else if( loopAlpha ){
			printData( algorithm, "Alpha", alpha, greedy );
		}
		else{
			System.out.println("Running with standard variables");
			printData( algorithm, "Standard", empty, greedy );
		}
	}

	public static void printData( String algorithm, String variableName, double[] varValues, boolean greedy) throws IOException{
		List<double[]> plotData = new ArrayList<double[]>();
		//Standard parameter values
		double alpha = 0.1;
		double initialQvalues = 1.0;
		double initialV = 1.0;
		double explore = 0.2;
		double discountFactor = 0.8; 
		double epsilon = 0.1;
		double temperature = 0.5;
		int nEpisodes = 1000;
		int nPreds = 2;
		boolean randomInitState = false;
		
		String plotTitle = "Plot title here";
		String xAxisLabel = "Number of episodes";
		String yAxisLabel = "Cumulated reward";
		String labelLine1 = "Prey";
		String labelLine2 = "Predator";
		String fileName = "test.png";
		int imageWidth = 500;
		int imageHeight = 500;
		int yAxisRange = nEpisodes;
		int yAxisTicks = nEpisodes;		

		Plot_results plot = new Plot_results( plotTitle, xAxisLabel, yAxisLabel );

		//Checken welke data uit welke index komt van plotData
		switch( variableName ){
		case "Explore":
			for( int i = 0; i < varValues.length; i++ ){
				Game game = new Game( nPreds );
				//plotData = game.miniMaxQlearning(initialQvalues, initialV, alpha, varValues[i], gamma);
				//plot.add_data_series(xData, yData, label);
				plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);//prey reward
				plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);//predator reward
				plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
			}
		case "Initial V values":
			for( int i = 0; i < varValues.length; i++ ){
				Game game = new Game( nPreds );
				//plotData = game.miniMaxQlearning(initialQvalues, varValues[i], alpha, explore, gamma);
				plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
				plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
				plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
			}
		case "Temperature":
			if( algorithm.equals("Sarsa")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.Sarsa( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, varValues[i], randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else if( algorithm.equals("Qlearning")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.qlearning( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, varValues[i], randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
				//	plot.add_data_series(plotData.get(0), plotData.get(3), labelLine1);
				//	plot.add_data_series(plotData.get(0), plotData.get(4), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
		case "Epsilon":
			if( algorithm.equals("Sarsa")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.Sarsa( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, varValues[i], temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else if( algorithm.equals("Qlearning")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.qlearning( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, varValues[i], temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
		case "Alpha":
			if( algorithm.equals("Sarsa")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.Sarsa( varValues[i], discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else if( algorithm.equals("Qlearning")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.qlearning( varValues[i], discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else{
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					//plotData = game.miniMaxQlearning( initialQvalues, initialV, 
					//varValues[i], explore, discountFactor);
					plot.add_data_series(plotData.get(0), plotData.get(1), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(2), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
		case "Discount factor":
			if( algorithm.equals("Sarsa")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.Sarsa(alpha, varValues[i], nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else if( algorithm.equals("Qlearning")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.qlearning( alpha, varValues[i], nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else{
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					//plotData = game.miniMaxQlearning( initialQvalues, initialV, 
					//alpha, explore, varValues[i]);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
		case "Initial Q values":
			if( algorithm.equals("Sarsa")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.Sarsa( alpha, discountFactor, nEpisodes, greedy,
							varValues[i], epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else if( algorithm.equals("Qlearning")){
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					plotData = game.qlearning( alpha, discountFactor, nEpisodes, greedy,
							varValues[i], epsilon, temperature, randomInitState);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
			else{
				for( int i = 0; i < varValues.length; i++ ){
					Game game = new Game( nPreds );
					//plotData = game.miniMaxQlearning( varValues[i], initialV, 
					//alpha, explore, discount);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
				}
			}
		case "Standard":
			if( algorithm.equals("Sarsa")){
					Game game = new Game( nPreds );
					plotData = game.Sarsa( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					//plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					//plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.add_data_series(plotData.get(0), plotData.get(3), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(4), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
			}
			else if( algorithm.equals("Qlearning")){
					Game game = new Game( nPreds );
					plotData = game.qlearning( alpha, discountFactor, nEpisodes, greedy,
							initialQvalues, epsilon, temperature, randomInitState);
					//plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					//plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.add_data_series(plotData.get(0), plotData.get(3), labelLine1);
					plot.add_data_series(plotData.get(0), plotData.get(4), labelLine2);
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
			}
			else{
					Game game = new Game( nPreds );
					//plotData = game.miniMaxQlearning( varValues[i], initialV, 
					//alpha, explore, discount);
					plot.add_data_series(plotData.get(0), plotData.get(1), "Predator");
					plot.add_data_series(plotData.get(0), plotData.get(2), "Prey");
					plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
			}
			
		}
	}
}