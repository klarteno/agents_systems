package env.planner;

import env.model.CellModel;
import env.model.GridOperations;
import env.model.FutureModel;
import level.Location;
import level.action.*;
import level.cell.Agent;
import level.cell.Box;
import level.cell.Cell;
import level.cell.Colored;
import logging.LoggerFactory;
import srch.searches.ActionSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class Executor {
	private static final Logger logger = LoggerFactory.getLogger(Executor.class.getName());
	private Planner planner;
	private ActionSearch actionSearch;
	
	public Executor(Planner planner)
	{
		this.planner = planner;
		this.actionSearch = new ActionSearch(planner.worldProxy.getCellModel().getGridOperations());
	}

	/**
	 * Moves the agent to a location next to the box
	 * @param agent to move next to the box
	 * @param box 
	 * @return True if the movement was possible
	 */
	protected boolean getAgentToBox(Agent agent, Box box)
	{
		int initialStep = planner.getInitialStep(agent);
		List<Action> actions = actionSearch.search(agent, agent, box.getLocation(), 1, initialStep);

		if (actions == null)
		{
			logger.info(agent + " could not find path to box " + box.getLetter());
			return false;			
		}
		logger.info(agent + " to " + box + ":\t\t" + actions.toString());

		planner.getActions().get(agent.getNumber()).addAll(actions);
		executeActions(agent, initialStep, actions);
		
		return true;
	}
	
	/**
	 * Move an object to a location 
	 * @param agent which should move the object
	 * @param tracked The object to move
	 * @param location to move the the object to
	 * @return True if the movement is possible
	 */
	protected boolean getObjectToLocation(Agent agent, Cell tracked, Location location)
	{
		int initialStep = planner.getInitialStep(agent);
		List<Action> actions = actionSearch.search(agent, tracked, location, 0, initialStep);

		if (actions == null)
		{
			logger.info(agent.getName() + " could not find path to location " + location);
			return false;			
		}
		logger.info(tracked + " to " + location + ":\t\t" + actions.toString());

		planner.getActions().get(agent.getNumber()).addAll(actions);
		executeActions(agent, initialStep, actions);
		
		return true;
	}
	
	public void startStopActions(Agent agent, int steps)
	{
		int initialStep = planner.getInitialStep(agent);
		List<Action> actions = new ArrayList<Action>();
		
		for (int i = 0; i < steps; i++)
		{
			actions.add(new SkipAction(agent.getLocation()));
		}

		planner.getActions().get(agent.getNumber()).addAll(actions);
		executeActions(agent, initialStep, actions);

		logger.info(agent + " skipping: " + actions.size() + " times");
	}
	
	private void executeActions(Agent agent, int initialStep, List<Action> actions)
	{
		if (actions.isEmpty()) return;

		FutureModel futureModel = new FutureModel(planner.getModel(initialStep));
		int step = initialStep;
		// Create models for the actions
		planner.createModels(initialStep, actions);

		// Update the grid models with the actions
		for (Action action : actions)
		{
			futureModel.getCellMode().doExecute(action);
			updateModelWithLocations(futureModel.getOriginalLocations(), ++step);
		}
		
		for (int futureStep = ++step; futureStep < planner.dataModelCount(); futureStep++)
		{
			updateModelWithLocations(futureModel.getOriginalLocations(), futureStep);
		}

		for (int modelStep = initialStep + 1; modelStep < planner.dataModelCount(); modelStep++)
		{
			blockGridLocation(agent.getNumber(), modelStep);
		}
	}
	
	private void updateModelWithLocations(Map<Cell, Location> originalLocations, int step)
	{		
		CellModel model = planner.getModel(step);
		int objectType;
		// Remove old cells and store object references
		for (Entry<Cell, Location> entry : originalLocations.entrySet())
		{
			objectType = entry.getKey() instanceof Agent ? GridOperations.AGENT : GridOperations.BOX;
			Cell object = model.removeCell(objectType, entry.getValue());
			model.addCell((Colored) entry.getKey(), object);
		}
	}

	private void blockGridLocation(int agentNumber, int fromAction)
	{
		GridOperations model = planner.getModel(fromAction).getGridOperations();
		List<Action> agentActions = planner.getActions().get(agentNumber);
		
		for (int nextAction = fromAction - 1; nextAction < agentActions.size(); nextAction++)
		{
			Action action = agentActions.get(nextAction);
			model.add(GridOperations.LOCKED, action.getNextAgentLocation());
			
			if (action instanceof PullAction)
			{
				model.add(GridOperations.LOCKED, ((ActionBoxMove) action).getNewBoxLocation());
			}
			else if (action instanceof PushAction)
			{
				model.add(GridOperations.LOCKED, ((ActionBoxMove) action).getNewBoxLocation());
			}
		}
	}
}
