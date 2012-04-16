
public class Territory {
	
	private String name;
	private int troops;
	
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
	
}
