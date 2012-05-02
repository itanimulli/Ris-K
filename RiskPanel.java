import java.io.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class RiskPanel extends JPanel implements ActionListener{
	
	private Image background;
	private JButton skipButton;
	private JButton[] tButtons;
	private JLabel[] tLabels;
	private JLabel cards = new JLabel();
	private JLabel state = new JLabel();
	private JLabel dice = new JLabel();
	private JLabel rRein = new JLabel();
	private JLabel prompt = new JLabel();
	
	private RiskGui gui;
	private ArrayList<Territory> territories;

	
	public RiskPanel(RiskGui gui){
		this.gui = gui;
		
		setLayout(null);
		setPreferredSize(new Dimension(1366,700));
		setSize(1366,700);
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
		skipButton = new JButton("->");
		int skipx = (int)(getWidth()-30);
		int skipy = (int)(getHeight()-30);
		add(skipButton);
		skipButton.setBounds(skipx, skipy, 30, 30);
		skipButton.setEnabled(false);
		tButtons = new JButton[gui.getGM().getBoard().getTerritories().size()];
		tLabels = new JLabel[tButtons.length];
		for(int i=0; i<tButtons.length; i++){
			tButtons[i] = new JButton();
			tLabels[i] = new JLabel();
		}
		territories = BoardImporter.getTerritories();
		ArrayList<Point2D.Double> coords = BoardImporter.readCoords("boardCoordinates.txt");
		for(int i=0; i<42; i++){
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
		add(cards);
		add(state);
		add(dice);
		add(rRein);
		add(prompt);
		cards.setBounds(10,400,200,50);
		state.setBounds(10,450,200,50);
		dice.setBounds(10,500,200,50);
		rRein.setBounds(10,550,200,50);
		prompt.setBounds(10,650,400,50);
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
	}

	public JLabel getCards() {
		return cards;
	}

	public void setCards(JLabel cards) {
		this.cards = cards;
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
	
	public void updateTerritory(Territory t) {
		int i = territories.indexOf(t);
		tLabels[i].setText(t.getOwner()+":"+t.getTroops());
		tLabels[i].setBackground(RiskGui.PLAYERCOLORS[gui.getGM().getPlayers().indexOf(t.getOwner())]);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == skipButton) {
			gui.setChosenTerritory(new Territory(""));
		} else if (e.getSource().getClass().getName().endsWith("JButton")) {
			JButton button = (JButton)e.getSource();
			gui.setChosenTerritory(button.getText());
		}
	}
	
	public void allowSkip(boolean allow) {
		skipButton.setEnabled(allow);
	}
	
	public void enableTerritories(ArrayList<Territory> enabledTerritories) {
		for (int i=0; i<tButtons.length; i++) {
			tButtons[i].setEnabled(false);
		}
		for(int i=0; i<enabledTerritories.size(); i++) {
			tButtons[territories.indexOf(enabledTerritories.get(i))].setEnabled(true);
		}
	}
	
	public void enableTerritories() {
		for (int i=0; i<tButtons.length; i++) {
			tButtons[i].setEnabled(true);
		}
	}
	
}