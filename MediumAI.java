import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*medium AI, targeted attacks, random reinforcement*/
public class MediumAI extends ComputerPlayer{
	
	public static final double aggression_ratio = 1.5;
	//Agression Ratio: determines the ratio of attackers to defenders that the AI must reach in a given territory in 
	//order to attack
	public static final int hard_aggression_minimum = 15;
	//Minimum number of troops needed for the AI to attack from any given territory
	
	Random r;
	public MediumAI(GameManager gm){
		super(gm);
		r = new Random();
	}
	
	//reinforce random territoeries with random troops
	public HashMap<Territory, Integer> reinforceProcess() {
		HashMap<Territory, Integer> map = new HashMap<Territory, Integer>();
			for(int i=0; i<territories.size(); i++) {
				map.put(territories.get(i), 0);
			}
			for(int i=remainingReinforcements; i>0; i--) {
				Territory randomTerritory = territories.get((int)(Math.random()*territories.size()));
				map.put(randomTerritory, map.get(randomTerritory)+1);
			}
			return map;
		
	}

	
	protected ArrayList<Territory> askReinforcements(int numReinforcements) {
		//generates a list of territories that the AI wants to reinforce
		if(this.hasCardSet() == true){
			this.turnInCards();
		}
		
		int i = r.nextInt(territories.size());
		ArrayList<Territory> t = new ArrayList<Territory>();
		for(int j = 0; j < i && j < numReinforcements; j++){
			t.add(territories.get(j));
		}
		return t;
	}

	
	public Territory askInitReinforce() {
		// gets a random territory to reinforce at the beginning of the game
		Territory t = null;
		
		do{
			int i = r.nextInt(manager.getBoard().getTerritories().size());
			t = manager.getBoard().getTerritories().get(i);
		}
		while(t.getOwner() != null);
		return t;
	}

	
	public Object[] attackProcess() {
		//comprises the entire attack process: Medium AI will always attack given that the ratio of
		//attacking to defending troops is above a certain threshold and there are no more than 15 more 
		//defending troops than attacking troops
		Territory stage = bestStage();
		if (stage == null) return null;
		Territory target = bestTarget(bestStage());
		if (target == null) return null;
		int ntroops = stage.getTroops() - 1;
		
		double ratio = ((double)ntroops)/target.getTroops();
		int difference = ntroops - target.getTroops();
		
		if ((ratio < aggression_ratio) && (difference < hard_aggression_minimum)) return null; 
		
		Object[] attack = {stage, target, ntroops};
		return attack;
	}

	
	public Object[] moveProcess() {
		//move troops from the second best staging area to the best staging area if they are connected
		Territory to = bestStage();
		Territory from = bestStage(to);
		if (from == null) return null;
		if (from.getTroops() == 1) return null;
		if (!manager.getBoard().hasExtendedConnection(from, to)) return null;
		Object[] move = {from, to, r.nextInt(from.getTroops()-1)};
		return move;
	}
	
	public Territory bestTarget(Territory t){
		//selects the best target as the territory with the fewest number of troops connected to some staging 
		//are t
		int min = 1000;
		Territory best = null;
		ArrayList<Territory> connect = (ArrayList<Territory>) manager.getBoard().getConnections(t);
		for(int j = 0; j < connect.size(); j++){
			if(connect.get(j).getTroops() < min && connect.get(j).getOwner() != this) {
				min = connect.get(j).getTroops();
				best = connect.get(j);
			}
		}
		return best;
	}
	
	public Territory bestStage(Territory except){
		//gets the territory with the most troops (excluding a single territory) from whihc the AI will attack
		int max = 0;
		Territory best = null;
		for(int i = 0; i < territories.size(); i++){
			boolean hasEnemyConnected = false;
			ArrayList<Territory> neighbors;
			for (int j=0; j<manager.getBoard().getConnections(territories.get(i)).size() && !hasEnemyConnected; j++) {
				if (manager.getBoard().getConnections(territories.get(i)).get(j).getOwner() != this) hasEnemyConnected = true;
			}
			if (except != null) hasEnemyConnected = true;
			if(hasEnemyConnected && (territories.get(i).getTroops() > max) && (territories.get(i) != except)){
				max = territories.get(i).getTroops();
				best = territories.get(i);
			}
		}
		return best;
	}
	
	public Territory bestStage(){
		//returns the absolte best stage
		return bestStage(null);
	}
	
	public boolean continueAttack(int remaining, Object[] attack){
		//medium AI will attack if it has at least two troops in a region
		if(remaining < 2){
			return false;
		}
		else{
			return true;
		}
	}

	@Override
	public Territory fortifyProcess() {
		return territories.get(r.nextInt(territories.size()-1));
	}
}
