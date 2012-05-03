/*This class is largely ununsed.
 * 
 */
import java.util.*;

public class HumanPlayer extends Player {
	
	Scanner in;
	RiskGui rg;
	
	public HumanPlayer(GameManager gm, RiskGui rg) {
		super(gm);
		this.rg = rg;
		in = new Scanner(System.in);
	}

	public HashMap<Territory, Integer> reinforceProcess() {
		int totalReinforcements = (int)Math.max(Math.floor(territories.size()*4), 3);
		ArrayList<Territory> reinforcementList = askReinforcements(totalReinforcements);
		HashMap<Territory, Integer> reinforcementMap = new HashMap<Territory, Integer>();
		
		for(int i=0; i<totalReinforcements; i++) {
			Territory t = reinforcementList.get(i);
			t.reinforce();
			if (reinforcementMap.containsKey(t)) {
				reinforcementMap.put(t, reinforcementMap.get(t)+1);
			} else {
				reinforcementMap.put(t, 1);
			}
		}
		
		return reinforcementMap;
	}

	protected ArrayList<Territory> askReinforcements(int numReinforcements) {
		int remainingReinforcements = numReinforcements;
		ArrayList<Territory> reinforcementList = new ArrayList<Territory>();
		
		while (remainingReinforcements > 0) {
			System.out.println("Which territory would you like to reinforce? (Remaining: "+remainingReinforcements);
			Territory attemptedTerritory = manager.getBoard().get(in.nextLine());
			if (attemptedTerritory == null) {
				System.out.println("No territory by that name exists.");
			} else if (attemptedTerritory.getOwner() != this) {
				System.out.println("You do not own that territory.");
			} else {
				System.out.println("How many troops would you like to place in that territory? (Remaining: "+remainingReinforcements);
				int reinforcements = in.nextInt();
				
				while (reinforcements < 0 || reinforcements > remainingReinforcements) {
					if (reinforcements < 0) {
						System.out.println("You cannot place negative reinforcements.");
					} else if (reinforcements > remainingReinforcements) {
						System.out.println("You cannot place that many reinforcements.");
					}
				}
				
				for(int i=0; i<reinforcements; i++) {
					reinforcementList.add(attemptedTerritory);
				}
			}
		}
		return reinforcementList;
	}

	public Territory askInitReinforce() {
		Territory chosenTerritory = null;
		while (chosenTerritory == null) {
			System.out.println("Please enter the name of the territory you wish to claim.");
			Territory attemptedTerritory = manager.getBoard().get(in.nextLine());
			if (attemptedTerritory == null) {
				System.out.println("No territory by that name exists.");
			} else if (attemptedTerritory.getOwner() != null) {
				System.out.println("That territory is already owned by "+attemptedTerritory.getOwner());
			} else {
				chosenTerritory = attemptedTerritory;
			}
		}
		return chosenTerritory;
	}
	
	public Territory fortifyProcess() {
		return territories.get(0);
	}

	public boolean attackProcess() {
		boolean attack = true;
		hasConquered = false;
		while(attack) {
			System.out.println("Please enter a territory to attack from. (Leave blank to end attacking)");
			String enteredTerritory = in.nextLine();
			if (enteredTerritory.equals("")) {
				attack = false;
			} else {
				Territory attemptedTerritory = manager.getBoard().get(enteredTerritory);
				if (attemptedTerritory == null) {
					System.out.println("That territory does not exist.");
				} else if (attemptedTerritory.getOwner() != this) {
					System.out.println("That territory does not belong to you.");
				} else {
					System.out.println("Please enter a territory to attack. (Leave blank to cancel this attack)");
					String enteredTarget = in.nextLine();
					if (!enteredTarget.equals("")) {
						Territory attemptedTarget = manager.getBoard().get(enteredTarget);
						if (attemptedTarget == null) {
							System.out.println("That territory does not exist.");
						} else if (attemptedTarget.getOwner() == this) {
							System.out.println("That territory already belongs to you.");
						} else if (!manager.getBoard().areConnected(attemptedTerritory, attemptedTarget)) {
							System.out.println("Those territories aren't connected.");
						} else {
							System.out.println("How many troops would you like to attack with? (total: "+attemptedTerritory.getTroops()+")");
							int troops = in.nextInt();
							if (troops < 0) {
								System.out.println("You cannot attack with negative troops.");
							} else if (troops > attemptedTerritory.getTroops()-1) {
								System.out.println("You do not have that many troops to attack with.");
							} else if (troops != 0) {
								if (rg.getGM().attack(attemptedTerritory, attemptedTarget, troops)) hasConquered = true;
							}
						}
					}
				}
			}
		}
		return hasConquered;
	}

	public void moveProcess() {
		boolean hasMoved = false;
		while (!hasMoved) {
			System.out.println("Please enter a territory to move from. (Leave blank to skip)");
			String enteredTerritory = in.nextLine();
			if (enteredTerritory == "") {
				hasMoved = true;
			} else {
				Territory attemptedTerritory = manager.getBoard().get(enteredTerritory);
				if (attemptedTerritory == null) {
					System.out.println("That territory does not exist.");
				} else if (attemptedTerritory.getOwner() != this) {
					System.out.println("That territory does not belong to you.");
				} else {
					System.out.println("Please enter a territory to move to. (Leave blank to cancel the move)");
					String enteredTarget = in.nextLine();
					if (!(enteredTarget == "")) {
						Territory attemptedTarget = manager.getBoard().get(enteredTarget);
						if (attemptedTarget == null) {
							System.out.println("That territory does not exist.");
						} else if (attemptedTarget.getOwner() != this) {
							System.out.println("That territory does not belong to you.");
						} else {
							System.out.println("How many troops would you like to move? (Total: "+attemptedTerritory.getTroops()+")");
							int numTroops = in.nextInt();
							if (numTroops < 0) {
								System.out.println("You cannot move negative troops.");
							} else if (numTroops > attemptedTerritory.getTroops()-1) {
								System.out.println("You cannot move that many troops.");
							} else {
								attemptedTerritory.remove(numTroops);
								attemptedTarget.reinforce(numTroops);
								hasMoved = true;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean continueAttack(int remaining, Object[] attack) {
		// TODO Auto-generated method stub
		return false;
	}

}
