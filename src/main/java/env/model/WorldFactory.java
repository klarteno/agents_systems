package env.model;

import env.InputGridData;
import env.ServerEnv;
import level.Level;
import level.Color;
import level.Location;
import level.cell.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class WorldFactory {
	private CellModel initialModel = null;
	private Map<Character, Set<Goal>> 	goalMap;
	private Map<Character, Set<Box>>  	boxMap;
	
	private int freeCells;

	public WorldFactory()
	{
		Optional<InputGridData> inputGridData = Optional.empty();

		try {
			inputGridData = Optional.ofNullable(
					SerializationEnvironment.readGridObject(Optional.of("MAYSoSirius.tmp")));
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

/*
		try {
			inputGridData = Optional.of(ServerEnvironmaent.getGrid());
		} catch (IOException e) {
			e.printStackTrace();
		}

*/
		if(inputGridData.isPresent()){
			this.initialModel = new CellModel(inputGridData.get().width, inputGridData.get().height, inputGridData.get().nbAgs);
		}else{
			System.err.println("data serialization reading failed");
		}

		assert initialModel != null;
		initialModel.setGoalLocations(initialModel.getGoals().stream().map(Location::getCopyLocation)
				.collect(Collectors.toSet()));
		
		goalMap 	= new HashMap<>();
		boxMap		= new HashMap<>();

		initData(inputGridData.get().data, inputGridData.get().colors);
	}

	private static class SerializationEnvironment{
		private static void writeGridObject(InputGridData inputGridData,Optional<String> file) throws IOException {
			FileOutputStream fileOutputStream = null;
			ObjectOutputStream objectOutputStream = null;

			String fileName = file.orElse("grid_data.tmp");

			try
			{
				fileOutputStream = new FileOutputStream(fileName);
				objectOutputStream = new ObjectOutputStream(fileOutputStream);

				/*
				 * Write the specified object to the
				 * ObjectOutputStream.
				 */
				objectOutputStream.writeObject(inputGridData);
				//System.out.println("Successfully written list of employee objects to the file.\n");
			}
			finally
			{
				if (objectOutputStream != null)
				{
					/*
					 * Closing a ObjectOutputStream will also
					 * close the OutputStream instance to which
					 * the ObjectOutputStream is writing.
					 */
					objectOutputStream.close();
				}
			}

		}
		private static InputGridData readGridObject(Optional<String> file) throws IOException, ClassNotFoundException {
			String fileName = file.orElse("grid_data.tmp");

			FileInputStream fileInputStream = null;
			ObjectInputStream objectInputStream = null;

			InputGridData  inputGridData;

			try
			{
				fileInputStream = new FileInputStream(fileName);
				objectInputStream = new ObjectInputStream(fileInputStream);

				/*
				 * Read an object from the ObjectInputStream.
				 */
				inputGridData = (InputGridData) objectInputStream.readObject();
				//System.out.println("Successfully read list of employee objects from the file.\n");

				//System.out.println("inputGridData from file = " +inputGridData.toString());
			}
			finally
			{
				if (objectInputStream != null)
				{
					/*
					 * Closing a ObjectInputStream will also
					 * close the InputStream instance from which
					 * the ObjectInputStream is reading.
					 */
					objectInputStream.close();
				}
			}
			return inputGridData;
		}
	}

	static class ServerEnvironmaent{
		public static InputGridData getGrid() throws IOException {
			Level resultParsed = Level.parse(new ServerEnv().serverIn);
			//ObjectMapper objectMapper = new ObjectMapper();

			return new InputGridData(resultParsed.height, resultParsed.width, resultParsed.nbAgs, resultParsed.data, resultParsed.colors);
			//SerializationEnvironment.writeGridObject(inputGridData, Optional.of("file_map"));
		}
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
		for (int x = 0; x < initialModel.getGridOperations().getWidth(); x++)
		{
			for (int y = 0; y < initialModel.getGridOperations().getHeight(); y++)
			{
				char ch = data[x][y];
				if (Character.isDigit(ch)) addAgent(x, y, ch, Color.getColor(colors.get(ch)));
				else if (Character.isLowerCase(ch)) addGoal(x, y, ch);
				else if (Character.isUpperCase(ch)) addBox(x, y, ch, Color.getColor(colors.get(ch)));
				else if (ch == '+') initialModel.gridOperations.add(GridOperations.WALL, x, y);
				else freeCells++;
			}
		}
	}
	
	private void addAgent(int x, int y, char letter, Color color)
	{
		Agent agent = new Agent(x, y, letter, color);
		int number = agent.getNumber();

		initialModel.gridOperations.add(GridOperations.AGENT, x, y);
		initialModel.gridOperations.addLetter(letter, GridOperations.BOX, x, y);
		initialModel.gridOperations.addColor(color, x, y);
		initialModel.agents[number] = agent;
		initialModel.agentArray[x][y] = agent;
	}
	
	private void addGoal(int x, int y, char letter)
	{
		Goal goal = new Goal(x, y, letter);

		initialModel.gridOperations.add(GridOperations.GOAL, x, y);
		initialModel.gridOperations.addLetter(letter, GridOperations.GOAL, x, y);
		initialModel.goals.add(goal);
		initialModel.goalArray[x][y] = goal;
		addToMap(goalMap, letter, goal);
	}
	
	private void addBox(int x, int y, char upperCaseLetter, Color color)
	{
		char letter = Character.toLowerCase(upperCaseLetter);
		
		Box box = new Box(x, y, letter, color);

		initialModel.gridOperations.add(GridOperations.BOX, x, y);
		initialModel.gridOperations.addLetter(letter, GridOperations.BOX, x, y);
		initialModel.gridOperations.addColor(color, x, y);
		initialModel.boxes.add(box);
		initialModel.boxArray[x][y] = box;
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

	public CellModel getInitialModel() {
		return initialModel;
	}

	public String toString() {
		return initialModel.toString();
	}

	public int hashCode() {
		return initialModel.hashCode();
	}

}
