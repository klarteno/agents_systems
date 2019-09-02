package env.planner;

import env.model.CellModel;
import env.model.FutureModel;
import env.model.GridOperations;
import level.Location;
import level.action.Action;
import level.action.ActionBoxMove;
import level.action.SkipAction;
import level.cell.Agent;
import level.cell.Box;
import level.cell.Colored;
import level.cell.Tracked;
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
		this.actionSearch = new ActionSearch(planner.worldProxy.getInitialModel().getGridOperations());
	}

	/**
	 * Moves the agent to a location next to the box
	 * @param agent to move next to the box
	 * @param trackedBox
	 * @return True if the movement was possible
	 */
	protected boolean getAgentToBox(Agent agent, Tracked trackedBox)
	{
		int initialStep = planner.getInitialStep(agent);
		Tracked tracked = new Tracked();
		tracked.setLocation(agent.getCopyLocation());
		tracked.type= Tracked.Type.AGENTT;
		tracked.agent = agent;
		List<Action> actions = actionSearch.search(agent, tracked, trackedBox.box.getCopyLocation(), 1, initialStep);

		if (actions == null)
		{
			logger.info(agent + " could not find path to box " + trackedBox.box.getLetter());
			return false;			
		}
		logger.info(agent + " to " + trackedBox.toString() + ":\t\t" + actions.toString());

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
	protected boolean getObjectToLocation(Agent agent, Tracked tracked, Location location)
	{
		int initialStep = planner.getInitialStep(agent);

		//Tracked  tracked2 = new Tracked();
		//tracked2.setLocation(tracked);

		List<Action> actions = actionSearch.search(agent, tracked, location, 0, initialStep);

		if (actions == null)
		{
			logger.info(agent.getName() + " could not find path to location " + location);
			return false;			
		}
		logger.info(tracked.toString() + " to " + location + ":\t\t" + actions.toString());

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
			actions.add(new SkipAction(agent.getCopyLocation()));
		}

		planner.getActions().get(agent.getNumber()).addAll(actions);
		executeActions(agent, initialStep, actions);

		logger.info(agent + " skipping: " + actions.size() + " times");
	}

	//			get only the agent modified
	public void executeActions(Agent agent, int initialStep, List<Action> actions)
	{
		if (actions.isEmpty()) return;

		CellModel cellModel = planner.getModel(initialStep);
		FutureModel futureModel = new FutureModel(cellModel);

		boolean result2 = cellModel.equals(planner.getModel(initialStep));
		boolean result3 = cellModel.equals(futureModel.getCellMode());
		FutureModel futureModel2 = new FutureModel(cellModel);
		boolean result12 = (futureModel2.getCellMode()).equals(futureModel.getCellMode());

		int step = initialStep;
		// Create models for the actions
		planner.createModels(initialStep, actions);

		// Update the grid models with the actions
		for (Action action : actions)
		{
			futureModel.getCellMode().doExecute(action);
			updateModelWithLocations(futureModel.getOriginalLocations(), ++step);
		}

		FutureModel futureModel3 = new FutureModel(cellModel);
		boolean result123 = futureModel3.getCellMode().equals(futureModel.getCellMode());

		for (int futureStep = ++step; futureStep < planner.dataModelCount(); futureStep++)
		{
			updateModelWithLocations(futureModel.getOriginalLocations(), futureStep);
		}

		for (int modelStep = initialStep + 1; modelStep < planner.dataModelCount(); modelStep++)
		{
			blockGridLocation(agent.getNumber(), modelStep);
		}

		FutureModel futureModel22 = new FutureModel(cellModel);
		boolean resultttttttt = futureModel22.getCellMode().equals(futureModel.getCellMode());

	}
	
	private void updateModelWithLocations(Map<Location, Location> originalLocations, int step)
	{		
		CellModel model = planner.getModel(step);
		int objectType;
		// Remove old cells and store object references
		for (Entry<Location, Location> entry : originalLocations.entrySet())
		{
			objectType = entry.getKey() instanceof Agent ? GridOperations.AGENT : GridOperations.BOX;
			Location object = model.removeCell(objectType, entry.getValue());
			model.addCell((Colored) entry.getKey(), object);
		}
	}

	private void blockGridLocation(int agentNumber, int modelStep)
	{
		GridOperations model = planner.getModel(modelStep).getGridOperations();
		List<Action> agentActions = planner.getActions().get(agentNumber);
		
		for (int nextAction = modelStep - 1; nextAction < agentActions.size(); nextAction++)
		{
			Action action = agentActions.get(nextAction);
			model.add(GridOperations.LOCKED, action.getNextAgentLocation());
			
			if (action.getType() == Action.ActionType.Pull
				||action.getType() == Action.ActionType.Push)
			{
				model.add(GridOperations.LOCKED, ((ActionBoxMove) action).getNewBoxLocation());
			}
		}
	}
}
