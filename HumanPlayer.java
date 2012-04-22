import java.util.*;

public class HumanPlayer extends Player {
	
	Scanner in;
	
	public HumanPlayer(GameManager gm) {
		super(gm);
		in = new Scanner(System.in);
	}

	public HashMap<Territory, Integer> reinforceProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	protected ArrayList<Territory> askReinforcements() {
		// TODO Auto-generated method stub
		return null;
	}

	public Territory askInitReinforce() {
		Territory chosenTerritory = null;
		while (chosenTerritory == null) {
			System.out.println("Please enter the name of the territory you wish to claim.");
			String attemptedTerritory = in.nextLine();
			ArrayList<Territory> territories = new ArrayList<Territory>(manager.getBoard().getTerritories());
			Territory existentTerritory = null;
			for(int i=0; i<territories.size() && (existentTerritory == null); i++) {
				if (territories.get(i).getName().equalsIgnoreCase(attemptedTerritory))
					existentTerritory = territories.get(i);
			}
			if (existentTerritory == null) {
				System.out.println("No territory by that name exists.");
			} else if (existentTerritory.getOwner() != null) {
				System.out.println("That territory is already owned by "+existentTerritory.getOwner());
			} else {
				chosenTerritory = existentTerritory;
			}
		}
		return chosenTerritory;
	}

	public boolean attackProcess() {
		// TODO Auto-generated method stub
		return false;
	}

	public void moveProcess() {
		// TODO Auto-generated method stub
		
	}

}
