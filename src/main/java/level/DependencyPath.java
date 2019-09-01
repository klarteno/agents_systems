package level;

import env.model.GridOperations;
import env.planner.Planner;
import level.cell.Agent;
import level.cell.Box;
import srch.searches.DependencyPathSearch;

import java.util.*;
import java.util.Map.Entry;

public class DependencyPath {

	private LinkedList<Location> path;
	private Map<Location, Integer> dependencies;
	
	public DependencyPath()
	{
		path 			= new LinkedList<>();
		dependencies 	= new HashMap<>();
	}
	
	public void addToPath(Location loc)
	{
		path.addFirst(loc);
	}
	
	public void addDependency(Location loc, int step)
	{
		dependencies.put(loc, step);
	}
	
	public List<Location> getPath()
	{
		return path;
	}
	
	public boolean hasDependencies()
	{
		return !dependencies.isEmpty();
//		if (dependencies.isEmpty()) 		return false;
//		else if (dependencies.size() == 1) 	return isDependency(agent, dependencies.entrySet().stream().findAny().get());
//		else 								return true;
//		else return dependencies.entrySet().stream().anyMatch(e -> isDependency(agent, e));
	}
/*
	protected boolean isDependency(Agent agent, Entry<Location, Integer> entry)
	{
		CellModel model = Planner.getInstance().getModel(entry.getValue());
		
		if (model.hasObject(GridOperations.BOX, entry.getKey()))
		{
			Box box = model.getBox(entry.getKey());
			
			return !box.getColor().equals(agent.getColor());
		}
		return true;
	}
*/
/*
	public int countDependencies()
	{
		return dependencies.size();
	}
*/

//	public Map<Location, Integer> getDependencies()
//	{
//		return dependencies;
//	}
	
	public Entry<Location, Integer> getDependency(Location loc,Planner planner)
	{
		Optional<Entry<Location, Integer>> box = dependencies.entrySet().stream()
				.filter(e -> planner.getModel(e.getValue()).hasObject(GridOperations.BOX, e.getKey()))
				.sorted((e1, e2) -> e1.getKey().distance(loc) - e2.getKey().distance(loc))
				.min((e1, e2) -> e1.getValue() - e2.getValue());

		return box.orElseGet(() -> dependencies.entrySet().stream()
				.sorted((e1, e2) -> e1.getKey().distance(loc) - e2.getKey().distance(loc))
				.min((e1, e2) -> e1.getValue() - e2.getValue()).get());
	}
	
	/**
	 * Get the dependency path between the locations with a box.
	 * @param agent
	 * @param box
	 * @param initialStep
	 * @return
	 */
	public  DependencyPath getDependencyPath(Agent agent, Box box, int initialStep,GridOperations gridOperations)
	{
		return (DependencyPath)new DependencyPathSearch(box.getCopyLocation()).
				getDependencyPath(agent, agent.getCopyLocation(), box.getCopyLocation(), true, initialStep, gridOperations);

	}
	
	/**
	 * Get the dependency path between the locations.
	 * @param agent
	 * @param to
	 * @return
	 */
	public DependencyPath getDependencyPath(Agent agent, Location tracked, Location to, int initialStep, GridOperations gridOperations)
	{
		return (DependencyPath)new DependencyPathSearch(to).
				getDependencyPath(agent, tracked.getCopyLocation(),  to, false, initialStep, gridOperations);
	}
}
