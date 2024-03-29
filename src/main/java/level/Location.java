package level;

import java.util.Objects;

public class Location {

	public int x, y;
	
	public Location(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Location(Location location)
	{
		this.x = location.x;
		this.y = location.y;
	}

	public void setLocation(Location location)
	{
		this.x = location.x;
		this.y = location.y;
	}

	public Location getCopyLocation()
	{
		return new Location(this.x,this.y);
	}

	public int distance(Location other)
	{
		return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	/**
	 * Computes a new Location based on current direction 
	 * and location.
	 * @param dir - Direction
	 * @param loc - Location
	 * @return The new Location.
	 */
	public static Location newLocation(Direction dir, Location loc)
	{        
		return loc.newLocation(dir);
	}
	
	public Location newLocation(Direction dir) 
	{
	    switch (dir) 
	    {
	    case NORTH	: return new Location(this.x, this.y - 1);
	    case SOUTH	: return new Location(this.x, this.y + 1);
	    case WEST	: return new Location(this.x - 1, this.y);
	    case EAST	: return new Location(this.x + 1, this.y);
	    default		: throw new IllegalArgumentException("Not a valid direction");
	    }        
	    
	}
	
	public Direction inDirection(Location other)
	{
			 if (this.x < other.x && this.y < other.y) return Direction.SOUTH_EAST;
		else if (this.x < other.x && this.y > other.y) return Direction.NORTH_EAST;
		else if (this.x > other.x && this.y < other.y) return Direction.SOUTH_WEST;
		else if (this.x > other.x && this.y > other.y) return Direction.NORTH_WEST;
		else if (this.x < other.x) return Direction.EAST;
		else if (this.x > other.x) return Direction.WEST;
		else if (this.y < other.y) return Direction.SOUTH;
		else if (this.y > other.y) return Direction.NORTH;
		else return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Location location = (Location) o;
		return x == location.x &&
				y == location.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
