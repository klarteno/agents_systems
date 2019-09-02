package env.model;

import level.Location;

import java.util.Collection;
import java.util.Stack;

public class MaskGridCollection {
	
	private static final int WALL = 1;
	private final  GridOperations gridOperations ;

	private int overlayObject = 1;
	private Stack<Collection<Location>> overlayStack;
	
	public MaskGridCollection(GridOperations gridOp)
	{
		this.gridOperations = new GridOperations(gridOp);//gridOperations.deepAddData(gridOp);
		// Remove everything except walls
		for (int x = 0; x < gridOperations.getWidth(); x++)
		{
			for (int y = 0; y < gridOperations.getHeight(); y++)
			{
				if (gridOperations.containsInGrid(x, y, GridOperations.WALL))
							gridOperations.addToGrid(x, y, MaskGridCollection.WALL);
				else							  gridOperations.addToGrid(x, y, 0);
			}
		}		
		overlayStack = new Stack<>();
	}

	public void addOverlay(Collection<Location> path)
	{		
		overlayObject <<= 1;
		//path.stream().forEach(loc -> gridOperations.add(overlayObject, loc));
		path.forEach(loc -> this.gridOperations.add(overlayObject, loc));
		overlayStack.push(path);
	}
	
	public void removeOverlay()
	{
		if (!overlayStack.isEmpty())
		{			
			//overlayStack.pop().stream().forEach(loc -> gridOperations.remove(overlayObject, loc));
			overlayStack.pop().forEach(loc -> gridOperations.remove(overlayObject, loc));
			overlayObject >>= 1;
		}
	}

	public GridOperations getGridOperations() {
		return gridOperations;
	}

/*
	public int hashCode() {
		return gridOperations.hashCode();
	}

	public boolean equals(Object obj) {
		return gridOperations.equals(obj);
	}
*/

	@Override
	public String toString()
		{
			StringBuilder str = new StringBuilder();

			// Print integer representation of level
			for (int y = 0; y < this.gridOperations.getHeight(); y++)
			{
				for (int x = 0; x < this.gridOperations.getWidth(); x++)
				{
						 if (this.gridOperations.containsInGrid(x, y, 0))	str.append(' ');
					else if (this.gridOperations.containsInGrid(x, y, 1)) 	str.append('+');
					else											str.append('#');
				}
				str.append("\n");
			}
			return str.toString();
		}
	}

