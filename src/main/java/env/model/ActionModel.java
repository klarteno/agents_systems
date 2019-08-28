package env.model;

import level.Location;
import level.action.Action;
import level.action.MoveAction;
import level.action.PullAction;
import level.action.PushAction;

import java.util.Set;

public class ActionModel {
    
    private static Set<Location> goalLocations;
	final GridOperations gridOperations;

	ActionModel(GridOperations model) {
		gridOperations = new GridOperations(model);
	}

	ActionModel(int width, int height) {
		gridOperations = new GridOperations(width, height);
	}
	
	public void setGoalLocations(Set<Location> locations) {
		goalLocations = locations;
	}
	
	public int countUnsolvedGoals() {
		return Math.toIntExact(goalLocations.stream().filter(goal -> !gridOperations.isSolved(goal)).count());
	}
	
	public void doExecute(Action action)
	{
        switch(action.getType())
        {
        case MOVE: doMove((MoveAction) action); return;
        case PUSH: doPush((PushAction) action); return;
        case PULL: doPull((PullAction) action); return;
		case SKIP:                              return;
        }
        throw new UnsupportedOperationException("Invalid action: " + action);    
	}
    
    private void doMove(MoveAction action)
    {
    	Location agLoc 	= action.getAgentLocation();
        Location nAgLoc 	= action.getNewAgentLocation();
    	
        move(GridOperations.AGENT, agLoc, nAgLoc);
    }
    
    private void doPush(PushAction action)
    {
    	Location agLoc 	= action.getAgentLocation();
    	Location nAgLoc 	= action.getNewAgentLocation();
    	Location boxLoc 	= action.getBoxLocation();
    	Location nBoxLoc = action.getNewBoxLocation();

        move(GridOperations.BOX, boxLoc, nBoxLoc);
        move(GridOperations.AGENT, agLoc, nAgLoc);
    }
    
    private void doPull(PullAction action)
    {
    	Location agLoc  	= action.getAgentLocation();
    	Location nAgLoc 	= action.getNewAgentLocation();
    	Location boxLoc 	= action.getBoxLocation();
    	Location nBoxLoc = action.getNewBoxLocation();

    	move(GridOperations.AGENT, agLoc, nAgLoc);
    	move(GridOperations.BOX, boxLoc, nBoxLoc);
    }
	
	public void move(int obj, Location fr, Location to)
	{		
		if ((obj & GridOperations.GOAL) != 0)
		{
			obj |= gridOperations.getMasked(GridOperations.GOAL_MASK, fr);
		}
		else if ((obj & GridOperations.BOX) != 0)
		{
			obj |= gridOperations.getMasked(GridOperations.BOX_MASK, fr);
			obj |= gridOperations.getMasked(GridOperations.COLOR_MASK, fr);
		}
		else if ((obj & GridOperations.AGENT) != 0)
		{
			obj |= gridOperations.getMasked(GridOperations.BOX_MASK, fr);
			obj |= gridOperations.getMasked(GridOperations.COLOR_MASK, fr);
		}

		gridOperations.remove(obj, fr);
		gridOperations.add(obj, to);
	}

	public GridOperations getGridOperations() {
		return gridOperations;
	}

	public boolean hasObject(int obj, Location l) {
		return gridOperations.hasObject(obj, l);
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
}
