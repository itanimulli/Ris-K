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
	private static final int[] cardBonus = {4, 6, 8, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
	private int cardIndex;
	private int state = 0;
	
	private PrintStream console;
	
	public static final int[] bonuses = {};
	
	public GameManager(PrintStream output) {
		players = new ArrayList<Player>();
		board = BoardImporter.makeBoard("board.txt");
		curPlayer = 0;
		console = output;
		state = CHOOSING;
		cardIndex = 0;
	}
	
	//Returns how much a player gets from turning in cards
	public int getCardBonus() {
		int reward = cardBonus[cardIndex];
		if (cardIndex < cardBonus.length - 1) cardIndex++;
		return reward;
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
	
	//Handles the initial state of reinforcing by adding the territory to the player's owned territories, and reinforcing it.
	//If there are more territories that need to be initially reinforced, end this call, if not set the state to fortifying
	public void doInitReinforce(Player p, Territory t) {
		p.addTerritory(t);
		t.reinforce();
		message(p + " has placed a troop in " + t);
		for(int i=0; i<board.getTerritories().size(); i++) {
			if (board.getTerritories().get(i).getOwner() == null) return;
		}
		setState(FORTIFYING);
	}
	
	//This method calculates the number of troops a player can use to reinforce the territories he initially gets
	public int remainingFortifications(Player p) {
		int startTroops = 50 - 5*players.size();
		return startTroops - p.numTroops();
	}
	
	public void message(String message) {
		console.println(message);
	}
	
	//attack a specific territory from another territory with a certain number of troops. Returns whether the attacker won.
	public static boolean attack(Territory from, Territory to, int troops){
		if (troops == 0) return false;
		Random r = new Random();
		int attackerLoss = 0, defenderLoss = 0;
		int[] attackerRolls = new int[troops];
		int[] defenderRolls = null;
		if(to.getTroops() ==1 || troops == 1){
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
			to.getOwner().remove(to);
			from.getOwner().addTerritory(to);
			from.getOwner().move(from, to, remainingAttackers);
			return true;
		}
		else{
			return false;
		}
	}
	
	//This method returns how many troops were lost in an attack, and prints who won.
	public int processAttack(Player p, Object[] attack) {
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
		if (result) return 0;
		return attackingTroops - attackerLoss;

	}
	
	//Picks a random player. Is used to determine who gets to pick the first territory.
	public void randomPlayer() {
		curPlayer = (int)(players.size()*Math.random());
	}
	
	//Returns the bonus a player gets for controlling continents
	public int calculateContinentBonus(Player p) {
		int totalBonus = 0;
		ArrayList<Continent> continents = board.getContinents();
		for(int i=0; i<continents.size(); i++) {
			Continent c = continents.get(i);
			if (p.getTerritories().containsAll(c.getTerritories())) totalBonus += c.getValue();
		}
		return totalBonus;
	}
	
	//Returns if a player has been eliminated by checking if they have no territories.
	public boolean checkElimination() {
		if (players.get(curPlayer).getTerritories().size() == 0) {
			players.remove(players.get(curPlayer));
			nextPlayer();
			return true;
		}
		return false;
	}
	
}
