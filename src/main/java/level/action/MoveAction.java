package level.action;

import level.Direction;
import level.Location;

public class MoveAction extends Action {

	private Direction direction;
	
	public MoveAction(Direction direction, Location location)
	{
		super(ActionType.MOVE, location, location.newLocation(direction));
		this.direction = direction;
	}
	
	@Override
	public String toString()
	{
		return "Move(" + this.direction.toString() + ")";
	}
	
	@Override
	public Action getOpposite() {
		return new MoveAction(this.direction.getOpposite(), this.getAgentLocation().newLocation(this.direction));
	}
	
	@Override
	public boolean isOpposite(Action action) {
		return action instanceof MoveAction && Direction.isOpposite(((MoveAction) action).direction, this.direction);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
		MoveAction other = (MoveAction) obj;
		return direction == other.direction;
	}
}
