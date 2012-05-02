/*This class represents the game manager which is what keeps track of the players in the game, the current player, a board.
 * 
 */
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class GameManager {
	
	public final static int CHOOSING = -2, FORTIFYING = -1, TURNING_IN = 0, REINFORCING = 1, ATTACKING = 2, MOVING = 3, COLLECTING = 4; 
	
	private ArrayList<Player> players;
	private int curPlayer;
	private Board board;
	private int currBonusInd = 0;
	private int state = 0;
	
	private PrintStream console;
	
	public static final int[] bonuses = {};
	
	public GameManager(PrintStream output) {
		players = new ArrayList<Player>();
		board = BoardImporter.makeBoard("board.txt");
		curPlayer = 0;
		console = output;
		state = CHOOSING;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player p){
		players.add(p);
	}

	public int getCurPlayer() {
		return curPlayer;
	}

	public void setCurPlayer(int curPlayer) {
		this.curPlayer = curPlayer;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	public int getState(){
		return state;
	}
	
	public void setState(int newState){
		state = newState;
	}
	/*Returns the player that won, and null otherwise
	 */
	public Player hasWon(){
		for(int i=0; i<players.size(); i++){
			if (players.get(i).getTerritories().size() == board.getTerritories().size())
				return players.get(i);
		}
		return null;
	}
	
	/*Calls the methods that take place during a turn. Turn order is as follows. Set hasConquered to false, if the player can 
	 *turn in cards and wants to, do so. Ask the player where they want their reinforcements and do so. Attack and if successful,
	 *set hasConquered to true. Move and collect card if the player hasConquered. Go to next player.
	 */
	public void play(){
		curPlayer = (int)(players.size()*Math.random());
		while(getState() == CHOOSING){
			Player p = nextPlayer();
			message("It's "+p+"'s turn to claim a territory.");
			Territory reinforcedTerritory = p.askInitReinforce();
			doInitReinforce(p, reinforcedTerritory);
		}
		while(hasWon() == null){
			Player p = nextPlayer();
			message("It's "+p+"'s turn!");
			int a = players.size();
			p.reinforceProcess();
			p.setHasConquered(false);
			Object[] attack;
			while ((attack = p.attackProcess()) != null) {
				processAttack(p, attack);
			}
			if(p.hasConquered()) {
				p.collectCard();
				message(p+" conquered this turn, so they get a card.");
			}
			if(a > players.size() && p.getCardCount() > 4) {
				message(p+" has too many cards, and is forced to turn in 3.");
				p.turnInCards();
			}
			p.moveProcess();
		}
	}
	
	//Increments the current player
	public Player nextPlayer(){
		curPlayer++;
		curPlayer %= players.size();
		return players.get(curPlayer);
	}
	
	public void doInitReinforce(Player p, Territory t) {
		p.addTerritory(t);
		t.reinforce();
		message(p + " has placed a troop in " + t);
		for(int i=0; i<board.getTerritories().size(); i++) {
			if (board.getTerritories().get(i).getOwner() == null) return;
		}
		setState(FORTIFYING);
	}
	
	public int remainingFortifications(Player p) {
		int startTroops = 50 - 5*players.size();
		return startTroops - p.numTroops();
	}
	
	public void message(String message) {
		console.println(message);
	}
	
	//attack a specific territory from another territory with a certain number of troops. Returns whether the attacker won.
	public static boolean attack(Territory from, Territory to, int troops){
		Random r = new Random();
		int attackerLoss = 0, defenderLoss = 0;
		int[] attackerRolls = new int[troops];
		int[] defenderRolls = null;
		if(to.getTroops() ==1){
			defenderRolls = new int[1];
		}
		else if (to.getTroops() > 1){
			defenderRolls = new int[2];
		}
		for(int i = 0; i < troops; i++){
			attackerRolls[i] = r.nextInt(5) + 1;
		}
		for(int j = 0; j < defenderRolls.length; j++){
			defenderRolls[j] = r.nextInt(5) + 1;
		}
		Arrays.sort(attackerRolls);
		Arrays.sort(defenderRolls);
		for(int n = 1; n <= defenderRolls.length; n++){
			if(attackerRolls[attackerRolls.length - n] > defenderRolls[defenderRolls.length - n]){
				defenderLoss++;
			}
			else{
				attackerLoss++;
			}
		}
		
		from.remove(attackerLoss);
		to.remove(defenderLoss);
		if(to.getTroops() < 1){
			int remainingAttackers = troops - attackerLoss;
			from.getOwner().addTerritory(to);
			from.getOwner().move(from, to, remainingAttackers);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void processAttack(Player p, Object[] attack) {
		Territory attackingTerritory = (Territory)attack[0];
		Territory defendingTerritory = (Territory)attack[1];
		int attackingTroops = (Integer)attack[2];
		int attackerInitial = attackingTerritory.getTroops();
		int defenderInitial = defendingTerritory.getTroops();
		boolean result = attack(attackingTerritory, defendingTerritory, attackingTroops);
		String resultString;
		int attackerLoss;
		int defenderLoss;
		if (result) {
			p.setHasConquered(true);
			resultString = "won";
			attackerLoss = attackerInitial-(attackingTerritory.getTroops()+defendingTerritory.getTroops());
			defenderLoss = defenderInitial;
		} else {
			resultString = "lost";
			attackerLoss = attackerInitial-attackingTerritory.getTroops();
			defenderLoss = defenderInitial-defendingTerritory.getTroops();
		}
		message(p + " attacked " + defendingTerritory + " from " + attackingTerritory + " with  " + attackingTroops
				+ " troops and " + resultString + "! (Attacker losses: " + attackerLoss + ", Defender Losses: "
				+ defenderLoss + ")");
	}
	
	public void randomPlayer() {
		curPlayer = (int)(players.size()*Math.random());
	}
	
	public int calculateContinentBonus(Player p) {
		int totalBonus = 0;
		ArrayList<Continent> continents = board.getContinents();
		for(int i=0; i<continents.size(); i++) {
			Continent c = continents.get(i);
			if (p.getTerritories().containsAll(c.getTerritories())) totalBonus += c.getValue();
		}
		return totalBonus;
	}
	
}
