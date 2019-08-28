package srch.searches;

import env.model.GridOperations;
import env.model.SimulationModel;
import env.planner.Planner;
import level.Direction;
import level.Location;
import level.action.Action;
import level.cell.Agent;
import level.cell.Cell;
import level.cell.Goal;
import srch.Evaluation.AStar;
import srch.Heuristic;
import srch.Node;
import srch.Search;
import srch.Strategy.BestFirst;
import srch.nodes.ActionNode;
import srch.nodes.DistanceNode;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ActionSearch extends Search implements Heuristic {

	public static List<Action> search(Agent agent, Cell tracked, Location to, int proximity, int initialStep,GridOperations gridOperations)
	{
		return new ActionSearch(tracked.getLocation(), to, proximity,gridOperations).search(new ActionNode(agent, tracked, initialStep),gridOperations);
	}
	
	private Map<Location, Integer> distances;
	private Location goalLocation;
	private int 	 goalDistance;
	
	private ActionSearch(Location from, Location to, int proximity,GridOperations gridOperations)
	{
		distances = new DistanceSearch(to, 0)
				.search(new DistanceNode(from),gridOperations);

		logger.setLevel(Level.OFF);
		
		this.setStrategy(new BestFirst(new AStar(this)));
		
		this.goalLocation = to;
		this.goalDistance = proximity;
	}

	@Override
	public boolean isGoalState(Node n) 
	{
		ActionNode node = (ActionNode) n;
		
		SimulationModel model = node.getSimulationModel();
		
		int step = model.getStep();
		
		if (Planner.getInstance().hasModel(step) && Planner.getInstance().getModel(step).hasObject(GridOperations.LOCKED, n.getLocation()))
		{
			return false;
		}

		return   node.getTrackedLoc().distance(goalLocation) == goalDistance &&
				!model.getActionModel().getGridOperations().isBlocked(n.getLocation());
	}

	@Override
	public int h(Node n) 
	{
		ActionNode node = (ActionNode) n;
		SimulationModel model = node.getSimulationModel();
		
		int goalDist = 0;
		
		Location loc = model.getTrackedLocation();
		
		if (distances.containsKey(loc))
		{
			goalDist += distances.get(loc);
		}
		else
		{
			// Find closest location in distance map if not present
			Location closest = distances.keySet().stream().min((l1, l2) -> l1.distance(loc) - l2.distance(loc)).get();
			
			goalDist += distances.get(closest);
			goalDist += loc.distance(closest);
		}

		if (loc.distance(n.getLocation()) > 1)
		{
			goalDist += loc.distance(n.getLocation());
		}

		goalDist += model.getActionModel().countUnsolvedGoals();
		
		Goal nextGoal = model.getAgent().peekFirst();
		
		if (nextGoal != null)
		{
			Direction nextDir = node.getAction().getAgentLocation().inDirection(nextGoal.getBox().getLocation());
			Direction agDir = node.getAction().getAgentLocation().inDirection(node.getAction().getNewAgentLocation());
			
			if (nextDir != null && agDir != null && !nextDir.hasDirection(agDir))
			{
				goalDist += 1;
			}
		}

		return goalDist; 
	}
}
