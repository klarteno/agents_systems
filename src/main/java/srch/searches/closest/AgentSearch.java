package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Color;
import level.Location;
import srch.Node;
import srch.interfaces.Getter;
import srch.nodes.ClosestNode;

public class AgentSearch extends ClosestSearch {

	CellModel cellModel;


	private Color color;

	public AgentSearch(CellModel cellModel)
	{
		super(GridOperations.AGENT);

		this.cellModel = cellModel;
	}


	public AgentSearch(Color color) 
	{
		super(GridOperations.AGENT);
		
		this.color = color;
	}

	public  Location search(Color color, Location from)
	{/*
		this.color = color;
		return    (Location)search(new ClosestNode(from, cellModel),cellModel.getGridOperations());

*/
		return new AgentSearch(color).search(new ClosestNode(from, cellModel),cellModel.getGridOperations());
	}



	@Override
	public boolean isGoalState(Node n) 
	{
		if (!super.isGoalState(n))
		{
			return false;
		}			
		return color.equals(Getter.getModel(n).getColor(n.getLocation()));
	}

}
