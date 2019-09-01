package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Color;
import level.Location;
import srch.Node;
import srch.Search;
import srch.Strategy;
import srch.nodes.ClosestNode;

public class AgentSearch extends Search {

	private CellModel cellModel;
	private int objectType;
	private Color color;

	public AgentSearch(CellModel cellModel)
	{
		this.objectType = GridOperations.AGENT;
		this.setStrategy(new Strategy.BFS());
		this.cellModel = cellModel;
	}

	private AgentSearch(Color color)
	{
		this.objectType = GridOperations.AGENT;
		this.setStrategy(new Strategy.BFS());
		this.color = color;
	}

	public  Location search(Color color, Location from)
	{
		return new AgentSearch(color).search(new ClosestNode(from, cellModel.getGridOperations()),cellModel.getGridOperations());
	}

	@Override
	public boolean isGoalState(Node n) 
	{
		GridOperations model = 	((ClosestNode) n).getModel();

	   if (!model.hasObject(objectType, n.getLocation()))
		{
			return false;
		}			
		return color.equals(model.getColor(n.getLocation()));
	}

}
