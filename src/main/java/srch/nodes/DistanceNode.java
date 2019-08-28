package srch.nodes;

import env.model.GridOperations;
import env.model.WorldFactory;
import level.Direction;
import level.Location;
import srch.Node;
import srch.interfaces.IDirectionNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistanceNode extends Node implements IDirectionNode {

	private Direction direction;

	public DistanceNode(Location initial)
	{
		super(initial);
		
		this.direction = null;
	}

	public DistanceNode(Node parent, Location location, Direction direction)
	{
		super(parent, location);
		this.direction = direction;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}	

	@Override
	public List<? extends Node> getExpandedNodes(GridOperations gridOperations) {
		List<Node> expandedNodes = new ArrayList<>();
		
		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);
			
			//if (WorldFactory.getInstance().getCellModel().getGridOperations().isFree(this.getObject(), loc))
			if(gridOperations.isFree(this.getObject(), loc))
			{
				expandedNodes.add(new DistanceNode(this, loc, dir));
			}			
		}		
		
		return expandedNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Location, Integer> extractPlan()
	{
		Map<Location, Integer> distances = new HashMap<>();
		
		int distance = 0;
		
		for (Node n = this; n != null; n = n.getParent())
		{
			distances.put(n.getLocation(), distance++);
		}
		
		return distances;
	}
	
	
}
