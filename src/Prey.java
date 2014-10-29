import java.awt.Point;
public class Prey {
	
	public Point pos;
		
	public Prey () {
		pos = new Point();
		pos.x = 5;
		pos.y = 5;
	}
	
	public String move ( String predNear ) {
		String move =  "";
		double chance = Math.random();

		if ( predNear.equals("CLEAR") ) {
			// Wait
			if ( chance < 0.80 ) {
				move = "WAIT";			
			} else {
				// East
				if ( chance >= 0.80 && chance < 0.85 ) {
					move = "EAST";
				} else {
					// South
					if ( chance >= 0.85 && chance < 0.90 ) {
						move = "SOUTH";
					} else {
						// West
						if ( chance >= 0.90 && chance < 0.95 ) {
							move = "WEST";
						} else {
							// Stay
							move = "NORTH";
						}
					}
				}
			}
		} else {
			switch ( predNear ) {
			case "NORTH":
				if ( chance < 0.80 ) {
					move = "WAIT";
				} else {
					if ( chance >= 0.80 && chance < 0.80 + 0.20/3 ) {
						move = "EAST";
					} else {
						if ( chance >= 0.80 + 0.20/3 && chance < 0.80 + 0.40/3 ) {
							move = "SOUTH";
						} else {
							if ( chance >= 0.80 + 0.40/3 ) {
								move = "WEST";
							}
						}
					}
				}
				break;
			case "WEST":
				if ( chance < 0.80 ) {
					move = "WAIT";
				} else {
					if ( chance >= 0.80 && chance < 0.80 + 0.20/3 ) {
						move = "EAST";
					} else {
						if ( chance >= 0.80 + 0.20/3 && chance < 0.80 + 0.40/3 ) {
							move = "SOUTH";
						} else {
							if ( chance >= 0.80 + 0.40/3 ) {
								move = "NORTH";
							}
						}
					}
				}
				break;
			case "EAST":
				if ( chance < 0.80 ) {
					move = "WAIT";
				} else {
					if ( chance >= 0.80 && chance < 0.80 + 0.20/3 ) {
						move = "NORTH";
					} else {
						if ( chance >= 0.80 + 0.20/3 && chance < 0.80 + 0.40/3 ) {
							move = "SOUTH";
						} else {
							if ( chance >= 0.80 + 0.40/3 ) {
								move = "WEST";
							}
						}
					}
				}
				break;
			case "SOUTH":
				if ( chance < 0.80 ) {
					move = "WAIT";
				} else {
					if ( chance >= 0.80 && chance < 0.80 + 0.20/3 ) {
						move = "EAST";
					} else {
						if ( chance >= 0.80 + 0.20/3 && chance < 0.80 + 0.40/3 ) {
							move = "NORTH";
						} else {
							if ( chance >= 0.80 + 0.40/3 ) {
								move = "WEST";
							}
						}
					}
				}
				break;
			default:
				break;	
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
	
	public String checkPred( Predator pred ) {
		Point checkPos = new Point();
		
		//North
		checkPos = pos;
		checkPos.y = checkPos.y - 1;
		if ( checkPos.y < 0 ) 
			checkPos.y = 10;
		if ( checkPos.equals(pred) ) {
			return "NORTH";
		} else {
			//East
			checkPos = pos;
			checkPos.x = checkPos.x + 1;
			if ( checkPos.x > 10 ) 
				checkPos.x = 0;
			if ( checkPos.equals(pred) ) {
				return "EAST";
			} else {
				//South
				checkPos = pos;
				checkPos.y = checkPos.y + 1;
				if ( checkPos.y > 10 ) 
					checkPos.y = 0;
				if ( checkPos.equals(pred) ) {
					return "SOUTH";
				} else {
					//West
					checkPos = pos;
					checkPos.x = checkPos.x - 1;
					if ( checkPos.x < 0 ) 
						checkPos.x = 10;
					if ( checkPos.equals(pred) ) {
						return "WEST";
					} else {
						return "CLEAR";
					}
				}
			}
		}
		
	}
	
	public void to_String() {
		System.out.printf( "Prey(%d,%d)\n", pos.x, pos.y );
	}
}

