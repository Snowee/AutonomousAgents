
public class Board {
	
	private char [][] board;
	
	private final char EMPTY = ' ';
	private final char PREDATOR = 'P';
	private final char PREY = 'q';
	
	public Board () {
		board = new char[11][11];
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				board[i][j] = EMPTY;
			}
		}
	}
	
	public Board ( int size ) {
		board = new char[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = EMPTY;
			}
		}		
	}
	
	 public void setPred ( int row, int col ) {
		board[row][col] = PREDATOR;
	}
	
	 public void setPrey ( int row, int col ) {
		board[row][col] = PREY;
	}
	
}
