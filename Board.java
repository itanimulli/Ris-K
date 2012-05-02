import structure5.GraphMatrixUndirected;
import java.util.*;

public class Board {
	
	private GraphMatrixUndirected<Territory, String> territories;
	private ArrayList<Continent> continents;
	
	public Board(GraphMatrixUndirected<Territory, String> ts, ArrayList<Continent> cs) {
		territories = ts;
		continents = cs;
	}
	
	public Territory get(String name) {
		Iterator<Territory> iter = territories.iterator();
		while(iter.hasNext()) {
			Territory t = iter.next();
			if (t.getName().equalsIgnoreCase(name)) return t;
		}
		
		return null;
	}
	
	public ArrayList<Continent> getContinents() {
		return continents;
	}
	
	public ArrayList<Territory> getTerritories() {
		ArrayList<Territory> tList = new ArrayList<Territory>();
		
		Iterator<Territory> iter = territories.iterator();
		
		while(iter.hasNext()) {
			tList.add(iter.next());
		}
		
		return tList;
	}
	
	public ArrayList<Territory> getConnections(Territory t) {
		//Returns a list of all territories connected to the given territory
		ArrayList<Territory> neighbors = new ArrayList<Territory>();
		Iterator<Territory> neighborIterator = territories.neighbors(t);
		
		while (neighborIterator.hasNext()) {
			neighbors.add(neighborIterator.next());
		}
		
		return neighbors;
	}
	
	public boolean areConnected(Territory t1, Territory t2) {
		return territories.containsEdge(t1, t2);
	}
	
	private boolean hasExtendedConnectionRecurse(Territory t, Territory dest) {
		if (t == dest) return true;
		Iterator<Territory> neighbors = territories.neighbors(t);
		while(neighbors.hasNext()) {
			Territory neighbor = neighbors.next();
			//Found it!
			if (neighbor == dest) return true;
			//Avoid revisiting vertices we've already checked
			if (territories.isVisited(neighbor)) continue;
			territories.visit(neighbor);
			if (neighbor.getOwner() != t.getOwner()) continue;
			
			//See if the neighbor has a path to the destination
			if (hasExtendedConnectionRecurse(neighbor, dest)) return true;
		}
		//No path found.
		return false;
	}
	
	public boolean hasExtendedConnection(Territory t, Territory dest) {
		territories.reset();
		return hasExtendedConnectionRecurse(t, dest);
	}

}