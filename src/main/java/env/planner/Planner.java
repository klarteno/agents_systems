package env.planner;

import env.model.*;
import level.DependencyPath;
import level.Location;
import level.action.Action;
import level.cell.Agent;
import level.cell.Box;
import level.cell.Goal;
import level.cell.Tracked;
import logging.LoggerFactory;
import srch.nodes.DistanceNode;
import srch.searches.DistanceSearch;
import srch.searches.closest.AgentSearch;
import srch.searches.closest.StorageSearch;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Planner {
	private static final Logger logger = LoggerFactory.getLogger(Planner.class.getName());

	private static Planner instance;

	private ArrayList<CellModel> 	dataModels;
	private ArrayList<List<Action>> actions;
	
	private Executor executor;
	WorldFactory worldProxy;
	private DependencyPath dependencyPath;
	private StorageSearch storageSearch;

	public Planner(WorldFactory worlProxy) {
		this.worldProxy = worlProxy;

		dependencyPath = new DependencyPath();
		storageSearch = new StorageSearch();
	}

	public static Planner getInstance() {
		return instance;
	}
	public void  setExecutor(Executor executor) {
		this.executor = executor;
	}
	public int dataModelCount() {
		return dataModels.size();
	}
	public WorldFactory getWorldProxy(){
		return this.worldProxy;
	}

	public CellModel initPlan()
	{
		logger.setLevel(Level.WARNING);
		
		instance = this;
		SimulationModel.setPlanner(this);
		dataModels = new ArrayList<>(Arrays.asList(new CellModel(worldProxy.getInitialModel())));
		actions = new ArrayList<List<Action>>(worldProxy.getInitialModel().getNbAgs());
		
		for (int i = 0; i < worldProxy.getInitialModel().getNbAgs(); i++)
		{
			actions.add(new ArrayList<Action>());
		}
		
		return worldProxy.getInitialModel();
	}

	public boolean solveGoal(Goal goal)
	{		
		Box box			= goal.getBox();
		Agent agent 	= box.getAgent();
		Location loc 	= goal.getCopyLocation();
		agent.removeGoal(goal);

		Tracked trackedBox = new Tracked();
		trackedBox.setLocation(box.getCopyLocation());
		trackedBox.type= Tracked.Type.BOXX;
		trackedBox.box = box;

		return planAgentToBox(agent, trackedBox) && planObjectToLocation(agent, trackedBox, loc);
	}

	private boolean planAgentToBox(Agent agent, Tracked trackedBox)
	{
		int distance = agent.getCopyLocation().distance(trackedBox.getLocation().getCopyLocation());
		if (distance  == 1) return true;

		int				step			= getInitialStep(agent);
		CellModel 		model 			= getModel(step);
		DependencyPath 	dependencyPath 	= this.dependencyPath.getDependencyPath(agent, trackedBox, step, worldProxy.getInitialModel().getGridOperations());

		if (dependencyPath.hasDependencies())
		{
			Entry<Location, Integer> dependency = dependencyPath.getDependency(agent.getCopyLocation(),this);
			
			if (step < dependency.getValue()) 
			{
				executor.startStopActions(agent, dependency.getValue() - step);
				return planAgentToBox(agent, trackedBox);
			}

			// Add overlay for getting box to goal
			MaskGridCollection overlay			= new MaskGridCollection(worldProxy.getInitialModel().getGridOperations());
			if (trackedBox.box.getGoal() != null)
			{
				Map<Location, Integer> result = new DistanceSearch(trackedBox.box.getGoal().getCopyLocation(), 0)
						.search(new DistanceNode(trackedBox.getLocation().getCopyLocation()), worldProxy.getInitialModel().getGridOperations());

				Set<Location> tmp = result.keySet();
				overlay.addOverlay(tmp);
			}
			List<Location> tmp = dependencyPath.getPath();
			overlay.addOverlay(tmp);
			int newStep = solveDependency(agent, dependency.getKey(), overlay, model);
			overlay.removeOverlay(); //could be removed

			if (newStep > getInitialStep(agent)) 
			{
				// Maximum: newStep - step, 1 for optimal solution
//				executor.executeSkips(agent, newStep - step);
				executor.startStopActions(agent, 1);
			}			
			return planAgentToBox(agent, trackedBox);
		}

		return executor.getAgentToBox(agent, trackedBox);
	}

	private boolean planObjectToLocation(Agent agent, Tracked tracked, Location loc)
	{
		int				step			= getInitialStep(agent);
		CellModel 		model 			= getModel(step);
		DependencyPath 	dependencyPath 	= this.dependencyPath.getDependencyPath(agent, tracked.getLocation(), loc, step, worldProxy.getInitialModel().getGridOperations());

		if (dependencyPath.hasDependencies())
		{
			Entry<Location, Integer> dependency = dependencyPath.getDependency(tracked.getLocation().getCopyLocation(),this);
			
			if (step < dependency.getValue()) 
			{
				executor.startStopActions(agent, dependency.getValue() - step);
				return planAgentToTracked(agent, tracked, loc);
			}

			MaskGridCollection overlay			= new MaskGridCollection(worldProxy.getInitialModel().getGridOperations());
			overlay.addOverlay(dependencyPath.getPath());
			int newStep = solveDependency(agent, dependency.getKey(), overlay, model);
			overlay.removeOverlay(); //could be removed

			if (newStep > getInitialStep(agent)) 
			{
				// Maximum: newStep - step, 1 for optimal solution
//				executor.executeSkips(agent, newStep - step);
				executor.startStopActions(agent, 1);
			}
			return planAgentToTracked(agent, tracked, loc);
		}

		return executor.getObjectToLocation(agent, tracked, loc);
	}
	
	private boolean planAgentToTracked(Agent agent, Tracked tracked, Location loc)
	{
		if (tracked.type == Tracked.Type.BOXX )
		{
			planAgentToBox(agent, tracked);
			return planObjectToLocation(agent, tracked, loc);
		}
		return planObjectToLocation(agent, tracked, loc);
	}

	private int solveDependency(Agent toHelp, Location dependency, MaskGridCollection overlay, CellModel model)
	{		
		if (model.hasObject(GridOperations.BOX, dependency))
		{
			Box box 	= model.getBox(dependency);
			AgentSearch agentSearch = new AgentSearch(model);
			Agent agent 	= model.getAgent(agentSearch.search(box.getColor(), box.getCopyLocation()));

			Tracked trackedBox = new Tracked();
			trackedBox.setLocation(box.getCopyLocation());
			trackedBox.type= Tracked.Type.BOXX;
			trackedBox.box = box;

			return solveAgentToBoxDependency(toHelp, agent, trackedBox, overlay);
		}
		else if (model.hasObject(GridOperations.AGENT, dependency))
		{
			Agent agent = model.getAgent(dependency);
			Tracked trackedAgent = new Tracked();
			trackedAgent.setLocation(agent.getCopyLocation());
			trackedAgent.type= Tracked.Type.AGENTT;
			trackedAgent.agent = agent;

			return solveObjectToLocationDependency(toHelp, agent, trackedAgent, overlay);
		}		
		throw new UnsupportedOperationException("Attempt to solve unknown dependency");
	}

	private int solveAgentToBoxDependency(Agent toHelp, Agent agent, Tracked boxTracked, MaskGridCollection overlay)
	{
		int agentStep = getInitialStep(agent);
		if (agentStep > getInitialStep(toHelp))
		{
			return agentStep;
		}
		
		planAgentToBox(agent, boxTracked);
		if (toHelp.equals(agent)) overlay.removeOverlay();
		
		return solveObjectToLocationDependency(toHelp, agent, boxTracked, overlay);
	}
	
	private int solveObjectToLocationDependency(Agent toHelp, Agent agent, Tracked tracked, MaskGridCollection overlay)
	{
		int agentStep = getInitialStep(agent);
		if (agentStep > getInitialStep(toHelp))
		{
			return agentStep;
		}

		CellModel model = getModel(agentStep);
		
		Location storage = null;
		boolean isAgent = tracked.type == Tracked.Type.AGENTT;
		if (toHelp.equals(agent))
		{
			storage = storageSearch.search(tracked.getLocation().getCopyLocation(), agent, true, isAgent, overlay, model, worldProxy.getFreeCellCount(), worldProxy.getInitialModel().getGridOperations());
		}

		if (storage == null)
		{
			storage = storageSearch.search(tracked.getLocation().getCopyLocation(), agent, false, isAgent, overlay, model, worldProxy.getFreeCellCount(), worldProxy.getInitialModel().getGridOperations());
		}		
		
		if (storage == null)
		{
			logger.warning("Could not find storage");
			throw new UnsupportedOperationException("No storage available");
		}
		
		planObjectToLocation(agent, tracked, storage);
		
		return -1;
	}

	/**
	 * @param agent
	 * @return The initial step of the agent, which is the length of the agent's actions + 1
	 */
	int getInitialStep(Agent agent)
	{
		return actions.get(agent.getNumber()).size();
	}
	
	/**
	 * @param step
	 * @param action
	 * @return True, if there is an agent with the opposite action in the step
	 */
	public boolean hasAgentWithOppositeAction(int step, Action action)
	{
		if (hasModel(step))
		{
			CellModel model = getModel(step);
			Agent agent = model.getAgent(action.getNextAgentLocation());
			
			if (agent != null && step < actions.get(agent.getNumber()).size())
			{
				return action.isOpposite(actions.get(agent.getNumber()).get(step));
			}
		}
		return false;
	}
	
	/**
	 * @param step to check
	 * @return True if the planner already has this step in its models
	 */
	public boolean hasModel(int step) {
		return step < dataModels.size();
	}
	
	public CellModel getModel(Agent agent) {
		return getModel(getInitialStep(agent));
	}
	
	/**
	 * @param step
	 * @return The data model with the given step
	 */
	public CellModel getModel(int step)
	{
		if (step > dataModels.size())
		{
			throw new UnsupportedOperationException("getModel - step is too large: " + step + " Size is only: " + dataModels.size());
		}
		
		// Should only trigger when step == gridModels.size()
		if (!hasModel(step))
		{
			dataModels.add(new CellModel(dataModels.get(step - 1)));
		}
		
		return dataModels.get(step);
	}

	void createModels(int initialStep, List<Action> actions)
	{
		for (int modelCount = initialStep + 1; modelCount < initialStep + actions.size() + 1; modelCount++)
		{
			this.getModel(modelCount);
		}
	}

	public int getDependencyCount(int step, Location loc, int dependency){
		int dependencyCount = 0;

		if (this.getLastStep() < step && getModel(getLastStep()).hasObject(dependency, loc))
		{
			dependencyCount++;
		}
		else if (hasModel(step - 1) && getModel(step - 1).hasObject(dependency, loc) ||
				hasModel(step) && getModel(step).hasObject(dependency, loc) ||
				hasModel(step + 1) && getModel(step + 1).hasObject(dependency, loc))
		{
			dependencyCount++;
		}
		return dependencyCount;
	}


	public List<List<Action>> getActions()
	{
		return actions;
	}
	
	/**
	 * @return The length of the solution
	 */
	public int getLastStep()
	{
		//or min of actions
		Optional<List<Action>> action = actions.stream().max(Comparator.comparingInt(List::size));
		return action.map(List::size).orElse(0);
	}
}

