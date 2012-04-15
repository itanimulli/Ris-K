import java.util.*;

public class Continent {
	
	private String name;
	private int value;
	private ArrayList<Territory> territories;
	
	public Continent(String name, int value) {
		
		this.name = name;
		this.value = value;
		this.territories = new ArrayList<Territory>();
		
	}
	
	public Continent(String name, int value, ArrayList<Territory> territories) {
		
		this.name = name;
		this.value = value;
		this.territories = territories;
		
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public void addTerritory(Territory t) {
		territories.add(t);
	}
	
	public ArrayList<Territory> getTerritories() {
		return territories;
	}
	
	public boolean containsTerritory(Territory t) {
		for(int i=0; i<territories.size(); i++) {
			if (territories.get(i).equals(t)) return true;
		}
		return false;
	}

}