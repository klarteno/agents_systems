package level.cell;

import level.Location;


//TO DO Tracked implements a bit stronger than shaloow clone when no more goals ¨have boxes ,boxes have goals
public class Tracked implements Cloneable {

    public Type type;
    private Location location;
    public Agent agent;
    public Box box;

    public enum Type {
        AGENTT,
        BOXX,
        NONE;
    }

    public Tracked() {
       this.type = Type.NONE;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }


/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracked tracked = (Tracked) o;
        return type == tracked.type &&
                location.equals(tracked.location) &&
                agent.equals(tracked.agent) &&
                box.equals(tracked.box);
    }

    @Override
    public Tracked clone() throws CloneNotSupportedException {
        Tracked tracked = (Tracked) super.clone();
        tracked.box = (Box) this.box.clone();
        tracked.agent = (Agent) this.agent.clone();
        tracked.location = this.location.clone();

        return   tracked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location, agent, box);
    }
*/
}
