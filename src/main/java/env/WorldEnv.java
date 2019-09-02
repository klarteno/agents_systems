package env;


import env.model.CellModel;
import env.model.WorldFactory;
import env.planner.Executor;
import env.planner.Planner;
import env.planner.Preprocessor;
import level.action.Action;
import level.action.SkipAction;
import level.cell.Agent;
import level.cell.Goal;
import logging.LoggerFactory;

import java.util.List;
import java.util.logging.Logger;

public class WorldEnv {

    private static final Logger logger = LoggerFactory.getLogger(WorldEnv.class.getName());
	
    //private CellModel cellModel;
    private Planner planner;
    private Executor executor;
	Preprocessor preprocessor;

    public WorldEnv()
    {
    	super();

		try 
		{
			WorldFactory worldModel = new WorldFactory();

			planner = new Planner(worldModel);
			executor = new Executor(planner);
			planner.setExecutor(executor);

			CellModel initialModel = planner.initPlan();
			preprocessor = new Preprocessor();
			List<Goal> goals = preprocessor.preprocess(initialModel);

			this.solveLevel(planner,goals);
		}
		catch (Exception e) 
		{
			logger.warning("Exception: " + e + " at init: " + e.getMessage());
		}
    }

	private void solveLevel(Planner planner, List<Goal> goals)
	{
		while (!goals.isEmpty())
		{
			for (Goal goal : goals)
			{
				planner.solveGoal(goal);
			}
			int lastStep = planner.getLastStep();
			goals = preprocessor.preprocess(planner.getModel(lastStep));
		}
	}

    public int getSolutionLength() {
    	return planner.getLastStep();
    }

    public Planner getPlanner(){
		return this.planner;
	}

	public Executor getExecutor(){
	return this.executor;
}

    public void executePlanner()
    {
    	int finalStep = getSolutionLength();
    	// Append skip actions to incomplete action lists
    	//for (Agent agent : cellModel.getAgents())
		for (Agent agent : planner.getWorldProxy().getInitialModel().getAgents())
		{
    		List<Action> actions = planner.getActions().get(agent.getNumber());
    		while (actions.size() < finalStep)
    		{
    			actions.add(new SkipAction(agent.getCopyLocation()));
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
			ServerEnv.sendJointActionToConsole(jointAction);
    	}
    }


}



