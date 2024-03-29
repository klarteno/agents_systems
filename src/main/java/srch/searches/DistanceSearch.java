package srch.searches;

import env.model.GridOperations;
import level.Location;
import srch.Evaluation.AStar;
import srch.Heuristic;
import srch.Node;
import srch.Search;
import srch.Strategy.BestFirst;
import srch.nodes.DistanceNode;

import java.util.Map;

public class DistanceSearch extends Search implements Heuristic {
/*
	public static Map<Location, Integer> search(Location from, Location to, GridOperations gridOperations)
	{
		//maybe bug ????
		return new DistanceSearch(to, 0).search(new DistanceNode(from),gridOperations);
	}
	*/
	private Location goalLocation;
	private int 	 goalDistance;
	
	public DistanceSearch(Location to, int proximity)
	{
		this.setStrategy(new BestFirst(new AStar(this)));
		
		this.goalLocation = to;
		this.goalDistance = proximity;
	}

	@Override
	public boolean isGoalState(Node n)
	{
		return n.getLocation().distance(goalLocation) == goalDistance;
	}

	@Override
	public int h(Node n) 
	{		
		return n.getLocation().distance(goalLocation);
	}
}
