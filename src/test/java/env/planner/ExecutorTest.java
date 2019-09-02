package env.planner;

import env.WorldEnv;
import env.model.CellModel;
import env.model.FutureModel;
import env.model.WorldFactory;
import level.Color;
import level.Direction;
import level.Location;
import level.action.Action;
import level.action.MoveAction;
import level.cell.Agent;
import level.cell.Box;
import level.cell.Goal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest {

    @Test
    @DisplayName("WorldEnv testing")
    void testExecutions() {
        WorldFactory worldModel = new WorldFactory();

        Planner planner = new Planner(worldModel);
        Executor executor = new Executor(planner);
        planner.setExecutor(executor);

        CellModel initialModel = planner.initPlan();
        Preprocessor preprocessor = new Preprocessor();
        List<Goal> goals = preprocessor.preprocess(initialModel);

        boolean planGoal = planner.solveGoal(goals.get(0));
        Location location = new Location(11,5);

        LinkedList<Goal> goalAgent=  new LinkedList<>();
        Box box = new Box(9, 3, 'b', Color.Green);
        Goal goal = new Goal(1, 12, 'b');
        goal.setBox(box);
        goalAgent.add(goal);
        Agent agent = new Agent(location, '1' , Color.Green,  goalAgent);

        int startStep = 0;
        List<Action> actions = new LinkedList<>();
        actions.add(new MoveAction(Direction.WEST,new Location(11,5)));
        actions.add(new MoveAction(Direction.WEST,new Location(10,5)));
        actions.add(new MoveAction(Direction.WEST,new Location(9,5)));


        int initialStep = 0;
        CellModel cellModel = planner.getModel(initialStep);
        FutureModel futureModel = new FutureModel(cellModel);

        //executor.executeActions(agent,initialStep,actions);
/*
        assertNotEquals(location,agent.);
        assert agent location different
        assertEquals();

*/
        /*
        result = {LinkedList@1183}  size = 3
        0 = {MoveAction@1197} "Move(W)"
        direction = {Direction@1201} "W"
        type = {Action$ActionType@1202} "Move"
        agentLocation = {Location@1203} "(11, 5)"
        newAgentLocation = {Location@1204} "(10, 5)"
        1 = {MoveAction@1209} "Move(W)"
        direction = {Direction@1201} "W"
        type = {Action$ActionType@1202} "Move"
        agentLocation = {Location@1204} "(10, 5)"
        newAgentLocation = {Location@1234} "(9, 5)"
        2 = {MoveAction@1194} "Move(W)"
        direction = {Direction@1201} "W"
        type = {Action$ActionType@1202} "Move"
        agentLocation = {Location@1234} "(9, 5)"
        newAgentLocation = {Location@1237} "(8, 5)"
        */
        /*
        WorldEnv env = new WorldEnv();

        Planner planner = env.getPlanner();
        assertNotNull(planner);
        Executor executor = env.getExecutor();
        assertNotNull(executor);
        */

        //goals = planner.initPlan();
        //env.solveLevel(goals);

       // assertTrue(planner.equals(env));
        //assertNotNull(null);
        //env.executePlanner();
    }
}
