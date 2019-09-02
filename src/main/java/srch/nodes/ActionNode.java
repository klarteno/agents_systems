package srch.nodes;

import env.model.GridOperations;
import env.model.SimulationModel;
import level.Location;
import level.action.Action;
import level.action.Action.ActionType;
import level.action.SkipAction;
import level.cell.Agent;
import level.cell.Tracked;
import srch.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class ActionNode extends Node {

	private Action action;
	private SimulationModel model;
	
	public ActionNode(Agent agent, Tracked tracked, int initialStep)
	{
		super(agent.getCopyLocation());

		action 	= null;
		model 	= new SimulationModel(initialStep, agent, tracked);
	}

	private ActionNode(Node parent, Action action, SimulationModel model)
	{
		super(parent, action.getNextAgentLocation());
		
		this.action = action;
		this.model 	= model;
	}

	public Action getAction()
	{
		return action;
	}
	
	private boolean isSkipNode()
	{
		if (action == null) return true;
		return action.getType() == ActionType.Skip;
	}
	
	public Location getTrackedLoc()
	{
		return model.getTrackedLocation();
	}
	
	public GridOperations getModel()
	{
		return model.getActionModel().getGridOperations();
	}

	public SimulationModel getSimulationModel()
	{
		return model;
	}


	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperations)
	{			
		List<Node> expandedNodes = new ArrayList<Node>();
		
		List<Action> actions = this.model.isTrackedAgent() ? Action.EveryMove(this.getLocation(), this.getAction())
																: Action.EveryBox(this.getLocation(), this.getAction());
		
		for (Action action : actions)
		{			
			if (model.canExecute(action))
			{
				expandedNodes.add(new ActionNode(this, action, model.run(action)));
			}
		}
		if (this.isSkipNode())
		{
			Action action = new SkipAction(this.getLocation());
			
			expandedNodes.add(new ActionNode(this, action, model.run(action)));
		}
		return expandedNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Action> extractPlan()
	{		
		LinkedList<Action> plan = new LinkedList<>();
		
		for (ActionNode n = this; n.getAction() != null; n = (ActionNode) n.getParent())
		{
			plan.addFirst(n.action);
		}
		return plan;
	}

	@Override
	public String toString() 
	{
		return this.getAction().toString() + " - " + super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ActionNode that = (ActionNode) o;
		return action.equals(that.action) &&
				model.equals(that.model);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), action, model);
	}
}
