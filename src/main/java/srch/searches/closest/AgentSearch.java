package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Color;
import level.Location;
import srch.Node;
import srch.Search;
import srch.Strategy;
import srch.interfaces.Getter;
import srch.interfaces.IModelNode;
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

	public AgentSearch(Color color) 
	{
		this.objectType = GridOperations.AGENT;
		this.setStrategy(new Strategy.BFS());
		this.color = color;
	}

	public  Location search(Color color, Location from)
	{/*
		this.color = color;
		return    (Location)search(new ClosestNode(from, cellModel),cellModel.getGridOperations());

*/
		return new AgentSearch(color).search(new ClosestNode(from, cellModel.getGridOperations()),cellModel.getGridOperations());
	}

	@Override
	public boolean isGoalState(Node n) 
	{
		GridOperations model = Getter.getModel(n);

	   if (!model.hasObject(objectType, n.getLocation()))
		{
			return false;
		}			
		return color.equals(model.getColor(n.getLocation()));
	}

}
