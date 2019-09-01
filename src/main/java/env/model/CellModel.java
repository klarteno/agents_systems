package env.model;

import level.Location;
import level.cell.*;
import logging.LoggerFactory;
import util.ModelUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
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
		return goal.getBox() != null && goal.getCopyLocation().equals(goal.getBox().getCopyLocation());
	}
	
	public boolean isSolved(Box box) {
		return box.getGoal() != null && box.getCopyLocation().equals(box.getGoal().getCopyLocation());
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

	public Location removeCell(int obj, Location loc)
	{
		Location cell = null;
		
		switch (obj)
		{
		case GridOperations.AGENT:
			cell = agentArray[loc.x][loc.y];
			agentArray[loc.x][loc.y] = null;
			break;
		case GridOperations.BOX:
			cell = boxArray[loc.x][loc.y];
			boxArray  [loc.x][loc.y] = null;
			break;
		default: return null;
		}
		gridOperations.remove(obj, loc);

		return cell;
	}
	
	public void addCell(Colored data, Location cell)
	{		
		Location loc = data.getCopyLocation();
		cell.setLocation(loc);
		int type = data instanceof Agent ? GridOperations.AGENT : GridOperations.BOX;
		
		if (data instanceof Agent)
		{
			agentArray[loc.x][loc.y] = (Agent) cell;
		}
		else if (data instanceof Box)
		{
			boxArray[loc.x][loc.y] = (Box) cell;
		}

		gridOperations.add(type, loc);
		gridOperations.addLetter(data.getLetter(), type, loc);
		gridOperations.addColor(data.getColor(), loc);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(super.hashCode(), goals, boxes);
		result = 31 * result + Arrays.hashCode(agents);
		result = 31 * result + Arrays.hashCode(agentArray);
		result = 31 * result + Arrays.hashCode(goalArray);
		result = 31 * result + Arrays.hashCode(boxArray);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CellModel cellModel = (CellModel) o;
		return Arrays.equals(agents, cellModel.agents) &&
				goals.equals(cellModel.goals) &&
				boxes.equals(cellModel.boxes) &&
				Arrays.equals(agentArray, cellModel.agentArray) &&
				Arrays.equals(goalArray, cellModel.goalArray) &&
				Arrays.equals(boxArray, cellModel.boxArray);
	}

}
