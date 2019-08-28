package level.cell;

import level.Location;

public class Cell {
	
	private Location location;
	
	public Cell(Location location) 
	{
		this.location = location;
	}

	public Cell(Cell cell)
	{
		this.location = new Location(cell.location.x, cell.location.y);
	}

	public Location getLocation() 
	{
		return location;
	}

	public void setLocation(Location location) 
	{
		this.location = location;
	}

	@Override
	public String toString() {
		return location.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (location == null) {
			return other.location == null;
		} else return location.equals(other.location);
	}
}
