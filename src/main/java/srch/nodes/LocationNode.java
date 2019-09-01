package srch.nodes;

import env.model.GridOperations;
import level.Direction;
import level.Location;
import srch.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LocationNode extends Node {
	
	private Direction direction;

	public LocationNode(Location initial, int initialStep) 
	{
		super(initial, initialStep);
		
		this.direction = null;
	}

	private LocationNode(Node parent, Direction direction, Location location)
	{
		super(parent, location);
		
		this.direction = direction;
	}

	public Direction getDirection() 
	{
		return direction;
	}

	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperations)
	{
		List<Node> expandedNodes = new ArrayList<Node>(Direction.EVERY.length);
		
		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);
			if (gridOperations.isFree(this.getObject(), loc))
			{
				expandedNodes.add(new LocationNode(this, dir, loc));
			}
		}
		return expandedNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Location> extractPlan() 
	{
		LinkedList<Location> plan = new LinkedList<Location>();
		
		for (Node n = this; n != null; n = n.getParent())
		{
			plan.addFirst(n.getLocation());
		}		
		return plan;
	}
}
