package level.action;

import level.Direction;
import level.Location;

import java.util.Objects;

public class ActionBoxMove extends Action {
    protected Direction agentDir;
    protected Direction boxDir;
    protected Location boxLocation;
    protected Location newBoxLocation;

    public ActionBoxMove(ActionType type, Location agentLocation, Location newAgentLocation) {

        super(type, agentLocation, newAgentLocation);
    }

    public ActionBoxMove(ActionType type, Direction agentDir, Direction boxDir, Location agentLocation) {

        super(type, agentLocation,  agentLocation.newLocation(agentDir));
        this.agentDir = agentDir;
        this.boxDir = boxDir;

        if(type == ActionType.Pull){
            this.boxLocation 	= getAgentLocation().newLocation(boxDir);
            this.newBoxLocation = getAgentLocation();
        }

        if(type == ActionType.Push){
            this.boxLocation	= getAgentLocation().newLocation(agentDir);
            this.newBoxLocation = getNextAgentLocation().newLocation(boxDir);
        }
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

    private Direction getAgentDir()
    {
        return agentDir;
    }

    private Direction getBoxDir()
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
            return new ActionBoxMove(ActionType.Push,this.getAgentDir().getOpposite(), this.getBoxDir(),
                    this.getAgentLocation().newLocation(this.getAgentDir()));
        }else{
            return new ActionBoxMove( ActionType.Pull,this.getAgentDir().getOpposite(), this.getBoxDir(),
                    this.getAgentLocation().newLocation(this.getAgentDir()));
        }
    }

    @Override
    public boolean isOpposite(Action action) {
        if ((action.getType() == ActionType.Push)
            || action.getType() == ActionType.Pull)
        {
            ActionBoxMove other = (ActionBoxMove) action;
            return Direction.isOpposite(this.getAgentDir(), other.getAgentDir())
                    && this.getBoxDir().equals(other.getBoxDir());
        }

        else
        {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ActionBoxMove that = (ActionBoxMove) o;
        return agentDir == that.agentDir &&
                boxDir == that.boxDir &&
                boxLocation.equals(that.boxLocation) &&
                newBoxLocation.equals(that.newBoxLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), agentDir, boxDir, boxLocation, newBoxLocation);
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
        ActionBoxMove other = (ActionBoxMove) obj;
        if (agentDir != other.agentDir)
            return false;
        return boxDir == other.boxDir;
    }

    */
}
