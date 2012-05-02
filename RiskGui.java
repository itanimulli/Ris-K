import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.*;

public class RiskGui extends JFrame {
	
	private GameManager gm;
	private RiskPanel rp;
	public static final Color[] PLAYERCOLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LIGHT_GRAY, Color.MAGENTA};
	
	private Territory chosenTerritory = null;
	
	public RiskGui(){
		gm = new GameManager(System.out);
		rp = new RiskPanel(this);
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
	
	public void setChosenTerritory(Territory t) {
		chosenTerritory = t;
	}
	
	public void setChosenTerritory(String t) {
		setChosenTerritory(gm.getBoard().get(t));
	}
	
	public static void main(String[] args){
		
		RiskGui rg = new RiskGui();
		GameManager gm = rg.getGM();
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
				player = new AdvancedAI(rg.getGM());
				player.setName(playerName);
			}
			gm.addPlayer(player);//add the player to the game manager
		}
		JOptionPane.showMessageDialog(rg, "Choose territories by pressing the button of an unoccupied territory");
		gm.randomPlayer();
		while(gm.getState() == GameManager.CHOOSING){//While the game is in the territory choosing state
			Player currentPlayer = gm.nextPlayer();
			//Turn display prompt
			rg.rp.getPrompt().setText("Choose territories, current player is player " + gm.getCurPlayer()+1 + ", " + currentPlayer);
			rg.chosenTerritory = null;
			if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer"))
				rg.chosenTerritory = currentPlayer.askInitReinforce();
			while (rg.chosenTerritory == null || rg.chosenTerritory.getOwner() != null) {
				if (rg.chosenTerritory != null) {
					JOptionPane.showMessageDialog(rg, "That territory has already been claimed.");
					rg.chosenTerritory = null;
				}
				try {Thread.sleep(10);} catch (InterruptedException e) {}
			}
			gm.doInitReinforce(currentPlayer, rg.chosenTerritory);
			rg.rp.updateTerritory(rg.chosenTerritory);
		}
		while(gm.getState() == GameManager.FORTIFYING){
			Player currentPlayer = gm.nextPlayer();
			if (gm.remainingFortifications(currentPlayer) < 1) {
				gm.setState(GameManager.REINFORCING);
			} else {
				rg.rp.getPrompt().setText("Fortify territories, current player is player " + gm.getCurPlayer()+1 + ", "
						+ currentPlayer + " (Remaining: "+gm.remainingFortifications(currentPlayer) + ")");
				rg.chosenTerritory = null;
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer"))
					rg.chosenTerritory = currentPlayer.fortifyProcess();
				while (rg.chosenTerritory == null || rg.chosenTerritory.getOwner() != currentPlayer) {
					if (rg.chosenTerritory != null) {
						JOptionPane.showMessageDialog(rg, "That territory does not belong to you.");
						rg.chosenTerritory = null;
					}
					try {Thread.sleep(10);} catch (InterruptedException e) {}
				}
				rg.chosenTerritory.reinforce();
				gm.message(currentPlayer + " has fortified " + rg.chosenTerritory);
				rg.rp.updateTerritory(rg.chosenTerritory);
			}
		}
		while(gm.hasWon() == null){
			Player currentPlayer = gm.getPlayers().get(gm.getCurPlayer());
			rg.chosenTerritory = null;
			
			switch (gm.getState()) {
			case GameManager.REINFORCING:
				rg.rp.allowSkip(false);
				if (currentPlayer.resetSwitch()) {
					currentPlayer.reset();
					currentPlayer.resetSwitch(false);
				}
				if (currentPlayer.remainingReinforcements == 0) {
					currentPlayer.resetSwitch(true);
					gm.setState(GameManager.ATTACKING);
					break;
				}
				rg.rp.getPrompt().setText(currentPlayer+"'s turn, reinforcement phase (Remaining: "+currentPlayer.remainingReinforcements+")");
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer")) {
					HashMap<Territory, Integer> reinforceMap = currentPlayer.reinforceProcess();
					for(int i=0; i<currentPlayer.getTerritories().size(); i++) {
						Territory currentTerritory = currentPlayer.getTerritories().get(i);
						if (reinforceMap.containsKey(currentTerritory) && reinforceMap.get(currentTerritory) > 0) {
							int numTroops = reinforceMap.get(currentTerritory);
							currentTerritory.reinforce(numTroops);
							gm.message(currentPlayer + " has placed " + numTroops + " troops in " + currentTerritory);
						}
					}
					gm.setState(GameManager.ATTACKING);
				} else {
					while (rg.chosenTerritory == null || rg.chosenTerritory.getOwner() != currentPlayer) {
						if (rg.chosenTerritory != null) {
							JOptionPane.showMessageDialog(rg, "That territory does not belong to you.");
							rg.chosenTerritory = null;
						}
						try {Thread.sleep(10);} catch (InterruptedException e) {}
					}
					try {
						int numTroops = Integer.parseInt(JOptionPane.showInputDialog(rg, "How many troops would you like to place?", 1));
						if (numTroops < 0) {
							JOptionPane.showMessageDialog(rg, "You cannot place negative reinforcements!");
						} else if (numTroops > currentPlayer.remainingReinforcements) {
							JOptionPane.showMessageDialog(rg, "You do not have that many remaining reinforcements.");
						} else if (numTroops > 0) {
							if (currentPlayer.placeReinforcements(rg.chosenTerritory, numTroops))
								gm.message(currentPlayer + " has placed " + numTroops + " troops in " + rg.chosenTerritory);
							rg.rp.updateTerritory(rg.chosenTerritory);
						}
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(rg, "Sorry, that is not a valid integer. Please try again.");
					}
				}
				break;
				
			case GameManager.ATTACKING:
				rg.rp.allowSkip(true);
				rg.rp.getPrompt().setText(currentPlayer+"'s turn, attack phase");
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer")) {
					Object[] attack;
					while ((attack = currentPlayer.attackProcess()) != null) {
						gm.processAttack(currentPlayer, attack);
					}
					gm.setState(GameManager.MOVING);
				} else {
					rg.chosenTerritory = null;
					while (gm.getState() == GameManager.ATTACKING) {
						try {Thread.sleep(10);} catch (InterruptedException e) {}
						Territory attackTerritory = null;
						Territory defendTerritory = null;
						if (rg.chosenTerritory != null && rg.chosenTerritory.getOwner() == currentPlayer) {
							if (rg.chosenTerritory.getTroops() < 2) continue; else {
								attackTerritory = rg.chosenTerritory;
								rg.chosenTerritory = null;
								while (rg.chosenTerritory == null) {
									try {Thread.sleep(10);} catch (InterruptedException e) {}
								}
								if (rg.chosenTerritory.getName().equals("Skip")) {
									continue;
								}
								boolean isNeighbor = false;
								ArrayList<Territory> neighbors = gm.getBoard().getConnections(attackTerritory);
								for (int i=0; i<neighbors.size() && !isNeighbor; i++)
									if (neighbors.get(i) == rg.chosenTerritory) isNeighbor = true;
								if (!isNeighbor) continue;
								
								defendTerritory = rg.chosenTerritory;
								try {
									int numTroops = Integer.parseInt(JOptionPane.showInputDialog(rg, "How many troops would you like to attack with?", 1));
									if (numTroops < 0) {
										JOptionPane.showMessageDialog(rg, "You cannot attack with negative troops!");
									} else if (numTroops > attackTerritory.getTroops()+1) {
										JOptionPane.showMessageDialog(rg, "You do not have enough troops to perform that attack.");
									} else if (numTroops > 0) {
										Object[] attack = {attackTerritory, defendTerritory, numTroops};
										gm.processAttack(currentPlayer, attack);
										rg.rp.updateTerritory(attackTerritory);
										rg.rp.updateTerritory(defendTerritory);
									}
								} catch (NumberFormatException e) {
									JOptionPane.showMessageDialog(rg, "Sorry, that is not a valid integer. Please try again.");
								}
							}
						} else if (rg.chosenTerritory != null) {
							if (rg.chosenTerritory.getName().equals("Skip")) {
								gm.setState(GameManager.MOVING);
								break;
							}
							continue;
						}
					}
				}
				break;
				
			case GameManager.MOVING:
				rg.rp.allowSkip(true);
				
			}
		}
	}
}
