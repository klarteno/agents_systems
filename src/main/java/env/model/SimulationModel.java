package env.model;

import env.planner.Planner;
import level.Location;
import level.action.Action;
import level.action.MoveAction;
import level.action.PullAction;
import level.action.PushAction;
import level.cell.Agent;
import level.cell.Cell;

import java.util.List;

public class SimulationModel {

	private static Planner planner;
	private final MyActionModel actionModel;

	private int 	currentStep, 
					nextStep;
	private Agent agent;
	private Cell tracked;
	private boolean isAgent;

	public SimulationModel(int step, Agent agent, Cell tracked)
	{
		this(planner.getModel(step).getGridOperations(), step, agent, tracked);
	}
	
	private SimulationModel(GridOperations model, int step, Agent agent, Cell tracked)
	{
		this(model, step, agent, tracked, tracked instanceof Agent);
	}

	private SimulationModel(GridOperations model, int step, Agent agent, Cell tracked, boolean isAgent)
	{
		actionModel = new MyActionModel(model);
		
		SimulationModel.this.currentStep 	= step;
		SimulationModel.this.nextStep		= step + 1;
		SimulationModel.this.agent			= new Agent(agent);
		SimulationModel.this.tracked  		= new Cell(tracked);
		SimulationModel.this.isAgent 		= isAgent;

		CellModel cellModel = SimulationModel.planner.getWorldProxy().getCellModel();

		for (Agent otherAgent : cellModel.getAgents())
		{
			List<Action> actionList = planner.getActions().get(otherAgent.getNumber());
			
			if (currentStep < actionList.size())
			{
				actionModel.doExecute(actionList.get(currentStep));
			}
		}			
	}
	
	public static void setPlanner(Planner planner)
	{
		SimulationModel.planner = planner;
	}
	
	public Location getTrackedLocation()
	{
		return tracked.getLocation();
	}
	
	public boolean isTrackedAgent()
	{
		return isAgent;
	}
	
	public Agent getAgent()
	{
		return agent;
	}
	
	public int getStep()
	{
		return currentStep;
	}

	public SimulationModel run(Action action)
    {    	
    	SimulationModel simulation = new SimulationModel(actionModel.getGridOperations(), nextStep, agent, tracked, isAgent);

		simulation.actionModel.doExecute(action);

		return simulation;
    }

	public boolean canExecute(Action action)
	{		
        switch(action.getType())
        {
        case MOVE: return canMove((MoveAction) action);
        case PUSH: return canPush((PushAction) action);
        case PULL: return canPull((PullAction) action);
        case SKIP: return true;
        }
        throw new UnsupportedOperationException("Invalid action: " + action.getType());        
	}

    private synchronized boolean canMove(MoveAction action)
    {        
        Location nAgLoc = action.getNewAgentLocation();
        
        if (nAgLoc == null) return false;

		if (!actionModel.gridOperations.isFree(nAgLoc)) return false;

		if (planner.hasModel(currentStep) && !planner.getModel(currentStep).gridOperations.isFree(nAgLoc)) return false;

		if (planner.hasModel(nextStep) && !planner.getModel(nextStep).gridOperations.isFree(nAgLoc)) return false;

		return !planner.hasAgentWithOppositeAction(currentStep, action);
	}
    
    private boolean canPush(PushAction action)
    {    	
    	Location agLoc 	 = action.getAgentLocation();
    	Location boxLoc  = action.getBoxLocation();
    	Location nBoxLoc = action.getNewBoxLocation();

		if (actionModel.gridOperations.isFree(GridOperations.BOX, boxLoc)) return false;
		if (!actionModel.gridOperations.isFree(nBoxLoc)) return false;

		int agColor = actionModel.gridOperations.getMasked(GridOperations.COLOR_MASK, agLoc);
		int boxColor = actionModel.gridOperations.getMasked(GridOperations.COLOR_MASK, boxLoc);
        
        if (agColor != boxColor) return false;

		if (planner.hasModel(currentStep) && !planner.getModel(currentStep).gridOperations.isFree(nBoxLoc)) return false;
		return !planner.hasModel(nextStep) || planner.getModel(nextStep).gridOperations.isFree(nBoxLoc);
	}
    
    private boolean canPull(PullAction action)
    {    	
    	Location agLoc 	= action.getAgentLocation();
    	Location nAgLoc = action.getNewAgentLocation();
    	Location boxLoc = action.getBoxLocation();
        
        if (agLoc == null) return false;

		int agColor = actionModel.gridOperations.getMasked(GridOperations.COLOR_MASK, agLoc);
    	
    	if (boxLoc == null) return false;

		if (actionModel.getGridOperations().isFree(GridOperations.BOX, boxLoc)) return false;

		int boxColor = actionModel.gridOperations.getMasked(GridOperations.COLOR_MASK, boxLoc);
    	
    	if (agColor != boxColor) return false;
    	
    	if (nAgLoc == null) return false;

		if (!actionModel.gridOperations.isFree(nAgLoc)) return false;

		if (planner.hasModel(currentStep) && !planner.getModel(currentStep).gridOperations.isFree(nAgLoc)) return false;

		return !planner.hasModel(nextStep) || planner.getModel(nextStep).gridOperations.isFree(nAgLoc);
	}

	public ActionModel getActionModel() {
		return actionModel;
	}


	public String toString() {
		return actionModel.toString();
	}

	public int hashCode() {
		return actionModel.hashCode();
	}

	public boolean equals(Object obj) {
		return actionModel.equals(obj);
	}


	private class MyActionModel extends ActionModel {
		MyActionModel(GridOperations model) {
			super(model);
		}

		public void move(int obj, Location fr, Location to)
		{
			if (tracked.getLocation().equals(fr))
			{
				tracked.setLocation(to);
			}
			super.move(obj, fr, to);
		}
	}
}
