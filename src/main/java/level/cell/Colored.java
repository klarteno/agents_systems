package level.cell;

import level.Color;
import level.Location;

public abstract class Colored extends Lettered {

	private Color color;
	
	public Colored(Location location, char letter, Color color) 
	{
		super(location, letter);
		this.color = color;
	}
	
	public Colored(int x, int y, char letter, Color color)
	{
		this(new Location(x, y), letter, color);
	}

	public Color getColor()
	{
		return color;
	}
	
	@Override
	public String toString() {
		return color + " " + super.toString();
	}
}
