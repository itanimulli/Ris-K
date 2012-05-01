import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class RiskPanel extends JPanel{
	
	private Image background;
	private JButton[] tButtons = new JButton[42];
	private JLabel cards = new JLabel();
	private JLabel state = new JLabel();
	private JLabel dice = new JLabel();
	private JLabel rRein = new JLabel();
	private JLabel prompt = new JLabel();


	
	public RiskPanel(){
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
		for(int i=0; i<tButtons.length; i++){
			tButtons[i] = new JButton();
		}
		ArrayList<Territory> territories = BoardImporter.getTerritories();
		ArrayList<Point2D.Double> coords = BoardImporter.readCoords("boardCoordinates.txt");
		for(int i=0; i<42; i++){
			int x = (int)(getWidth()*coords.get(i).x)-50;
			int y = (int)(getHeight()*coords.get(i).y)-15;
			tButtons[i].setText(territories.get(i).getName());
			add(tButtons[i]);
			tButtons[i].setBounds(x, y, 90, 30);
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
	
}