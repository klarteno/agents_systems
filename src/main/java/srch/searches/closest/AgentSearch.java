package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Color;
import level.Location;
import srch.Node;
import srch.interfaces.Getter;
import srch.nodes.ClosestNode;

public class AgentSearch extends ClosestSearch {

	public static Location search(Color color, Location from, CellModel model,GridOperations gridOperations)
	{
		return new AgentSearch(color).search(new ClosestNode(from, model),gridOperations);
	}
	
	private Color color;

	public AgentSearch(Color color) 
	{
		super(GridOperations.AGENT);
		
		this.color = color;
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
