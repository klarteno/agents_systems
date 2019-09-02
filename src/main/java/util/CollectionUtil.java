package util;

import level.cell.Agent;
import level.cell.Box;
import level.cell.Goal;

import java.util.Arrays;

public class CollectionUtil {
	
	private CollectionUtil() {}

	public static int getAgentNumber(Agent agent) {
		return ((int) agent.getLetter()) << 24;
	}
/*
	public static int getAgentNumber(Location loc, GridOperations model)
	{
		if (model.hasObject(GridOperations.AGENT, loc))
		{
			return model.getMasked(GridOperations.BOX_MASK, loc) >> 24;
		}
		return 0;
	}
	*/

	public static int[][] clone(int[][] data)
	{
		return Arrays.stream(data).map(int[]::clone).toArray(int[][]::new);
	}

	public static Agent[][] clone(Agent[][] data)
	{
		return Arrays.stream(data).map(Agent[]::clone).toArray(Agent[][]::new);
	}

	public static Goal[][] clone(Goal[][] data)
	{
	    return Arrays.stream(data).map(Goal[]::clone).toArray(Goal[][]::new);
	}
	
	public static Box[][] clone(Box[][] data)
	{
		return Arrays.stream(data).map(Box[]::clone).toArray(Box[][]::new);
	}
}
