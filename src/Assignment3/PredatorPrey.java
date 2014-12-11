package Assignment3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;


public class PredatorPrey {

	public static void main( String[] args ) throws IOException {
/*
		Predator pred = new Predator(false, 0);
		double[] var = new double[6];
		try {
			LpSolve solver = LpSolve.makeLp(0, 6); //5 variables? 
			//Constraint 1
			double[] row1 = {0, 0, 0, 0, 0, -1}; 
			double[] row2 = {10, 2, 1, 4, 1, -1};
			double[] row3 = {1, 1, 1, 1, 1, -1}; 
			double[] row4 = {10, 10, 10, 10, 10, -1};
			double[] row5 = {10, 20, 1, 1, 3, -1};
			
				solver.strAddConstraint("0 0 0 0 0 -1 ", LpSolve.GE, 0.0);
				solver.strAddConstraint("10 2 0 -4 0 -1 ", LpSolve.GE, 0.0);
				solver.strAddConstraint("-10 -10 1 1 1 -1 ", LpSolve.GE, 0.0);
				solver.strAddConstraint("0 0 0 0 0 -1 ", LpSolve.GE, 0.0);
				solver.strAddConstraint("10 20 -1 0 3 -1 ", LpSolve.GE, 0.0);

			solver.addConstraint(row1, LpSolve.GE, 0.0);
			solver.addConstraint(row2, LpSolve.GE, 0.0);
			solver.addConstraint(row3, LpSolve.GE, 0.0);
			solver.addConstraint(row4, LpSolve.GE, 0.0);
			solver.addConstraint(row5, LpSolve.GE, 0.0);

			//Constraint 2
			//double[] constraint2 = {1, 1, 1, 1, 1, 0};
			//solver.addConstraint(constraint2, LpSolve.EQ, 1.0);
			solver.strAddConstraint("1 1 1 1 1 0", LpSolve.EQ, 1.0);

			//Constraint 3
			
			double[] constraint3_1 = {1, 0, 0, 0, 0, 0}; 
			double[] constraint3_2 = {0, 1, 0, 0, 0, 0}; 
			double[] constraint3_3 = {0, 0, 1, 0, 0, 0}; 
			double[] constraint3_4 = {0, 0, 0, 1, 0, 0}; 
			double[] constraint3_5 = {0, 0, 0, 0, 1, 0}; 
			
			solver.addConstraint(constraint3_1, LpSolve.GE, 0.0);
			solver.addConstraint(constraint3_2, LpSolve.GE, 0.0);
			solver.addConstraint(constraint3_3, LpSolve.GE, 0.0);
			solver.addConstraint(constraint3_4, LpSolve.GE, 0.0);
			solver.addConstraint(constraint3_5, LpSolve.GE, 0.0);
			
			solver.strAddConstraint("1 0 0 0 0 0", LpSolve.GE, 0.0);
			solver.strAddConstraint("0 1 0 0 0 0", LpSolve.GE, 0.0);
			solver.strAddConstraint("0 0 1 0 0 0", LpSolve.GE, 0.0);
			solver.strAddConstraint("0 0 0 1 0 0", LpSolve.GE, 0.0);
			solver.strAddConstraint("0 0 0 0 1 0", LpSolve.GE, 0.0);

			//Objective
			//double[] objective = {0, 0, 0, 0, 0, 1};
			//solver.setObjFn(objective);
			solver.strSetObjFn("0 0 0 0 0 1");
			solver.setUnbounded(pred.actions.length);
			solver.setMaxim(); //Maximize objective
			solver.solve();

			// print solution
			System.out.println("Value of objective function: " + solver.getObjective());
			var = solver.getPtrVariables();
			for (int i = 0; i < var.length; i++) {
				System.out.println("Value of var[" + i + "] = " + var[i]);
			}
		} catch (LpSolveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//Algorithm to run
		//String algorithm = "Qlearning";
		String algorithm = "Minimax";
		//String algorithm = "Sarsa";

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

		double explore = 0.2;
		double discountFactorPred = 0.8;
		double discountFactorPrey = 0.8;
		double epsilon = 0.1;
		double temperature = 0.5;
		int nEpisodes = 200;
		double decayPred = Math.pow(10, Math.log(0.1)/nEpisodes);
		double decayPrey = Math.pow(10, Math.log(0.1)/nEpisodes);
		int nPreds = 2;
		boolean randomInitState = false;

		String plotTitle = "Plot title here";
		String xAxisLabel = "Number of episodes";
		String yAxisLabel = "Average umber of steps";
		String labelLine1 = "Prey";
		String labelLine2 = "Predator";
		String fileName = "test.png";
		int imageWidth = 500;
		int imageHeight = 500;
		int yAxisRange = 300;
		int yAxisTicks = yAxisRange/10;	
		int sampleSize = nEpisodes/40;

		Plot_results plot = new Plot_results( plotTitle, xAxisLabel, yAxisLabel );

		Game game = new Game( nPreds );
		plotData = game.miniMaxQlearning( explore, discountFactorPred,
				discountFactorPrey, decayPred, decayPrey, nEpisodes);
		plot.averaged_data(plotData.get(0), plotData.get(1), "", sampleSize, nEpisodes );
		//plot.add_data_series(plotData.get(0), plotData.get(1), "");
		plot.export_image(fileName, imageWidth, imageHeight, nEpisodes,  yAxisRange, yAxisTicks);
		
	}
}