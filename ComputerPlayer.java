import java.util.ArrayList;
import java.util.HashMap;


public class ComputerPlayer extends Player{

	public ComputerPlayer(GameManager gm) {
		super(gm);
	}

	@Override
	public HashMap<Territory, Integer> reinforceProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<Territory> askReinforcements(int numReinforcements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Territory askInitReinforce() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean attackProcess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveProcess() {
		// TODO Auto-generated method stub
		
	}
	
	

}
