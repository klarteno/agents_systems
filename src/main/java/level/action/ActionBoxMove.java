package level.action;

import level.Direction;
import level.Location;

public class ActionBoxMove extends Action {
    protected Direction agentDir;
    protected Direction boxDir;
    protected Location boxLocation;
    protected Location newBoxLocation;

    public ActionBoxMove(ActionType type, Location agentLocation, Location newAgentLocation) {

        super(type, agentLocation, newAgentLocation);
    }
/*
    public ActionBoxMove(ActionType type, Location agentLocation, Location newAgentLocation, Direction agentDir, Direction boxDir) {

        super(type, agentLocation, newAgentLocation);

        this.agentDir = agentDir;
        this.boxDir = boxDir;
        this.boxLocation 	= getAgentLocation().newLocation(boxDir);
        this.newBoxLocation = getAgentLocation();
    }
*/

    protected Direction getAgentDir()
    {
        return agentDir;
    }

    protected Direction getBoxDir()
    {
        return boxDir;
    }

    public Location getBoxLocation()
    {
        return boxLocation;
    }

    public Location getNewBoxLocation()
    {
        return newBoxLocation;
    }

    @Override
    public String toString()
    {
        return  super.getType().name() + "(" + this.agentDir.toString() + ","
                + this.boxDir.toString() + ")";
    }

    @Override
    public Action getOpposite()
    {
        if (super.getType() == ActionType.Pull ){
            return new PushAction(this.getAgentDir().getOpposite(), this.getBoxDir(),
                    this.getAgentLocation().newLocation(this.getAgentDir()));
        }else{
            return new PullAction(this.getAgentDir().getOpposite(), this.getBoxDir(),
                    this.getAgentLocation().newLocation(this.getAgentDir()));
        }
    }

    @Override
    public boolean isOpposite(Action action) {
        if (action.getType() == ActionType.Push)
        {
            PushAction other = (PushAction) action;
            return Direction.isOpposite(this.getAgentDir(), other.getAgentDir())
                    && this.getBoxDir().equals(other.getBoxDir());
        }

        if (action.getType() == ActionType.Pull)
        {
            PullAction other = (PullAction) action;
            return Direction.isOpposite(this.getAgentDir(), other.getAgentDir())
                    && this.getBoxDir().equals(other.getBoxDir());
        }

        else
        {
            return false;
        }
    }

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
        ActionBoxMove other = (ActionBoxMove) obj;
        if (agentDir != other.agentDir)
            return false;
        return boxDir == other.boxDir;
    }
}
