import java.util.ArrayList;
import java.util.HashMap;


public class EasyAI extends ComputerPlayer {
	
	private boolean attackedThisTurn = false;

	public EasyAI(GameManager gm) {
		super(gm);
		attackedThisTurn = false;
	}

	@Override
	public HashMap<Territory, Integer> reinforceProcess() {
		try{Thread.sleep(5);}catch(Exception e){}
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

	@Override
	protected ArrayList<Territory> askReinforcements(int numReinforcements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Territory askInitReinforce() {
		try{Thread.sleep(5);}catch(Exception e){}
		ArrayList<Territory> tList = manager.getBoard().getTerritories();
		for(int i=0; i<tList.size(); i++) {
			if (tList.get(i).getOwner() == null) return tList.get(i);
		}
		return null;
	}

	@Override
	public Object[] attackProcess() {
		try{Thread.sleep(5);}catch(Exception e){}
		long time = System.currentTimeMillis();
		if (attackedThisTurn) return null;
		Territory sourceTerritory = null;
		Territory destTerritory = null;
		while (sourceTerritory == null) {
			if (System.currentTimeMillis() - time > 50) return null;
			Territory randomTerritory = territories.get((int)Math.random()*territories.size());
			if (randomTerritory.getTroops() < 2) continue;
			ArrayList<Territory> neighbors = manager.getBoard().getConnections(randomTerritory);
			for(int i=0; i<neighbors.size(); i++) {
				if (neighbors.get(i).getOwner() != this) {
					sourceTerritory = randomTerritory;
					destTerritory = neighbors.get(i);
				}
			}
		}
		int maxTroops = sourceTerritory.getTroops()-1;
		Object[] attack = {sourceTerritory, destTerritory, maxTroops};
		attackedThisTurn = true;
		return attack;
	}

	@Override
	public Object[] moveProcess() {
		try{Thread.sleep(5);}catch(Exception e){}
		return null;
	}

	@Override
	public Territory fortifyProcess() {
		try{Thread.sleep(5);}catch(Exception e){}
		return territories.get((int)Math.random()*territories.size());
	}
	
	public void reset() {
		try{Thread.sleep(5);}catch(Exception e){}
		super.reset();
		attackedThisTurn = false;
	}

	@Override
	public boolean continueAttack(int remaining, Object[] attack) {
		return true;
	}

}
