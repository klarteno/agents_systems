package env.model;

import env.InputGridData;
import level.Color;
import level.cell.*;

import java.util.*;

public class WorldFactory {

	private final CellModel cellModel;
	private Map<Character, Set<Goal>> 	goalMap;
	private Map<Character, Set<Box>>  	boxMap;
	
	private int freeCells;
	
	private static WorldFactory instance;

	/**
	 * Constructs a new WorldModel based on a level.
	 * @param level - InputGridData object
	 */
	public WorldFactory(InputGridData level, CellModel cellModel)
	{		
		this.cellModel = cellModel;
		
		goalMap 	= new HashMap<>();
		boxMap		= new HashMap<>();
		
		initData(level.data, level.colors);

		instance = this;
	}

	public static WorldFactory getInstance() {
		return instance;
	}

	public int getFreeCellCount() {
		return freeCells;
	}

	/**
	 * Initializes the grid with objects according to the 
	 * data.
	 * @param data - Two-dimensional char array
	 */
	private void initData(char[][] data, Map<Character, String> colors) 
	{		
		for (int x = 0; x < cellModel.getGridOperations().width; x++)
		{
			for (int y = 0; y < cellModel.getGridOperations().height; y++)
			{
				char ch = data[x][y];
				
				if (Character.isDigit(ch)) addAgent(x, y, ch, Color.getColor(colors.get(ch)));
				
				else if (Character.isLowerCase(ch)) addGoal(x, y, ch);
				
				else if (Character.isUpperCase(ch)) addBox(x, y, ch, Color.getColor(colors.get(ch)));
				
				else if (ch == '+') cellModel.gridOperations.add(GridOperations.WALL, x, y);
				
				else freeCells++;
			}
		}
	}
	
	private void addAgent(int x, int y, char letter, Color color)
	{
		Agent agent = new Agent(x, y, letter, color);
		int number = agent.getNumber();

		cellModel.gridOperations.add(GridOperations.AGENT, x, y);
		cellModel.gridOperations.addLetter(letter, GridOperations.BOX, x, y);
		cellModel.gridOperations.addColor(color, x, y);
		cellModel.agents[number] = agent;
		cellModel.agentArray[x][y] = agent;
	}
	
	private void addGoal(int x, int y, char letter)
	{
		Goal goal = new Goal(x, y, letter);

		cellModel.gridOperations.add(GridOperations.GOAL, x, y);
		cellModel.gridOperations.addLetter(letter, GridOperations.GOAL, x, y);
		cellModel.goals.add(goal);
		cellModel.goalArray[x][y] = goal;
		addToMap(goalMap, letter, goal);
	}
	
	private void addBox(int x, int y, char upperCaseLetter, Color color)
	{
		char letter = Character.toLowerCase(upperCaseLetter);
		
		Box box = new Box(x, y, letter, color);

		cellModel.gridOperations.add(GridOperations.BOX, x, y);
		cellModel.gridOperations.addLetter(letter, GridOperations.BOX, x, y);
		cellModel.gridOperations.addColor(color, x, y);
		cellModel.boxes.add(box);
		cellModel.boxArray[x][y] = box;
		addToMap(boxMap, letter, box);
	}

	private static <T> void addToMap(Map<Character, Set<T>> map, char letter, T object)
	{
		if (map.containsKey(letter))
		{
			map.get(letter).add(object);
		}
		else
		{
			map.put(letter, new HashSet<T>(Arrays.asList(object)));
		}
	}

	public CellModel getCellModel() {
		return cellModel;
	}

	public String toString() {
		return cellModel.toString();
	}

	public int hashCode() {
		return cellModel.hashCode();
	}

}
