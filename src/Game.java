import java.awt.Point;

public class Game {

	private Predator pred;
	private Prey prey;
	private boolean endState;
	
	public Game( Predator pred, Prey prey ) {
		this.pred = pred;
		this.prey = prey;
		endState = false;
	}
	
	public void start() {
		int counter = 0;
		//pred.to_String();
		//prey.to_String();
		while( endState == false ) {			
			String predNear = prey.checkPred( pred );
			String preyMove = prey.move( predNear );
			Point moveCoordsPrey = getMove( preyMove );
			prey.newPos( moveCoordsPrey );
			
			String predMove = pred.move();
			Point moveCoordsPred = getMove( predMove );
			pred.newPos( moveCoordsPred );
			counter += 1;
			//pred.to_String();
			//prey.to_String();
			checkStatus();
			
		}
		System.out.printf("Game ended in %d steps\n", counter);
	}
	
	public Point getMove( String move ) {
		Point moveCoords = new Point();
		switch( move ) {
		case "NORTH":
			moveCoords.x = 0;
			moveCoords.y = 1;
			break;
		case "EAST":
			moveCoords.x = 1;
			moveCoords.y = 0;
			break;
		case "SOUTH":
			moveCoords.x = 0;
			moveCoords.y = -1;
			break;
		case "WEST":
			moveCoords.x = -1;
			moveCoords.y = 0;
			break;
		case "WAIT":
			moveCoords.x = 0;
			moveCoords.y = 0;
			break;
		default:
			break;
		}
		return moveCoords;
	}
	
	public void checkStatus() {
		if ( pred.pos.equals( prey.pos ) ) {
			endState = true;
		}
	}
	
}
