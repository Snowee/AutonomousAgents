package Assignment3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class test {

	public static void main(String[] args) {
		/*
		double alpha = 0.1;
		double discountFactor = 0.8;
		int nEpisodes = 1;
		boolean greedy = true;
		int initQval = 5;
		double epsilon = 0.1;
		double temperature = 0.5;
		Game game = new Game(2);
		game.qlearning(alpha, discountFactor, nEpisodes, greedy, initQval, epsilon, temperature);
		*/
		Game game = new Game(2);
		game.start(true, 2);
	}

}
