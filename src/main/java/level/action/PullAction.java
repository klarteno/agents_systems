package level.action;

import level.Direction;
import level.Location;

public class PullAction extends ActionBoxMove {

	public PullAction(Direction agentDir, Direction boxDir, Location agentLocation)
	{
		super(ActionType.Pull, agentLocation,  agentLocation.newLocation(agentDir));

		this.agentDir = agentDir;
		this.boxDir = boxDir;
		this.boxLocation 	= getAgentLocation().newLocation(boxDir);
		this.newBoxLocation = getAgentLocation();
	}
}
