package level.cell;

public class Goal extends Lettered {
	
	private Box box;
	
	public Goal(int x, int y, char letter)
	{
		super(x, y, letter);
	}
	public Box getBox() {
		return box;
	}
	public void setBox(Box box) {
		this.box = box;
	}
	
	@Override
	public String toString() {
		return "Goal: " + super.toString();
	}

}
