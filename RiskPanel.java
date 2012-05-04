import java.io.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class RiskPanel extends JPanel implements ActionListener{
	
	//The board image
	private Image background;
	//Button to skip the current step
	private JButton skipButton;
	//A button to to display a dialog containing the current player's cards
	private JButton cardsButton;
	//Buttons for the territories
	private JButton[] tButtons;
	//Label for the territories
	private JLabel[] tLabels;
	//Labels that show the state of the game
	private JLabel state = new JLabel();
	private JLabel dice = new JLabel();
	private JLabel rRein = new JLabel();
	private JLabel prompt = new JLabel();
	
	//A reference to the RiskGui that contains this panel
	private RiskGui gui;
	//The territories of the board
	private ArrayList<Territory> territories;

	
	public RiskPanel(RiskGui gui){
		this.gui = gui;
		
		setLayout(null);
		//Size the window
		setPreferredSize(new Dimension(1366,700));
		setSize(1366,700);
		//Read in the board image
		try{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("RiskBoard.png");
			background = ImageIO.read(input);
		}catch(IOException ioe){
			System.out.println(ioe.getStackTrace());
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		repaint();
		//Initialize the buttons and add them to the screen
		skipButton = new JButton(">>>");
		skipButton.addActionListener(this);
		int skipx = (int)(getWidth()-60);
		int skipy = (int)(getHeight()-30);
		add(skipButton);
		skipButton.setBounds(skipx, skipy, 60, 30);
		skipButton.setEnabled(false);
		cardsButton = new JButton("View Cards");
		cardsButton.addActionListener(this);
		int cardx = (int)(getWidth()-180);
		int cardy = (int)(getHeight()-30);
		add(cardsButton);
		cardsButton.setBounds(cardx, cardy, 120, 30);
		cardsButton.setEnabled(false);
		tButtons = new JButton[gui.getGM().getBoard().getTerritories().size()];
		tLabels = new JLabel[tButtons.length];
		for(int i=0; i<tButtons.length; i++){
			tButtons[i] = new JButton();
			tLabels[i] = new JLabel();
		}
		territories = BoardImporter.getTerritories();
		ArrayList<Point2D.Double> coords = BoardImporter.readCoords("boardCoordinates.txt");
		for(int i=0; i<territories.size(); i++){
			int x = (int)(getWidth()*coords.get(i).x)-50;
			int y = (int)(getHeight()*coords.get(i).y)-15;
			tButtons[i].setText(territories.get(i).getName());
			tButtons[i].addActionListener(this);
			add(tButtons[i]);
			tButtons[i].setBounds(x, y, 90, 30);
			int lx = (int)(getWidth()*coords.get(i).x)-50;
			int ly = (int)(getHeight()*coords.get(i).y)-30;
			tLabels[i].setText("No Owner:0");
			add(tLabels[i]);
			tLabels[i].setOpaque(true);
			tLabels[i].setBackground(Color.white);
			tLabels[i].setBounds(lx, ly, 90, 15);
		}
		//Add the state labels
		add(state);
		add(dice);
		add(rRein);
		add(prompt);
		state.setBounds(10,450,200,50);
		dice.setBounds(10,500,200,50);
		rRein.setBounds(10,550,200,50);
		prompt.setBounds(10,650,400,50);
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
	}

	public JLabel getState() {
		return state;
	}

	public void setState(JLabel state) {
		this.state = state;
	}

	public JLabel getDice() {
		return dice;
	}

	public void setDice(JLabel dice) {
		this.dice = dice;
	}

	public JLabel getrRein() {
		return rRein;
	}

	public void setrRein(JLabel rRein) {
		this.rRein = rRein;
	}

	public JLabel getPrompt() {
		return prompt;
	}

	public void setPrompt(JLabel prompt) {
		this.prompt = prompt;
	}
	
	//Updates the label of a territory to reflect the current owner and number of troops.
	public void updateTerritory(Territory t) {
		int i = territories.indexOf(t);
		tLabels[i].setText(t.getOwner()+":"+t.getTroops());
		tLabels[i].setBackground(RiskGui.PLAYERCOLORS[gui.getGM().getPlayers().indexOf(t.getOwner())]);
	}
	
	//The actionPerformed method.
	public void actionPerformed(ActionEvent e) {
		//If the skip button is clicked, relay this information back to the GUI
		if (e.getSource() == skipButton) {
			gui.setChosenTerritory(new Territory("Skip"));
		} 
		//If the cards button was clicked, display a dialog showing the cards held
		else if (e.getSource() == cardsButton) {
			Player currentPlayer = gui.getGM().getPlayers().get(gui.getGM().getCurPlayer());
			String message = "You are holding the following cards: \n\n";
			ArrayList<Integer> cards = currentPlayer.getCards();
			for(int i=0; i<cards.size(); i++) {
				message += cards.get(i)+"\t";
			}
			Object[] options = {"Close", "Turn In"};
			//Offer the option of turning in cards
			if (JOptionPane.showOptionDialog(gui, message, "Cards", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,  options, options[0]) == 1) {
				int reward;
				if ((reward = currentPlayer.turnInCards()) == 0) {
					JOptionPane.showConfirmDialog(gui, "You cannot turn in cards now.");
				} else {
					Territory nT = new Territory("Cards");
					nT.setTroops(reward);
					gui.setChosenTerritory(nT);
				}
			}
		} 
		//If a territory is clicked
		else if (e.getSource().getClass().getName().endsWith("JButton")) {
			JButton button = (JButton)e.getSource();
			gui.setChosenTerritory(button.getText());
		}
	}
	
	public void allowSkip(boolean allow) {
		skipButton.setEnabled(allow);
	}
	
	public void allowCards(boolean allow) {
		cardsButton.setEnabled(allow);
	}
	
	public void enableTerritories(ArrayList<Territory> enabledTerritories) {
		for (int i=0; i<tButtons.length; i++) {
			if (enabledTerritories.indexOf(territories.get(i)) != -1) tButtons[i].setEnabled(true);
				else tButtons[i].setEnabled(false);
		}
	}
	
	public void enableTerritories() {
		for (int i=0; i<tButtons.length; i++) {
			tButtons[i].setEnabled(true);
		}
	}
	
}