package env.model;

import level.Location;
import level.cell.*;

import java.util.HashMap;
import java.util.Map;

public class FutureModel {

	private final MyCellModel cellModel;
	private Map<Location, Location> originalLocations;

	public FutureModel(CellModel model)
	{
		cellModel = new MyCellModel(model);
		originalLocations = new HashMap<>();
	}
	
	private void addOriginalLocation(Location cell, Location location)
	{
		for (Location key : originalLocations.keySet())
		{
			if (key == cell) return;
		}
		originalLocations.put(cell, location);
	}
	
	public Map<Location, Location> getOriginalLocations()
	{
		return originalLocations;
	}

	public String toString() {
		return cellModel.toString();
	}

	public MyCellModel getCellMode(){
		return this.cellModel;
	}

	public class MyCellModel extends CellModel {
		MyCellModel(CellModel model) {
			super(model);
		}

		public void move(int obj, Location fr, Location to)
		{
			if (fr.equals(to)) return;

			switch (obj)
			{
			case GridOperations.AGENT:
				Agent agent = agentArray[fr.x][fr.y];

				agent.setLocation(to);
				addOriginalLocation(agent, fr);

				agentArray[to.x][to.y] = agent;
				agentArray[fr.x][fr.y] = null;

				break;

			case GridOperations.BOX:
				Box box = boxArray [fr.x][fr.y];

				box.setLocation(to);
				addOriginalLocation(box, fr);

				boxArray  [to.x][to.y] = box;
				boxArray  [fr.x][fr.y] = null;

				break;

			default:
			}
		}
	}
}
