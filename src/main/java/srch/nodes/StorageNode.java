package srch.nodes;

import env.model.GridOperations;
import level.Direction;
import level.Location;
import level.cell.Agent;
import srch.Node;
import util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageNode extends Node {

	private Agent 	agent;
	private int		agNumber;

	private GridOperations gridOperations;

	public StorageNode(Location initial, Agent agent, GridOperations gridOperations) {
		super(initial, 0);

		this.gridOperations 		= gridOperations;
		this.agent 		= agent;		
		this.agNumber 	= CollectionUtil.getAgentNumber(agent);
		this.agNumber 	= agent.getNumber();
	}

	private StorageNode(Node parentNode, Direction direction, Location location) {
		super(parentNode, location);

		StorageNode p = (StorageNode) parentNode;
		this.gridOperations 		= p.gridOperations;
		this.agent 		= p.agent;
		this.agNumber 	= p.agNumber;
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
					this.getModel().isNextCellFree(loc))
			{
				expandedNodes.add(new StorageNode(this, dir, loc));
			}
		}
		return expandedNodes;
	}

	public GridOperations getModel() {
		return this.gridOperations;
	}

	@Override
	public Location extractPlan()
	{
		return this.getLocation();
	}
}
