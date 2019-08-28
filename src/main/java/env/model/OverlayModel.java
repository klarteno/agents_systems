package env.model;

import level.Location;

import java.util.Collection;
import java.util.Stack;

public class OverlayModel {
	
	private static final int WALL = 1;
	private final MyGridOperations gridOperations ;

	private int overlayObject = 1;
	
	private Stack<Collection<Location>> overlayStack;
	
	public OverlayModel(GridOperations gridOverlay)
	{
		this.gridOperations = new MyGridOperations(gridOverlay);

		// Remove everything except walls
		for (int x = 0; x < gridOperations.width; x++)
		{
			for (int y = 0; y < gridOperations.height; y++)
			{
				if (gridOperations.data[x][y] == GridOperations.WALL) gridOperations.data[x][y] = OverlayModel.this.WALL;
				else							  gridOperations.data[x][y] = 0;
			}
		}		
		overlayStack = new Stack<>();
	}
	
	public OverlayModel(OverlayModel overlay,GridOperations gridOverlay)
	{
		this(gridOverlay);

		gridOperations.deepAddData(overlay.gridOperations);

		for (Collection<Location> path : overlay.overlayStack)
		{
			addOverlay(path);
		}
	}
	
	public void addOverlay(Collection<Location> path)
	{		
		overlayObject <<= 1;
		
		path.stream().forEach(l -> gridOperations.add(overlayObject, l));
		
		overlayStack.push(path);
	}
	
	public void removeOverlay()
	{
		if (!overlayStack.isEmpty())
		{			
			overlayStack.pop().stream().forEach(l -> gridOperations.remove(overlayObject, l));
			
			overlayObject >>= 1;
		}
	}

	public GridOperations getGridOperations() {
		return gridOperations;
	}

	public String toString() {
		return gridOperations.toString();
	}

	public int hashCode() {
		return gridOperations.hashCode();
	}

	public boolean equals(Object obj) {
		return gridOperations.equals(obj);
	}

	private class MyGridOperations extends GridOperations {
		public MyGridOperations(GridOperations model) {
			super(model);
		}

		@Override
		public String toString()
		{
			StringBuilder str = new StringBuilder();

			// Print integer representation of level
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
						 if (data[x][y] == 0)	str.append(' ');
					else if (data[x][y] == 1) 	str.append('+');
					else						str.append('#');
				}
				str.append("\n");
			}
			return str.toString();
		}
	}
}
