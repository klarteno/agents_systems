package srch.nodes;

import env.model.CellModel;
import env.model.GridOperations;
import level.Direction;
import level.Location;
import level.cell.Agent;
import srch.Node;
import util.ModelUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageNode extends ClosestNode {
	
	private Agent 	agent;
	private int		agNumber;

	public StorageNode(Location initial, Agent agent, CellModel model) {
		super(initial, model);
		this.agent 		= agent;		
		this.agNumber 	= ModelUtil.getAgentNumber(agent);
	}

	private StorageNode(Node parent, Direction direction, Location location) {
		super(parent, direction, location);		
		this.agent 		= ((StorageNode) parent).agent;
		this.agNumber 	= ((StorageNode) parent).agNumber;
	}
	
	public int getAgentNumber() {
		return agNumber;
	}

	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperationssss)
	{
		List<Node> expandedNodes = new ArrayList<Node>(Direction.EVERY.length);
		
		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);
			
				// Add node if loc has agent itself or
			if (this.getModel().hasObject(agNumber, GridOperations.BOX_MASK, loc) ||
				// Add node if loc is free of object or
				this.getModel().isFree(this.getObject(), loc))
			{
				expandedNodes.add(new StorageNode(this, dir, loc));
			}
			
			// Add node if loc has box with agent's color and
			// this box can be moved to another loc
			if (this.getModel().hasObject(GridOperations.BOX, loc) &&
				this.getModel().getColor(loc).equals(agent.getColor()) &&
				!this.getModel().isBlocked(loc))
			{
				expandedNodes.add(new StorageNode(this, dir, loc));
			}
		}
		return expandedNodes;
	}
	

}
