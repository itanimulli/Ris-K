import java.util.ArrayList;

public class HumanTest {
	
	public static void main(String[] args) {
		GameManager manager = new GameManager(System.out);
		HumanPlayer player1 = new HumanPlayer(manager);
		player1.setName("Player 1");
		HumanPlayer player2 = new HumanPlayer(manager);
		player2.setName("Player 2");
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(player1);
		players.add(player2);
		
		manager.setPlayers(players);
		
		manager.play();
	}

}
