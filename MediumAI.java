import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MediumAI extends ComputerPlayer{
	
	Random r;
	public MediumAI(GameManager gm){
		super(gm);
		r = new Random();
	}
	
	public HashMap<Territory, Integer> reinforceProcess() {
		//TODO
		//generates a hashmap of territories and their associate reinforcements
		HashMap reinforcements = new HashMap<Territory, Integer>();
		while(this.remainingReinforcements > 0){
			reinforcements.put(t,)
			
		}
	}

	
	protected ArrayList<Territory> askReinforcements(int numReinforcements) {
		//generates a list of territories that the AI wants to reinforce
		if(this.hasCardSet() == true){
			this.turnInCards();
		}
		
		int i = r.nextInt(this.territories.size());
		ArrayList<Territory> t = new ArrayList<Territory>();
		for(int j = 0; j < i && j < numReinforcements; j++){
			t.add(this.territories.get(j));
		}
		return t;
	}

	
	public Territory askInitReinforce() {
		// gets a random territory to reinforce at the beginning of the game
		Territory t = null;
		int i = r.nextInt(manager.getBoard().getTerritories().size());
		
		do{
			t = manager.getBoard().getTerritories().get(i);
		}
		while(t.getOwner() != null);
		return t;
	}

	
	public boolean attackProcess() {
		//comprises the entire attack process
		return manager.attack(this.bestStage(), this.bestTarget(this.bestStage()), (this.bestStage().getTroops() -1));
	}

	
	public void moveProcess() {
		//makes a random troop movement (random # of troop between 2 random territories)
		
		int i = r.nextInt(this.getTerritories().size());
		Territory from = this.getTerritories().get(i);
		int j = r.nextInt(this.getTerritories().size());
		Territory to;
		if(i != j){
			to = this.getTerritories().get(j);
		}
		else{
			j++;
			to = this.getTerritories().get(j);
		}
		this.move(from, to, r.nextInt(from.getTroops()-1));
	}
	
	public Territory bestTarget(Territory t){
		int min = 1000;
		Territory best = null;
		ArrayList<Territory> connect = (ArrayList<Territory>) manager.getBoard().getConnections(t);
		for(int j = 1; j < connect.size(); i++){
			if(connect.get(j).getTroops() < min && connect.get(j).getOwner() != this);
			min = connect.get(j).getTroops();
			best = connect.get(j);
		}
		return best;
	}
	
	public Territory bestStage(){
		//gets the territory with the most troops
		int max = 0;
		Territory best = null;
		for(int i = 0; i < this.territories.size(); i++){
			if(this.territories.get(i).getTroops() > max){
				max = this.territories.get(i).getTroops();
				best = this.territories.get(i);
			}
		}
		return best;
	}
	
	public boolean continueAttack(){
		return true;
	}
}