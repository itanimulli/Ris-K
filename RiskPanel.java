import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;


public class RiskPanel extends JPanel{
	
	Image background;
	GameManager gm;
	JButton[] tButtons = new JButton[42];
	JLabel cards = new JLabel();
	JLabel state = new JLabel();
	JLabel dice = new JLabel();
	JLabel rRein = new JLabel();
	JPanel panel = new JPanel();
	
	public RiskPanel(){
		setLayout(null);
		gm = new GameManager(System.out);
		setPreferredSize(new Dimension(1366,700));
		setSize(1366,700);
		System.out.println(getWidth());
		System.out.println(getHeight());
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
		System.out.println(getWidth());
		System.out.println(getHeight());
		ArrayList<Territory> territories = BoardImporter.getTerritories();
		ArrayList<Point2D.Double> coords = BoardImporter.readCoords("boardCoordinates.txt");
		for(int i=0; i<42; i++){
			int x = (int)(getWidth()*coords.get(i).x)-50;
			int y = (int)(getHeight()*coords.get(i).y)-15;
			tButtons[i].setText(territories.get(i).getName());
			add(tButtons[i]);
			tButtons[i].setBounds(x, y, 90, 30);
		}
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
	}
	
}