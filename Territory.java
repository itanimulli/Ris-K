//Basic territory class with mutators for all instance variables (all self explanatory)
public class Territory {
	
	private String name;
	private int troops;
	private Player owner;
	
	public Territory (String n){
		name = n;
	}
	
	public void reinforce(int n){
		troops = troops + n;
	}
	
	public void reinforce(){
		troops++;
	}
	
	public void remove(int n){
		troops = troops - n;
	}
	
	public void remove(){
		troops--;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTroops() {
		return troops;
	}

	public void setTroops(int troops) {
		this.troops = troops;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setOwner(Player p) {
		owner = p;
	}
	
	public String toString() {
		return name;
	}
	
}
