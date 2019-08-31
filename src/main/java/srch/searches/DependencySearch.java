package srch.searches;

import env.model.CellModel;
import env.model.GridOperations;
import level.Location;
import srch.Evaluation.AStar;
import srch.Heuristic;
import srch.Node;
import srch.Search;
import srch.Strategy.BestFirst;
import srch.nodes.DependencyNode;

import java.util.List;

public class DependencySearch extends Search implements Heuristic {

	private CellModel cellModel;
	
	private Location goalLocation;

	public DependencySearch(CellModel cellModel)
	{
		this.cellModel = cellModel;
		this.setStrategy(new BestFirst(new AStar(this)));
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @param object
	 * @return
	 */
	public List<Location> search(Location from, Location to, int object)
	{
		this.setStrategy(new BestFirst(new AStar(this)));

		goalLocation = to;

		return search(new DependencyNode(from, object, cellModel),cellModel.getGridOperations());
	}

	@Override
	public boolean isGoalState(Node n) {
		return n.getLocation().distance(goalLocation) == 1;
	}

	@Override
	public int h(Node n) 
	{
		int h = 0;
		
		h += n.getLocation().distance(goalLocation);
		
		h += ((DependencyNode) n).getDependencyCount() * 10;
		
		return h; 
	}
}
