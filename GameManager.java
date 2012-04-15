/*This class represents the game manager which is what keeps track of the players in the game, the current player, a board.
 * 
 */
import java.util.*;

public class GameManager {
	private ArrayList<Player> players;
	private int curPlayer;
	private Board board;
	private int currBonusInd = 0;
	
	public static final int[] bonuses = {};
	
	public GameManager(){
		players = new ArrayList<Player>();
		board = BoardImporter.makeBoard("board.txt");
		curPlayer = 0;
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
	
	/*Returns the index of the player that won, and -1 otherwise
	 */
	public int hasWon(){
		for(int i=0; i<players.size(); i++){
			ArrayList<ArrayList<Integer>> t = players.get(i).getTerritories();
			boolean hasWonThis = true;
			for(int j=0; j<t.size() && hasWonThis; j++){
				for(int k=0; k<t.get(j).size(); k++){
					boolean conqNext;
					if(t.get(j).get(k) > 0)
						conqNext = true;
					else
						conqNext = false;
					hasWonThis = conqNext && hasWonThis;
				}
			}
			if(hasWonThis)
				return i;
		}
		return -1;
	}
	
	/*Calls the methods that take place during a turn. Turn order is as follows. Set hasConquered to false, if the player can 
	 *turn in cards and wants to, do so. Ask the player where they want their reinforcements and do so. Attack and if successful,
	 *set hasConquered to true. Move and collect card if the player hasConquered. Go to next player.
	 */
	public void play(){
		curPlayer = (int)(players.size()*Math.random());
		Player p = players.get(curPlayer);
		for(int i=0; i<board.size(); i++){
			p.placeReinforcement(p.askInitReinforce());
		}
		while(hasWon() == -1){
			p = players.get(curPlayer);
			int a = players.size();
			p.reinforceProcess();
			if(p.attackProcess()) p.collectCard();
			if(a > players.size() && p.getCards().size() > 4)
				p.turnInCards();
			p.moveProcess();
			nextPlayer();
		}
	}
	
	//Increments the current player
	public Player nextPlayer(){
		curPlayer++;
		curPlayer %= players.size();
		return players.get(curPlayer);
	}
}
