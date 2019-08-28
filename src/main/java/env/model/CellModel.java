package env.model;

import level.Location;
import level.cell.*;
import logging.LoggerFactory;
import util.ModelUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class CellModel extends ActionModel {

	protected static final Logger logger = LoggerFactory.getLogger(CellModel.class.getName());
	
	Agent[]					agents;
	Set<Goal>					goals;
	Set<Box>					boxes;

	Agent[][]					agentArray;
	Goal[][]					goalArray;
	Box[][]					boxArray;
	
	public CellModel(int width, int height, int nbAgs)
	{
		super(width, height);
		
		agents 		= new Agent[nbAgs];
		goals		= new HashSet<>();
		boxes		= new HashSet<>();
		
		agentArray 	= new Agent[width][height];
		goalArray 	= new Goal [width][height];
		boxArray  	= new Box[width][height];
	}
	
	public CellModel(CellModel model)
	{
		super(model.gridOperations);
		
		agents 		= model.agents.clone();
		goals		= new HashSet<>(model.goals);
		boxes		= new HashSet<>(model.boxes);
		
		agentArray 	= ModelUtil.deepCopyAgents(model.agentArray);
		goalArray 	= ModelUtil.deepCopyGoals(model.goalArray);
		boxArray 	= ModelUtil.deepCopyBoxes(model.boxArray);
	}
    
    public int getNbAgs() {
    	return agents.length;
    }
	
	public Agent getAgent(int i) {
		return agents[i];
	}

	public Agent[] getAgents() {
		return agents;
	}
	
	public Set<Goal> getGoals() {
		return goals;
	}
	
	public Set<Box> getBoxes() {
		return boxes;
	}
	
	public Agent getAgent(Location l) {
		return agentArray[l.x][l.y];
	}
	
	public Goal getGoal(Location l) {
		return goalArray[l.x][l.y];
	}
	
	public Box getBox(Location l) {
		return boxArray[l.x][l.y];
	}

	public boolean isSolved(Goal goal) {
		return goal.getBox() != null && goal.getLocation().equals(goal.getBox().getLocation());
	}
	
	public boolean isSolved(Box box) {
		return box.getGoal() != null && box.getLocation().equals(box.getGoal().getLocation());
	}
	

	public void move(int obj, Location fr, Location to)
	{
		if (fr.equals(to)) return;
		
		switch (obj)
		{
		case GridOperations.AGENT:
			agentArray[fr.x][fr.y].setLocation(to); 
			agentArray[to.x][to.y] = agentArray[fr.x][fr.y];
			agentArray[fr.x][fr.y] = null;
			break;
		case GridOperations.BOX:
			boxArray  [fr.x][fr.y].setLocation(to); 
			boxArray  [to.x][to.y] = boxArray[fr.x][fr.y];
			boxArray  [fr.x][fr.y] = null;
			break;
		default: 	return;
		}

		super.move(obj, fr, to);
	}
	
	public Cell removeCell(int obj, Location l)
	{
		Cell cell = null;
		
		switch (obj)
		{
		case GridOperations.AGENT:
			cell = agentArray[l.x][l.y];
			agentArray[l.x][l.y] = null;
			break;
		case GridOperations.BOX:
			cell = boxArray[l.x][l.y];
			boxArray  [l.x][l.y] = null;
			break;
		default: return null;
		}
		gridOperations.remove(obj, l);

		return cell;
	}
	
	public void addCell(Colored data, Cell object)
	{		
		Location loc = data.getLocation();
		
		object.setLocation(loc);
		
		int type = data instanceof Agent ? GridOperations.AGENT : GridOperations.BOX;
		
		if (data instanceof Agent)
		{
			agentArray[loc.x][loc.y] = (Agent) object;
		}
		else if (data instanceof Box)
		{
			boxArray[loc.x][loc.y] = (Box) object;
		}

		gridOperations.add(type, loc);
		gridOperations.addLetter(data.getLetter(), type, loc);
		gridOperations.addColor(data.getColor(), loc);
	}
}
