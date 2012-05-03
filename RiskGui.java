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
		setTitle("Ris-K - A Project by Nick Shelton, Scotty Loftin, Trey Moore and Tom Lu");
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
	
	//Driver
	public static void main(String[] args){
		
		RiskGui rg = new RiskGui();
		GameManager gm = rg.getGM();
		boolean entered = false;
		int numPlayers = 0;
		while(!entered){//get number of players
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
			Object[] options = {"Human", "Easy AI", "Medium AI"};
			int playerType = JOptionPane.showOptionDialog(rg, "Player type for player "+(i+1), "Type of Player", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,  options, options[0]); 
			if(playerType == 1){
				player = new EasyAI(rg.getGM());
			}else if (playerType == 2){
				player = new MediumAI(rg.getGM());
			} else {
				player = new HumanPlayer(rg.getGM(), rg);
			}
			player.setName(playerName);
			gm.addPlayer(player);//add the player to the game manager
		}
		JOptionPane.showMessageDialog(rg, "Choose territories by pressing the button of an unoccupied territory");
		gm.randomPlayer();//Pick a random player to start choosing territories
		ArrayList<Territory> enableTerritories = new ArrayList<Territory>();//Territories able to be selected
		ArrayList<Territory> removeTerritories = new ArrayList<Territory>();//Territories that can't be selected
		enableTerritories.addAll(gm.getBoard().getTerritories());
		while(gm.getState() == GameManager.CHOOSING){//While the game is in the territory choosing state
			/*This section of the code waits for the current player to select a territory and claims it for them if it's not
			 * already claimed.
			 * 
			 */
			Player currentPlayer = gm.nextPlayer();
			rg.rp.enableTerritories(enableTerritories);
			//Turn display prompt
			rg.rp.getPrompt().setText("It's "+currentPlayer+"'s turn to choose a territory.");
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
			enableTerritories.remove(rg.chosenTerritory);
			rg.rp.updateTerritory(rg.chosenTerritory);
		}
		while(gm.getState() == GameManager.FORTIFYING){//While fortifying initially chosen territories
			/*This section of code handles players doing the initial reinforcements. If the current player has zero remaining 
			 * troops for initial reinforcements, it switches to normal game flow. Otherwise it waits for the player
			 * to select a territory that they own and places one reinforcement there. Alerts them if they don't own the selected
			 * territory
			 */
			enableTerritories.clear();
			Player currentPlayer = gm.nextPlayer();
			enableTerritories.addAll(currentPlayer.getTerritories());
			rg.rp.enableTerritories(enableTerritories);
			if (gm.remainingFortifications(currentPlayer) < 1) {
				gm.setState(GameManager.REINFORCING);
			} else {
				rg.rp.getPrompt().setText("It's "+currentPlayer+"'s turn to fortify a territory (Remaining: "+gm.remainingFortifications(currentPlayer) + ")");
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
		int turnCount;
		for(turnCount = 0; gm.hasWon() == null; turnCount++){//Main part of the game, while the game isn't won
			Player currentPlayer = gm.getPlayers().get(gm.getCurPlayer());
			rg.chosenTerritory = null;
			
			if (gm.checkElimination()) continue;//If a player has been eliminated, loop again.
			
			switch (gm.getState()) {
			case GameManager.REINFORCING: //For the reinforcing phase of the turn
				enableTerritories.clear();
				enableTerritories.addAll(currentPlayer.getTerritories());//Only allow the player to select territories they own
				rg.rp.enableTerritories(enableTerritories);
				rg.rp.allowSkip(false);
				rg.rp.allowCards(true);//Allow them to turn in cards
				if (currentPlayer.resetSwitch()) {
					currentPlayer.reset();
					currentPlayer.resetSwitch(false);
				}
				if (currentPlayer.remainingReinforcements == 0) {//Once they run out of reinforcing, switch to attack mode
					currentPlayer.resetSwitch(true);
					gm.setState(GameManager.ATTACKING);
					break;
				}
				rg.rp.getPrompt().setText(currentPlayer+"'s turn, reinforcement phase (Remaining: "+currentPlayer.remainingReinforcements+")");
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer")) {//Do the appropriate methods if the player is a computer
					if (currentPlayer.askTurnIn()) {
						currentPlayer.remainingReinforcements += currentPlayer.turnInCards();
						gm.setState(GameManager.REINFORCING);
						break;
					}
					HashMap<Territory, Integer> reinforceMap = currentPlayer.reinforceProcess();
					for(int i=0; i<currentPlayer.getTerritories().size(); i++) {
						Territory currentTerritory = currentPlayer.getTerritories().get(i);
						if (reinforceMap.containsKey(currentTerritory) && reinforceMap.get(currentTerritory) > 0) {
							int numTroops = reinforceMap.get(currentTerritory);
							currentPlayer.placeReinforcements(currentTerritory, numTroops);
							gm.message(currentPlayer + " has placed " + numTroops + " troops in " + currentTerritory);
							rg.rp.updateTerritory(currentTerritory);
						}
					}
					currentPlayer.resetSwitch(true);
					currentPlayer.remainingReinforcements = 0;
					gm.setState(GameManager.ATTACKING);
				} else {//If its a human player
					while (rg.chosenTerritory == null || rg.chosenTerritory.getOwner() != currentPlayer) {//Wait for them to select a territory
						if (rg.chosenTerritory != null) {
							if (rg.chosenTerritory.getName().equals("Cards")) {
								currentPlayer.remainingReinforcements += rg.chosenTerritory.getTroops();
								gm.setState(GameManager.REINFORCING);
								break;
							}
							JOptionPane.showMessageDialog(rg, "That territory does not belong to you.");
							rg.chosenTerritory = null;
						}
						try {Thread.sleep(10);} catch (InterruptedException e) {}
					}
					try {//Get the number of troops they want to place in the territory
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
				
			case GameManager.ATTACKING://For attacking
				rg.rp.allowCards(false);
				boolean getsCard = false;
				if (!currentPlayer.hasConquered()) {//If they conquered this turn, they get a card at the end of the turn
					getsCard = true;
				}
				rg.rp.allowSkip(true);//Let them choose to end their attacking phase
				rg.rp.getPrompt().setText(currentPlayer+"'s turn, attack phase");
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer")) {//If the player is a computer, do the attacking methods
					Object[] attack;
					while ((attack = currentPlayer.attackProcess()) != null) {
						Player defendPlayer = ((Territory)attack[1]).getOwner();
						boolean wantsToContinue = true;
						int remaining;
						while (wantsToContinue && (remaining = gm.processAttack(currentPlayer, attack)) != 0) {
							wantsToContinue = currentPlayer.continueAttack(remaining, attack);
							attack[2] = remaining;
						}
						rg.rp.updateTerritory((Territory)attack[0]);
						rg.rp.updateTerritory((Territory)attack[1]);
						if (currentPlayer.hasConquered && getsCard) {
							currentPlayer.collectCard();
							getsCard = false;
						}
						if (defendPlayer.getTerritories().size() == 0) {
							currentPlayer.collectCards(defendPlayer);
							while (currentPlayer.cardCount() > 5) {
								currentPlayer.remainingReinforcements = currentPlayer.turnInCards();
								gm.setState(GameManager.REINFORCING);
							}
							break;
						}
					}
					gm.setState(GameManager.MOVING);
				} else {//If the player is human
					while (gm.getState() == GameManager.ATTACKING) {
						rg.chosenTerritory = null;
						enableTerritories.clear();
						removeTerritories.clear();
						enableTerritories.addAll(currentPlayer.getTerritories()); 
						for(int i=0; i<enableTerritories.size(); i++) {
							Territory t = enableTerritories.get(i);
							if (t.getTroops() < 2) {
								removeTerritories.add(t);
								continue;
							}
							ArrayList<Territory> neighbors = gm.getBoard().getConnections(t);
							boolean hasEnemyConnection = false;
							for(int k=0; k<neighbors.size() && !hasEnemyConnection; k++) {
								if (neighbors.get(k).getOwner() != currentPlayer) hasEnemyConnection = true;
							}
							if (!hasEnemyConnection) removeTerritories.add(t);
						}
						enableTerritories.removeAll(removeTerritories);
						rg.rp.enableTerritories(enableTerritories);
						try {Thread.sleep(10);} catch (InterruptedException e) {}
						Territory attackTerritory = null;
						Territory defendTerritory = null;
						Player defendPlayer = null;
						if (rg.chosenTerritory != null) {
							if (rg.chosenTerritory.getName().equals("Skip")) {
								gm.setState(GameManager.MOVING);
								break;
							}
							if (rg.chosenTerritory.getTroops() < 2) continue; else {
								attackTerritory = rg.chosenTerritory;
								rg.chosenTerritory = null;
								enableTerritories.clear();
								removeTerritories.clear();
								enableTerritories.addAll(gm.getBoard().getConnections(attackTerritory));
								for(int i=0; i<enableTerritories.size(); i++) {
									if (enableTerritories.get(i).getOwner() == currentPlayer) removeTerritories.add(enableTerritories.get(i));
								}
								enableTerritories.removeAll(removeTerritories);
								enableTerritories.add(attackTerritory);
								rg.rp.enableTerritories(enableTerritories);
								while (rg.chosenTerritory == null) {
									try {Thread.sleep(10);} catch (InterruptedException e) {}
								}
								if (rg.chosenTerritory.getName().equals("Skip")) {
									gm.setState(GameManager.MOVING);
									continue;
								}
								boolean isNeighbor = false;
								ArrayList<Territory> neighbors = gm.getBoard().getConnections(attackTerritory);
								for (int i=0; i<neighbors.size() && !isNeighbor; i++)
									if (neighbors.get(i) == rg.chosenTerritory) isNeighbor = true;
								if (!isNeighbor) continue;
								if (rg.chosenTerritory.getOwner() == currentPlayer) continue;
								
								defendTerritory = rg.chosenTerritory;
								defendPlayer = defendTerritory.getOwner();
								try {
									int numTroops = Integer.parseInt(JOptionPane.showInputDialog(rg, "How many troops would you like to attack with?", 1));
									if (numTroops < 0) {
										JOptionPane.showMessageDialog(rg, "You cannot attack with negative troops!");
									} else if (numTroops > attackTerritory.getTroops()-1) {
										JOptionPane.showMessageDialog(rg, "You do not have enough troops to perform that attack.");
									} else if (numTroops > 0) {
										Object[] attack = {attackTerritory, defendTerritory, numTroops};
										boolean wantsToContinue = true;
										int remaining;
										while (wantsToContinue && (remaining = gm.processAttack(currentPlayer, attack)) != 0) {
											rg.rp.updateTerritory(attackTerritory);
											rg.rp.updateTerritory(defendTerritory);
											wantsToContinue = (JOptionPane.showConfirmDialog(rg, "Continue Attacking?\n"
													+ "Remaining Attacking Troops: "+remaining
													+ "\nRemaining Defending Troops: "+defendTerritory.getTroops(),
													"Continue Attack?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
											attack[2] = remaining;
										}
										rg.rp.updateTerritory(attackTerritory);
										rg.rp.updateTerritory(defendTerritory);
										if (currentPlayer.hasConquered && getsCard) {
											currentPlayer.collectCard();
											getsCard = false;
										}
										if (defendPlayer.getTerritories().size() == 0) {
											currentPlayer.collectCards(defendPlayer);
											while (currentPlayer.cardCount() > 5) {
												currentPlayer.remainingReinforcements += currentPlayer.turnInCards();
												gm.setState(GameManager.REINFORCING);
											}
											break;
										}
									}
								} catch (NumberFormatException e) {
									JOptionPane.showMessageDialog(rg, "Sorry, that is not a valid integer. Please try again.");
								}
							}
						} else if (rg.chosenTerritory != null) continue;
					}
				}
				break;
				
			case GameManager.MOVING://For moving phase
				rg.rp.allowCards(false);
				rg.rp.allowSkip(true);
				rg.rp.getPrompt().setText(currentPlayer + "'s turn, movement phase");
				if (currentPlayer.getClass().getSuperclass().getName().endsWith("ComputerPlayer")) {//If its a computer player
					Object[] move = currentPlayer.moveProcess();
					if (move == null) {
						gm.nextPlayer();
						gm.setState(GameManager.REINFORCING);
						break;
					}
					Territory sourceTerritory = (Territory)move[0];
					Territory destTerritory = (Territory)move[1];
					int numTroops = (Integer)move[2];
					currentPlayer.move(sourceTerritory, destTerritory, numTroops);
					gm.message(currentPlayer + " has moved " + numTroops + " troops from " + sourceTerritory
							+ " to " + destTerritory + ".");
					rg.rp.updateTerritory(sourceTerritory);
					rg.rp.updateTerritory(destTerritory);
					gm.nextPlayer();
					gm.setState(GameManager.REINFORCING);
				} else {//If it's a human player
					while (gm.getState() == GameManager.MOVING) {
						Territory sourceTerritory = null;
						Territory destTerritory = null;
						rg.chosenTerritory = null;
						enableTerritories.clear();
						removeTerritories.clear();
						enableTerritories.addAll(currentPlayer.getTerritories());
						for(int i=0; i<enableTerritories.size(); i++) {
							if (enableTerritories.get(i).getTroops() < 2) removeTerritories.add(enableTerritories.get(i));
						}
						enableTerritories.removeAll(removeTerritories);
						rg.rp.enableTerritories(enableTerritories);
						
						try {Thread.sleep(10);} catch (InterruptedException e) {}
						if (rg.chosenTerritory != null) {
							if (rg.chosenTerritory.getName().equals("Skip")) {
								gm.nextPlayer();
								gm.setState(GameManager.REINFORCING);
							} else {
								sourceTerritory = rg.chosenTerritory;
								rg.chosenTerritory = null;
								enableTerritories.clear();
								removeTerritories.clear();
								enableTerritories.addAll(currentPlayer.getTerritories());
								for(int i=0; i<enableTerritories.size(); i++) {
									if (!gm.getBoard().hasExtendedConnection(sourceTerritory, enableTerritories.get(i)))
										removeTerritories.add(enableTerritories.get(i));
								}
								enableTerritories.removeAll(removeTerritories);
								rg.rp.enableTerritories(enableTerritories);
								while (rg.chosenTerritory == null) {
									try {Thread.sleep(10);} catch (InterruptedException e) {}
								}
								if (rg.chosenTerritory.getName().equals("Skip")) {
									gm.nextPlayer();
									gm.setState(GameManager.REINFORCING);
								} else {
									if (rg.chosenTerritory == sourceTerritory) continue;
									destTerritory = rg.chosenTerritory;
									rg.chosenTerritory = null;
									try {
										int numTroops = Integer.parseInt(JOptionPane.showInputDialog(rg, "How many troops would you like to move?", 1));
										if (numTroops < 0) {
											JOptionPane.showMessageDialog(rg, "You cannot move negative troops!");
										} else if (numTroops > sourceTerritory.getTroops()+1) {
											JOptionPane.showMessageDialog(rg, "You cannot move that many troops.");
										} else if (numTroops > 0) {
											currentPlayer.move(sourceTerritory, destTerritory, numTroops);
											gm.message(currentPlayer + " has moved " + numTroops + " troops from " + sourceTerritory
													+ " to " + destTerritory + ".");
											rg.rp.updateTerritory(sourceTerritory);
											rg.rp.updateTerritory(destTerritory);
											gm.nextPlayer();
											gm.setState(GameManager.REINFORCING);
										}
									} catch (NumberFormatException e) {
										JOptionPane.showMessageDialog(rg, "Sorry, that is not a valid integer. Please try again.");
									}
								}
							}
						}
					}
				}
			}
		}
		//When someone has one display who won.
		JOptionPane.showMessageDialog(rg, gm.hasWon() + " has won the game!");
		rg.getGM().message("Total turns: " + turnCount);
	}
}
