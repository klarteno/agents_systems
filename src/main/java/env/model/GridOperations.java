package env.model;

import level.Color;
import level.Direction;
import level.Location;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class GridOperations {
	
	protected static final Logger logger = Logger.getLogger(WorldFactory.class.getName());
	
    public static final int 	AGENT   =  1;
	public static final int		GOAL 	=  2;
	public static final int		BOX		=  4;
    public static final int 	WALL 	=  8;
    public static final int 	LOCKED 	= 16;
    public static final int 	IN_USE	= 32;

    public static final int 	TYPE_MASK	= 0xFF;
    public static final int 	COLOR_MASK	= 0xFF00;
    public static final int		GOAL_MASK 	= 0xFF0000;
    public static final int		BOX_MASK 	= 0xFF000000;
	
	private  int	width = 0,
					height= 0;
    private final int[][] 	data;

	public GridOperations(int width, int height)
	{
		this.width 	= width;
		this.height	= height;		
		this.data 	= new int[width][height];
	}

	public GridOperations(int[][] data)
	{
		this.width 	= data.length;
		this.height	= data[0].length;
		this.data = util.CollectionUtil.clone(data);
	}
	
	public GridOperations(GridOperations model)
	{		
		this.data 	= model.cloneData();
		this.width 	= data.length;
		this.height = data[0].length;
	}

	public int getWidth(){
		return this.width;
	}

	public int getHeight(){
		return this.height;
	}

	public boolean isSolved(Location l) {
		return isSolved(l.x, l.y);
	}
	
	private boolean isSolved(int x, int y) {
		return hasObject(GOAL, x, y) && (data[x][y] & GOAL_MASK) == ((data[x][y] & BOX_MASK) >> 8);
	}

	public boolean isNextCellFree(Location l)
	{
		for (Direction dir : Direction.EVERY)
		{
			if (isFree(l.newLocation(dir))) return true;
		}
		return false;
	}

	public int isFreeAdjacent(int agNumber, Location l)
	{
		int count = 0;
		
		for (Direction dir : Direction.EVERY)
		{
			Location newLoc = l.newLocation(dir);
			
			if (isFree(newLoc) || hasObject(agNumber, GridOperations.BOX_MASK, newLoc))
			{
				count++;
			}
		}
		
		return count;
	}
    
    private boolean inGrid(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

	public boolean containsInGrid(int x, int y,int obj ) {
		return (data[x][y]^obj) == 0 ;
	}

	public void addToGrid(int x, int y,int obj ) {
		 data[x][y] = obj;
	}

    public boolean hasObject(int obj, Location l) {
        return hasObject(obj, l.x, l.y);
    }
    
    public boolean hasObject(int obj, int x, int y) {
        return inGrid(x, y) && (data[x][y] & obj) != 0;
    }
    
    public boolean hasObject(int obj, int mask, Location l) {
    	return hasObject(obj, mask, l.x, l.y);
    }
    
    public boolean hasObject(int obj, int mask, int x, int y) {
    	return inGrid(x, y) && getMasked(mask, x, y) == obj;
    }

    public boolean isFree(Location l) {
        return isFree(l.x, l.y);
    }

    private boolean isFree(int x, int y) {
		return inGrid(x, y) && (data[x][y] & (WALL | AGENT | BOX)) == 0;
    }

    public boolean isFree(int obj, Location l) {
        return isFree(obj, l.x, l.y);
    }
    
    private boolean isFree(int obj, int x, int y) {
    	return inGrid(x, y) && (data[x][y] & obj) == 0;
    }
    
    public boolean isFree(int obj, int mask, Location l) {
    	return isFree(obj, mask, l.x, l.y);
    }
    
    private boolean isFree(int obj, int mask, int x, int y) {
    	return inGrid(x, y) && getMasked(mask, x, y) != obj;
    }
    
    public void add(int obj, Location l) {
        add(obj, l.x, l.y);
    }

    public void add(int obj, int x, int y) {
        data[x][y] |= obj;
    }

    public void remove(int obj, Location l) {
        remove(obj, l.x, l.y);
    }

    private void remove(int obj, int x, int y)
    {
    	if ((obj & AGENT) != 0 || (obj & BOX) != 0)
    	{
    		obj |= (BOX_MASK | COLOR_MASK);
    	}
        data[x][y] &= ~obj;
    }
   
    public void addColor(Color color, Location l) {
    	addColor(color, l.x, l.y);
    }
    
    public void addColor(Color color, int x, int y) {
    	add(Color.getValue(color) << 8, x , y);
    }
    
    public Color getColor(Location l) {
    	return getColor(l.x, l.y);
    }
    
    public Color getColor(int x, int y) {
    	if (!hasObject(AGENT, x, y) && !hasObject(BOX, x, y)) return null;
    	return Color.getColor((data[x][y] & COLOR_MASK) >> 8);
    }
    
    public void addLetter(char letter, int obj, Location l) {
    	addLetter(letter, obj, l.x, l.y);
    }
    
    public void addLetter(char letter, int obj, int x, int y) 
    {
    	int ch = ((int) letter);
		
			 if ((obj & GOAL)  != 0) 	ch <<= 16;
		else if ((obj & BOX)   != 0 ||
				 (obj & AGENT) != 0) 	ch <<= 24;
		
		add(ch, x, y);
    }
	
    public int getMasked(int mask, Location l)	{
		return getMasked(mask, l.x, l.y);
	}
	
    private int getMasked(int mask, int x, int y) {
		return data[x][y] & mask;
	}
	
	private int[][] cloneData()
	{
	    int[][] result = new int[data.length][data[0].length];

	    for (int x = 0; x < data.length; x++) 
	    {
	    	for (int y = 0; y < data[0].length; y++)
	    	{
	    		result[x][y] = (data[x][y] & ~LOCKED);
	    	}
	    }
	    return result;
	}
	
	public void addClonedData(GridOperations model)
	{	    
		int[][] data = model.data;
		
	    for (int x = 0; x < data.length; x++) 
	    {
	    	for (int y = 0; y < data[0].length; y++)
	    	{
	    		this.data[x][y] |= data[x][y];
	    	}
	    }
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		// Print integer representation of level
		for (int y = 0; y < height; y++) 
		{
			for (int x = 0; x < width; x++) 
			{
				if (hasObject(AGENT, x, y)) 	 str.append((char) ((data[x][y] & BOX_MASK) >> 24));
				else if (hasObject(BOX	, x, y)) str.append(Character.toUpperCase((char) ((data[x][y] & BOX_MASK) >> 24)));
				else if (hasObject(LOCKED, x, y))str.append('%');
				else if (hasObject(GOAL	, x, y)) str.append((char) ((data[x][y] & GOAL_MASK) >> 16));
				else if (hasObject(WALL	, x, y)) str.append('+');
				else str.append(' ');
			}
			str.append("\n");
		}
		return str.toString();
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(width, height);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GridOperations that = (GridOperations) o;
		return width == that.width &&
				height == that.height &&
				Arrays.equals(data, that.data);
	}
}
