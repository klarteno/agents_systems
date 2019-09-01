package level.cell;

import level.Color;

public class Box extends Colored {
	
	private Agent agent;
	private Goal goal;

	public Box(int x, int y, char letter, Color color) {
		super(x, y, letter, color);
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public Goal getGoal() {
		return goal;
	}
	
	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	@Override
	public String toString() {
		return "Box: " + super.toString();
	}
}
