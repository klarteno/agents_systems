package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Location;
import level.cell.Box;
import srch.Node;
import srch.nodes.ClosestNode;

import java.util.Set;

public class BoxSearch extends ClosestSearch {

	public static Location search(Set<Box> boxes, char letter, Location from, CellModel model,GridOperations gridOperations)
	{
		return new BoxSearch(boxes, letter, model).search(new ClosestNode(from, model),gridOperations);
	}
	
	private Set<Box> boxes;
	private char letter;
	private CellModel model;

	public BoxSearch(Set<Box> boxes, char letter, CellModel model)
	{
		super(GridOperations.BOX);
		
		this.boxes 	= boxes;
		this.letter = letter;
		this.model  = model;
	}
	
	@Override
	public boolean isGoalState(Node n)
	{
		if (!super.isGoalState(n))
		{
			return false;
		}
		
		Box box = model.getBox(n.getLocation());
		
		return box.getLetter() == letter && boxes.contains(box);
	}

}
