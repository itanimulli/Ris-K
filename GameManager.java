/*This class represents the game manager which is what keeps track of the players in the game, the current player, a board.
 * 
 */
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class GameManager {
	private ArrayList<Player> players;
	private int curPlayer;
	private Board board;
	private int currBonusInd = 0;
	
	private PrintStream console;
	
	public static final int[] bonuses = {};
	
	public GameManager(){
		players = new ArrayList<Player>();
		board = BoardImporter.makeBoard("board.txt");
		curPlayer = 0;
		console = System.out;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
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
		for(int i=0; i<board.getTerritories().size(); i++){
			Player p = nextPlayer();
			Territory reinforcedTerritory = p.askInitReinforce();
			p.placeReinforcement(reinforcedTerritory);
			console.println(p + " has placed a troop in " + reinforcedTerritory);
		}
		while(hasWon() == null){
			Player p = nextPlayer();
			console.println("It's "+p+"'s turn!");
			int a = players.size();
			p.reinforceProcess();
			if(p.attackProcess()) {
				p.collectCard();
				console.println(p+" conquered this turn, so they get a card.");
			}
			if(a > players.size() && p.getCardCount() > 4) {
				console.println(p+" has too many cards, and is forced to turn in 3.");
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
}
