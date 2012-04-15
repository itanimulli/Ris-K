import structure5.GraphMatrixUndirected;
import java.util.*;

public class Board {
	
	private GraphMatrixUndirected<Territory, String> territories;
	private List<Continent> continents;
	
	public Board(GraphMatrixUndirected<Territory, String> ts, List<Continent> cs) {
		territories = ts;
		continents = cs;
	}
	
	public List<Continent> getContinents() {
		return continents;
	}
	
	public List<Territory> getConnections(Territory t) {
		//Returns a list of all territories connected to the given territory
		List<Territory> neighbors = new LinkedList<Territory>();
		Iterator<Territory> neighborIterator = territories.neighbors(t);
		
		while (neighborIterator.hasNext()) {
			neighbors.add(neighborIterator.next());
		}
		
		return neighbors;
	}
	
	public boolean areConnected(Territory t1, Territory t2) {
		return territories.containsEdge(t1, t2);
	}

}