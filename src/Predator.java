import java.awt.Point;


public class Predator {

	public Point pos;
	
	public Predator () {
		pos = new Point();
		pos.x = 0;
		pos.y = 0;
	}
	
	public String move () {
		double chance = Math.random();
		String move;
		// North
		if ( chance < 0.20 ) {
			move = "NORTH";			
		} else {
			// East
			if ( chance >= 0.20 && chance < 0.40 ) {
				move = "EAST";
			} else {
				// South
				if ( chance >= 0.40 && chance < 0.60 ) {
					move = "SOUTH";
				} else {
					// West
					if ( chance >= 0.60 && chance < 0.80 ) {
						move = "WEST";
					} else {
						// Wait
						move = "WAIT";
					}
				}
			}
		}
		return move;
	}
	
	public void newPos( Point move ) {
		pos.x = pos.x + move.x;
		pos.y = pos.y + move.y;
		if ( pos.x > 10 ) 
			pos.x = 0;
		if ( pos.y > 10 )
			pos.y = 0;
		if ( pos.x < 0 )
			pos.x = 10;
		if ( pos.y < 0 )
			pos.y = 10;
	}
	
	public void to_String() {
		System.out.printf("Predator(%d,%d)\n", pos.x, pos.y);
	}
}
