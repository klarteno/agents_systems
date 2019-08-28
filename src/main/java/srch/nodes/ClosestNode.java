package srch.nodes;

import env.model.CellModel;
import env.model.GridOperations;
import level.Direction;
import level.Location;
import srch.Node;
import srch.interfaces.IDirectionNode;
import srch.interfaces.IModelNode;

import java.util.ArrayList;
import java.util.List;

public class ClosestNode extends Node implements IDirectionNode, IModelNode {
	
	private Direction direction;
	private CellModel model;
	
	public ClosestNode(Location initial, CellModel model) {
		this(initial, 0, model);
	}
	
	private ClosestNode(Location initial, int object, CellModel model) {
		super(initial, object);
		this.direction 	= null;
		this.model 		= model;
	}

	ClosestNode(Node parent, Direction direction, Location location) {
		super(parent, location);
		this.direction 	= direction;
		this.model 		= ((ClosestNode) parent).model;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}
	
	public GridOperations getModel() {
		return this.model.getGridOperations();
	}

	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperationsssss)
	{
		List<Node> expandedNodes = new ArrayList<Node>(Direction.EVERY.length);
		
		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);
			
			if (model.getGridOperations().isFree(this.getObject(), loc))
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
