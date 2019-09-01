package level.action;

import level.Direction;
import level.Location;

import java.util.Objects;

public class MoveAction extends Action {

	private Direction direction;
	
	public MoveAction(Direction direction, Location location)
	{
		super(ActionType.Move, location, location.newLocation(direction));
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
		return action.getType() == ActionType.Move
				&& Direction.isOpposite(((MoveAction) action).direction, this.direction);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		MoveAction that = (MoveAction) o;
		return direction == that.direction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), direction);
	}
/*
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

	*/
}
