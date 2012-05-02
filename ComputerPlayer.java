import java.util.ArrayList;
import java.util.HashMap;


public abstract class ComputerPlayer extends Player{

	public ComputerPlayer(GameManager gm) {
		super(gm);
	}

	public abstract HashMap<Territory, Integer> reinforceProcess();
	protected abstract ArrayList<Territory> askReinforcements(int numReinforcements);
	public abstract Territory askInitReinforce();
	public abstract boolean attackProcess();
	public abstract void moveProcess();

}
