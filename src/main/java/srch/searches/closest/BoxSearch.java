package srch.searches.closest;

import env.model.CellModel;
import env.model.GridOperations;
import level.Location;
import level.cell.Box;
import srch.Node;
import srch.Search;
import srch.Strategy;
import srch.nodes.ClosestNode;

import java.util.Set;

public class BoxSearch extends Search {

	public Location search(Set<Box> boxes, char letter, Location from)
	{
		return new BoxSearch(boxes, letter, cellModel).search(new ClosestNode(from, cellModel.getGridOperations()),cellModel.getGridOperations());
	}

	private int objectType;
	private Set<Box> boxes;
	private char letter;
	private CellModel cellModel;

	public BoxSearch(CellModel model)
	{
		this.objectType = GridOperations.BOX;
		this.setStrategy(new Strategy.BFS());
		this.cellModel  = model;
	}

	private BoxSearch(Set<Box> boxes, char letter, CellModel model)
	{
		this.objectType = GridOperations.BOX;
		this.setStrategy(new Strategy.BFS());
		
		this.boxes 	= boxes;
		this.letter = letter;
		this.cellModel  = model;
	}
	
	@Override
	public boolean isGoalState(Node n)
	{
		if (!((ClosestNode) n).getModel().hasObject(objectType, n.getLocation()))
		{
			return false;
		}
		Box box = cellModel.getBox(n.getLocation());
		
		return box.getLetter() == letter && boxes.contains(box);
	}

}
