package srch.nodes;

import env.model.GridOperations;
import level.Direction;
import level.Location;
import srch.Node;
import srch.interfaces.IModelNode;

import java.util.ArrayList;
import java.util.List;

public class ClosestNode extends Node implements IModelNode {
	
	private Direction direction;
	private GridOperations gridOperations;
	
	public ClosestNode(Location initial, GridOperations gridOperations ) {
		this(initial, 0, gridOperations);
	}
	
	private ClosestNode(Location initial, int object, GridOperations gridOperations) {
		super(initial, object);
		this.direction 	= null;
		this.gridOperations 		= gridOperations;
	}

	private ClosestNode(Node parent, Direction direction, Location location) {
		super(parent, location);
		this.direction 	= direction;
		this.gridOperations 		= ((ClosestNode) parent).gridOperations;
	}

	public Direction getDirection() {
		return this.direction;
	}

	@Override
	public GridOperations getModel() {
		return this.gridOperations;
	}

	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperationsssss)
	{
		List<Node> expandedNodes = new ArrayList<Node>(Direction.EVERY.length);
		
		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);
			if (this.gridOperations.isFree(this.getObject(), loc))
			{
				expandedNodes.add(new ClosestNode(this, dir, loc));
			}
		}
		return expandedNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Location extractPlan()
	{
		return this.getLocation();
	}

}
