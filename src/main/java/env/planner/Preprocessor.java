package env.planner;

import env.model.CellModel;
import env.model.GridOperations;
import level.Location;
import level.cell.Agent;
import level.cell.Box;
import level.cell.Goal;
import logging.LoggerFactory;
import srch.searches.DependencySearch;
import srch.searches.closest.AgentSearch;
import srch.searches.closest.BoxSearch;
import util.MapUtil;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Preprocessor {
	
	private static final Logger logger = LoggerFactory.getLogger(Preprocessor.class.getName());
	
	private  CellModel cellModel;

	public Preprocessor() {}
	
	public  List<Goal> preprocess(CellModel model)
	{		
		this.cellModel = model;

		long startTime = System.nanoTime();
		
		matchBoxesAndGoals();
		matchAgentsAndBoxes();
		List<Goal> goals = prioritizeGoals();
		matchAgentsAndGoals(goals);
		
		logger.info("Preprocessing done: " + ((System.nanoTime() - startTime) / 1000000000.0));
		
		return goals;
	}
	
	private  void matchBoxesAndGoals()
	{		
		Set<Box> availableBoxes = cellModel.getBoxes().stream()
				.filter(box -> !cellModel.isSolved(box))
				.collect(Collectors.toSet());
		
		Set<Goal> unsolvedGoals = cellModel.getGoals().stream()
				.filter(goal -> !cellModel.isSolved(goal))
				.collect(Collectors.toSet());
		
		for (Goal goal : unsolvedGoals)
		{			
			Location boxLoc = BoxSearch.search(availableBoxes, goal.getLetter(), goal.getLocation(), cellModel, cellModel.getGridOperations());
			Box box = cellModel.getBox(boxLoc);

			if (box != null && availableBoxes.remove(box))
			{
				goal.setBox(box);
				box.setGoal(goal);
			}
			else logger.warning("ERROR: matchBoxesAndGoals()");
		}
	}
	
	private  void matchAgentsAndBoxes()
	{
		Set<Box> boxes = cellModel.getGoals().stream()
							.map(goal -> goal.getBox())
							.collect(Collectors.toSet());
		
		if (cellModel.getAgents().length == 1)
		{
			Agent agent = cellModel.getAgent(0);
			boxes.stream().forEach(box -> box.setAgent(agent));
		}
		else
		{
			for (Box box : boxes)
			{			
				Location agLoc = AgentSearch.search(box.getColor(), box.getLocation(), cellModel, cellModel.getGridOperations());
				Agent agent = cellModel.getAgent(agLoc);
				
				if (agent != null)
				{
					box.setAgent(agent);
				} 
				else logger.warning("ERROR: matchAgentsAndBoxes()");
			}
		}		
	}
	
	private  void matchAgentsAndGoals(List<Goal> goals)
	{
		for (Goal goal : goals)
		{
			Agent agent = goal.getBox().getAgent();
			agent.addGoal(goal);
		}
	}

	private  List<Goal> prioritizeGoals()
	{
		Map<Goal, Set<SimpleEntry<Goal, Boolean>>> dependencies = new HashMap<>();
		for (Goal goal : cellModel.getGoals())
		{
			if (cellModel.isSolved(goal)) continue;
			
			if (!dependencies.containsKey(goal))
			{
				dependencies.put(goal, new HashSet<SimpleEntry<Goal, Boolean>>());
			}
			
			Box box 	= goal.getBox();
			Agent agent 	= box.getAgent();

	        DependencySearch.search(goal.getLocation(), box.getLocation(), GridOperations.GOAL, cellModel, cellModel.getGridOperations())
	        	.stream().forEach(loc -> addDependency(dependencies, loc, goal, true));
	        
	        DependencySearch.search(box.getLocation(), agent.getLocation(), GridOperations.BOX | GridOperations.GOAL, cellModel, cellModel.getGridOperations())
		        .stream().forEach(loc -> addDependency(dependencies, loc, goal, false));
		}
		
		return dependencies.entrySet().stream()
				.sorted(comparator)
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}
	
	private  void addDependency(Map<Goal, Set<SimpleEntry<Goal, Boolean>>> dependencies, Location l, Goal goal, boolean isGoalToBox)
	{
		SimpleEntry<Goal, Boolean> entry = new SimpleEntry<>(goal, isGoalToBox);
		
    	if (cellModel.hasObject(GridOperations.GOAL, l))
    	{
    		MapUtil.addToMap(dependencies, cellModel.getGoal(l), entry);
    	}
    	else
    	{
    		Goal otherGoal = cellModel.getBox(l).getGoal();
    		if (otherGoal != null)
    			MapUtil.addToMap(dependencies, otherGoal, entry);
    	}
	}
	
	private  Comparator<Entry<Goal, Set<SimpleEntry<Goal, Boolean>>>> comparator
		= new Comparator<Entry<Goal,Set<SimpleEntry<Goal, Boolean>>>>() {

		@Override
		public int compare(	Entry<Goal, Set<SimpleEntry<Goal, Boolean>>> o1, 
							Entry<Goal, Set<SimpleEntry<Goal, Boolean>>> o2) 
		{
			int size1 = Math.toIntExact(o1.getValue().stream().filter(SimpleEntry::getValue).count());
			int size2 = Math.toIntExact(o2.getValue().stream().filter(SimpleEntry::getValue).count());
			
			// Sort by goal to box dependency count
			if (size1 != size2)
			{
				return size1 - size2;
			}
			
			size1 = Math.toIntExact(o1.getValue().stream().filter(e -> !e.getValue()).count());
			size2 = Math.toIntExact(o2.getValue().stream().filter(e -> !e.getValue()).count());

			// Sort by agent to box dependency count
			if (size1 != size2)
			{
				return size1 - size2;
			}			
			
			Goal goal1 = o1.getKey();
			Goal goal2 = o2.getKey();
			
			Box box1 = goal1.getBox();
			Box box2 = goal2.getBox();
			
			int dist1 = box1.getLocation().distance(goal1.getLocation());
			int dist2 = box2.getLocation().distance(goal2.getLocation());
			
			// Sort by distance between box and goal
			if (dist1 != dist2)
			{
				return dist1 - dist2;
			}
			
			Agent agent1 = box1.getAgent();
			Agent agent2 = box2.getAgent();
			
			int agDist1 = agent1.getLocation().distance(box1.getLocation());
			int agDist2 = agent2.getLocation().distance(box2.getLocation());
			
			// Sort by distance between agent and box
			return agDist1 - agDist2;
		}
	};
}
