import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;
import java.util.Random.*;

public class Miert_HF_Model extends GridWorldModel {
    public static final int GSize = 60;
    private int numCourier;
    private int numClient;
    private int numShops;
    private boolean takenPositions[][] = new boolean[GSize][GSize];
    private HashMap<Integer, Location> prevLocations = new HashMap<Integer, Location>();

    protected Miert_HF_Model(int numCourier, int numClient,int numShops) {
        super(GSize, GSize, 44);
        this.numCourier = numCourier;
        this.numClient = numClient;
        this.numShops = numShops;

        // set headquarters position
        setAgPos(0, 0, 0);
        takenPositions[0][0] = true;

        // set taxi and client positions
        for(int i = 1; i < 1 + this.numCourier; i++)
            placeTaxi(i);
        for(int i = 1 + this.numCourier; i < 1 + this.numCourier + this.numClient; i++)
            placeClient(i);
        for(int i = 1 + this.numCourier + this.numClient; i < 1 + this.numCourier + this.numClient+this.numShops; i++)
            placeShops(i);
    }

    public void placeTaxi(int agentId) {
        setAgPos(agentId, getGoodTaxiLocation());
    }
    
    

    public void placeClient(int agentId) {
        setAgPos(agentId, getGoodClientLocation());
    }
    
    public void placeShops(int agentId) {
    	setAgPos(agentId,getFreePos());
    }

    public void removeClient(int agentId, int clientId) {
        Location agentLocation = getAgPos(agentId);
        Location clientLocation = getAgPos(clientId);
        remove(AGENT, clientLocation.x, clientLocation.y);

        // reset taken position of client that has been removed
        takenPositions[clientLocation.x][clientLocation.y] = false;		//alatta vagy fölötte -> nem lehet olyan eset, amikor oldalról érkezik?
        if (inGrid(clientLocation.x, clientLocation.y-1))
            takenPositions[clientLocation.x][clientLocation.y-1] = false;
        if (inGrid(clientLocation.x, clientLocation.y+1))
            takenPositions[clientLocation.x][clientLocation.y+1] = false;

        // set taken position for taxi that removed the client
        takenPositions[agentLocation.x][agentLocation.y] = true;
    }

    public Location getGoodTaxiLocation() {
        Location location = getFreePos();
        while (takenInColumnOrRow(location.x, location.y)) {
            location = getFreePos();
        }
        takenPositions[location.x][location.y] = true;
        return location;
    }
    public Location getGoodShopLocation() {
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	int sumAgentsExceptsShops= numClient+numCourier+1;
    	for (int i=sumAgentsExceptsShops;i<=sumAgentsExceptsShops+numShops;i++) {
    		ids.add(i);
    	}
    	Random random = new Random();
    	int randInt = random.nextInt(ids.size());
    	int agentIdRand=ids.get(randInt);
    	return getAgPos(agentIdRand);
    	
    }

    public Location getGoodClientLocation() {
        Location location = getFreePos();
        while (!goodLocationForClient(location )) {
        	location  = getFreePos();
        }

        takenPositions[location .x][location .y] = true;
        if (inGrid(location .x, location .y-1))
            takenPositions[location .x][location .y-1] = true;
        if (inGrid(location .x, location .y+1))
            takenPositions[location .x][location .y+1] = true;

        return location ;
    }

    private boolean goodLocationForClient(Location l) {
        if (takenInColumnOrRow(l.x, l.y))
            return false;
        if (inGrid(l.x, l.y-1) && takenInColumnOrRow(l.x, l.y-1))
            return false;
        if (inGrid(l.x, l.y+1) && takenInColumnOrRow(l.x, l.y+1))
            return false;

        return true;
    }

    private boolean takenInColumnOrRow(int x, int y) {
        for (int i = 0; i < GSize; i++) {
            if (takenPositions[x][i] || takenPositions[i][y]) {
                return true;
            }
        }

        return false;
    }

    enum Direction {UP, DOWN, LEFT, RIGHT;}

    public int getNumCourier() {return numCourier;}

    public int getNumClient() {return numClient;}
    
    public int getNumShops() {return numShops;}

    public boolean move(int agentId, Direction direction) {
        Location location = getAgPos(agentId);
        Location newLoc = null;
        switch (direction) {
            case UP:
                newLoc = new Location(location.x, location.y - 1);
                break;
            case DOWN:
                newLoc = new Location(location.x, location.y + 1);
                break;
            case LEFT:
                newLoc = new Location(location.x - 1, location.y);
                break;
            case RIGHT:
                newLoc = new Location(location.x + 1, location.y);
                break;
        }
        if(newLoc == null || !inGrid(newLoc) || !isFree(newLoc)) {
            return false;
        }

        // update taken positions
        takenPositions[location.x][location.y] = false;
        takenPositions[newLoc.x][newLoc.y] = true;

        prevLocations.put(agentId, location);
        setAgPos(agentId, newLoc);
        return true;
    }

    public Location getPrevAgentLocation(int agent_id) {
        return prevLocations.get(agent_id);
    }

    public Location getAgentLocation(int agentId) {
        return getAgPos(agentId);
    }
    public Location getFreepos() {
    	Location loc = getFreePos();
    	return loc;
    }
}