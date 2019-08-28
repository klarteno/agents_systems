package env;


import com.fasterxml.jackson.databind.ObjectMapper;
import env.model.CellModel;
import env.model.WorldModel;
import env.planner.Planner;
import level.action.Action;
import level.action.SkipAction;
import level.cell.Agent;
import logging.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class WorldEnv extends ServerEnv {

    private static final Logger logger = LoggerFactory.getLogger(WorldEnv.class.getName());
	
    private CellModel model;    
    private static WorldEnv instance;
    private Planner planner;
    
    public WorldEnv()
    {
    	super();
    	
		instance = this;

		try 
		{
			//Level resultParsed = Level.parse(serverIn);
			ObjectMapper objectMapper = new ObjectMapper();

			//InputGridData inputGridData = new InputGridData(resultParsed.height,resultParsed.width,resultParsed.nbAgs,resultParsed.data,resultParsed.colors);
			//WorldEnv.writeGridObject(inputGridData);
			InputGridData inputGridData= WorldEnv.readGridObject(Optional.of("MAYSoSirius.tmp"));

			model = new WorldModel(inputGridData);
			
			planner = new Planner();
			
			planner.plan();
		} 
		catch (Exception e) 
		{
			logger.warning("Exception: " + e + " at init: " + e.getMessage());
		}
    }
    
    public int getSolutionLength() {
    	return planner.getLastStep();
    }
  
    public void executePlanner()
    {
    	int finalStep = getSolutionLength();
    	
    	// Append skip actions to incomplete action lists
    	for (Agent agent : model.getAgents())
    	{
    		List<Action> actions = planner.getActions().get(agent.getNumber());
    		
    		while (actions.size() < finalStep)
    		{
    			actions.add(new SkipAction(agent.getLocation()));
    		}
    	}
    	
    	List<List<Action>> actions = planner.getActions();
    	
		String[] jointAction = new String[actions.size()];
    	
    	// Send joint action to server for each step
    	for (int step = 0; step < finalStep; step++)
    	{    		
    		for (int agNumber = 0; agNumber < actions.size(); agNumber++)
    		{
    			jointAction[agNumber] = actions.get(agNumber).get(step).toString();
    		}
			sendJointActionToConsole(jointAction);
    	}
    }
    
    public static WorldEnv getInstance() {
    	return instance;
    }



	public static void writeGridObject(InputGridData inputGridData,Optional<String> file) throws IOException {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;

		String fileName = file.isPresent() ? file.get() : "grid_data.tmp";

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

	public static InputGridData readGridObject(Optional<String> file) throws IOException, ClassNotFoundException {
		String fileName = file.isPresent() ? file.get() : "grid_data.tmp";

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



