package srch.nodes;

import env.model.CellModel;
import env.model.GridOperations;
import jdk.internal.agent.resources.agent;
import level.Direction;
import level.Location;
import level.cell.Agent;
import srch.Node;
import util.ModelUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageNodeBackup  {

    private  int object;
    private  Location location;
    private Agent 	agent;
    private int		agNumber;

    private Direction direction;
    private GridOperations gridOperations;


    public StorageNodeBackup(Location initial, Agent agent, GridOperations gridOperations) {
        //super(initial, 0);
        this.object		=  GridOperations.WALL;
        this.location  	= initial;

        this.gridOperations 		= gridOperations;
        this.agent 		= agent;
        this.agNumber 	= ModelUtil.getAgentNumber(agent);
    }

    private StorageNodeBackup(StorageNodeBackup parentNode, Direction direction, Location location) {
        //super(parentNode, location);
        this.location= location;
        this.direction 	= direction;

        this.object	   	= parentNode.object;


        StorageNodeBackup p = (StorageNodeBackup) parentNode;
        this.gridOperations 		= p.gridOperations;
        this.agent 		= p.agent;
        this.agNumber 	= p.agNumber;
    }

    public int getAgentNumber() {
        return agNumber;
    }

    public List<StorageNodeBackup> getExpandedNodes(GridOperations gridOperationssss)
    {
        List<StorageNodeBackup> expandedNodes = new ArrayList<StorageNodeBackup>(Direction.EVERY.length);

        for (Direction dir : Direction.EVERY)
        {
            Location loc = this.location.newLocation(dir);
            // Add node if loc has agent itself or
            if (this.getModel().hasObject(agNumber, GridOperations.BOX_MASK, loc) ||
                    // Add node if loc is free of object or
                    this.getModel().isFree(this.object, loc))
            {
                expandedNodes.add(new StorageNodeBackup(this, dir, loc));
            }

            // Add node if loc has box with agent's color and
            // this box can be moved to another loc
            if (this.getModel().hasObject(GridOperations.BOX, loc) &&
                    this.getModel().getColor(loc).equals(agent.getColor()) &&
                    !this.getModel().isBlocked(loc))
            {
                expandedNodes.add(new StorageNodeBackup(this, dir, loc));
            }
        }
        return expandedNodes;
    }

    public GridOperations getModel() {
        return this.gridOperations;
    }

    public Location extractPlan()
    {
        return this.location;
    }
}
