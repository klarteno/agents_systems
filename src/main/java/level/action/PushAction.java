package level.action;

import level.Direction;
import level.Location;

public class PushAction extends ActionBoxMove {

	public PushAction(Direction agentDir, Direction boxDir, Location agentLocation)
	{
		super(ActionType.Push, agentLocation,  agentLocation.newLocation(agentDir));

		this.agentDir 		= agentDir;
		this.boxDir 		= boxDir;
		this.boxLocation	= getAgentLocation().newLocation(agentDir);
		this.newBoxLocation = getNextAgentLocation().newLocation(boxDir);
	}
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((agentDir == null) ? 0 : agentDir.hashCode());
		result = prime * result + ((boxDir == null) ? 0 : boxDir.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PushAction other = (PushAction) obj;
		if (agentDir != other.agentDir)
			return false;
		return boxDir == other.boxDir;
	}
*/
}
