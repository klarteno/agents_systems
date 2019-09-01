package srch;

import env.model.GridOperations;
import level.Location;

import java.util.List;
import java.util.Objects;

public abstract class Node {
	
	private Node parent;
	private Location location;
	private int object;
	private int g;
	
	public Node(Location initial)
	{
		this(initial, 0);
	}
	
	public Node(Location initial, int object)
	{
		this(initial, object, 0);
	}
	
	public Node(Location initial, int object, int include)
	{
		this.parent 	= null;
		this.location 	= initial;
		this.object		= object | GridOperations.WALL;
		this.g 			= 0;
	}
	
	public Node(Node parent, Location location)
	{
		this.parent    	= parent;
		this.location  	= location;
		this.object	   	= parent.object;
		this.g         	= parent.g + 1;
	}
	
	public Node getParent() {
		return this.parent;
	}
	public Location getLocation() {
		return this.location;
	}
	
	/**
	 * @return Objects to exclude
	 */
	public int getObject() 
	{
		return this.object;
	}

	public int getG() {
		return this.g;
	}

	public abstract List<? extends Node> getExpandedNodes(GridOperations gridOperations);

	public abstract <T> T extractPlan();

	@Override
	public String toString() {
		return this.getLocation().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return object == node.object &&
				location.equals(node.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, object);
	}
}
