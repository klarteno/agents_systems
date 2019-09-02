package srch.searches;

import env.model.GridOperations;
import env.planner.Planner;
import level.DependencyPath;
import level.Direction;
import level.Location;
import level.cell.Agent;
import srch.Evaluation.AStar;
import srch.Heuristic;
import srch.Node;
import srch.Search;
import srch.Strategy.BestFirst;
import util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public class DependencyPathSearch extends Search implements Heuristic {

	private Location goalLocation;
	
	public DependencyPathSearch(Location to)
	{
		this.setStrategy(new BestFirst(new AStar(this)));
		goalLocation = to;
	}

	public Object getDependencyPath(Agent agent, Location from, Location to, boolean toBox, int initialStep, GridOperations gridOperations){
		int obj = GridOperations.BOX | GridOperations.AGENT;

		return search(new DependencyPathNode(from, agent, obj, toBox, initialStep),gridOperations);
	}

	@Override
	public boolean isGoalState(Node n) 
	{
		return n.getLocation().distance(goalLocation) == 0;
	}

	@Override
	public int h(Node n) 
	{
		int h = 0;
		
		h += n.getLocation().distance(goalLocation);
		
		h += ((DependencyPathNode) n).getDependencyCount() * 25;
		
		h += ((DependencyPathNode) n).getModel().isSolved(n.getLocation()) ? 10 : 0;
		
		return h; 
	}
}



class DependencyPathNode extends Node  {

	private int step;

	private static Planner planner = Planner.getInstance();

	private Direction direction;
	private Agent		agent;
	private int 		dependency;
	private int 		dependencyCount;
	private boolean 	ignoreLast;
	private GridOperations model;

	public DependencyPathNode(Location initial, Agent agent, int dependency, boolean includeLast, int initialStep)
	{
		super(initial);

		this.step = initialStep;

		this.direction 			= null;
		this.agent				= agent;
		this.dependency 		= dependency;
		this.dependencyCount 	= 0;
		this.ignoreLast			= includeLast;
		this.model				= planner.getModel(initialStep).getGridOperations();
	}

	private DependencyPathNode(DependencyPathNode parent, Direction dir, Location loc)
	{
		super(parent, loc);

		this.step = parent.getStep() + 1;

		this.direction			= dir;
		this.agent				= parent.agent;
		this.dependency 		= parent.dependency;
		this.dependencyCount 	= parent.dependencyCount + planner.getDependencyCount(getStep(),loc,this.dependency);;
		this.ignoreLast			= parent.ignoreLast;
		this.model				= parent.model;
		//dependencyCount +=  planner.getDependencyCount(getStep(),loc,this.dependency);
	}

	@Override
	public DependencyPathNode getParent() {
		return (DependencyPathNode) super.getParent();
	}

	private int getStep()
	{
		return this.step;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getDependencyCount() {
		return dependencyCount;
	}

	public GridOperations getModel() {
		return model;
	}

	@Override
	public List<Node> getExpandedNodes(GridOperations gridOperationssss)
	{
		List<Node> expandedNodes = new ArrayList<Node>(Direction.EVERY.length);

		for (Direction dir : Direction.EVERY)
		{
			Location loc = this.getLocation().newLocation(dir);

			if (model.isFree(this.getObject(), loc))
			{
				expandedNodes.add(new DependencyPathNode(this, dir, loc));
			}
		}
		return expandedNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DependencyPath extractPlan()
	{
		DependencyPath path = new DependencyPath();

		int agNumber = CollectionUtil.getAgentNumber(agent);
		agNumber 	= agent.getNumber();


		for (int futureStep = this.getStep(); futureStep < planner.dataModelCount(); futureStep++)
		{
			if (hasDependency(planner.getModel(futureStep).getGridOperations(), this, agNumber))
			{
				path.addDependency(this.getLocation(), futureStep);
			}
			if (this.getParent() != null && hasDependency(planner.getModel(futureStep).getGridOperations(), this.getParent(), agNumber))
			{
				path.addDependency(this.getParent().getLocation(), futureStep);
			}
		}

		for (DependencyPathNode n = this; n != null; n = (DependencyPathNode) n.getParent())
		{
			Location loc = n.getLocation();

			path.addToPath(loc);

			// Avoid checking model(-1)
			if (n.getParent() == null) break;

			for (int step : new int[]{ n.getStep() - 1, n.getStep(), n.getStep() + 1 })
			{
				if (planner.getLastStep() < n.getStep() && hasDependency(planner.getModel(planner.getLastStep()).getGridOperations(), n, agNumber))
				{
					path.addDependency(loc, planner.getLastStep());
				}
				else if (planner.hasModel(step) && hasDependency(planner.getModel(step).getGridOperations(), n, agNumber))
				{
					path.addDependency(loc, step);
				}
			}
		}
		return path;
	}

	private boolean hasDependency(GridOperations model, DependencyPathNode n, int agNumber)
	{
		Location loc = n.getLocation();

		return (model.hasObject(dependency, loc) &&
				// Do not add dependency if n is last and ignoreLast
				!(n == this && ignoreLast) &&
				// Do not add dependency if n is first
				!(n.getParent() == null) &&
				// Do not add dependency if dependency is box of agent's color
//				!(model.hasObject(DataModel.BOX, loc) && model.getColor(loc).equals(agent.getColor())) &&
				// Do not add dependency if dependency is agent itself
				!(model.hasObject(agNumber, GridOperations.BOX_MASK, loc)));
	}

}




