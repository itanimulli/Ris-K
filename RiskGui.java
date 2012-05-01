import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;

public class RiskGui extends JFrame{
	
	private GameManager gm;
	private RiskPanel rp;
	public static final Color[] PLAYERCOLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LIGHT_GRAY, Color.MAGENTA};
	
	public RiskGui(){
		gm = new GameManager(System.out);
		rp = new RiskPanel();
		getContentPane().add(rp);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public GameManager getGM(){
		return gm;
	}
	
	public void setGM(GameManager ngm){
		gm = ngm;
	}
	
	public static void main(String[] args){
		
		RiskGui rg = new RiskGui();
		boolean entered = false;
		int numPlayers = 0;
		ArrayList<Player> players = new ArrayList<Player>();
		while(!entered){
			try{
				numPlayers = Integer.parseInt(JOptionPane.showInputDialog(rg, "How many players will be playing in this game?"));
				if(numPlayers < 2)
					JOptionPane.showMessageDialog(rg, "Sorry, there must be at least 2 players (and at most 6). Please try again.");
				else if(numPlayers > 6)
					JOptionPane.showMessageDialog(rg, "Sorry, there must be at most 6 players (and at least 2). Please try again.");
				else
					entered = true;
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(rg, "Sorry, that is not a valid integer. Please enter an integer between 2 and 6, inclusive, and try again.");
			}
		}
		for(int i = 0; i<numPlayers; i++){//Get all the player names and player types, human or computer
			String playerName = JOptionPane.showInputDialog(rg, "Input player " + (i+1) + "\'s name.");
			Player player;
			if(JOptionPane.showConfirmDialog(rg, "Is player " + (i+1) + " a human?") == JOptionPane.YES_OPTION){
				player = new HumanPlayer(rg.getGM(), rg);
				player.setName(playerName);
			}else{
				player = new ComputerPlayer(rg.getGM());
				player.setName(playerName);
			}
			rg.getGM().addPlayer(player);//add the player to the game manager
		}
		JOptionPane.showMessageDialog(rg, "Choose territories by pressing the button of an unoccupied territory");
		while(rg.getGM().hasWon() == null){
			while(rg.getGM().getState() == rg.getGM().CHOOSING){//While the game is in the territory chosing state
				rg.rp.getPrompt().setText("Choose territories, current player is player " + rg.getGM().getCurPlayer() + ", " +
				rg.getGM().getPlayers().get(rg.getGM().getCurPlayer())); //This line of code sets the text in the prompt label to 
				// tell the players which players turn it is to choose a territory.
				
				
			}
		}
	}
}
