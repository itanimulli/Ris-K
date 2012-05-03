/*This class is a utility class, used for getting the coordinates of the territories of a map. It's important to be aware of the 
 * fact the the territories must be clicked in the same order they are listed in the board.txt file
 * 
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;

public class BoardCoordinates extends JPanel {
	
	private Image background;
	
	public BoardCoordinates(){
		addMouseListener(new CoordListener());
		setPreferredSize(new Dimension(600,400));
		try{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("RiskBoard.png");
			background = ImageIO.read(input);
		}catch(IOException ioe){
			System.out.println(ioe.getStackTrace());
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		
		this.setVisible(true);
	}
	
	public void paintComponent (Graphics page){
		super.paintComponent(page);
		page.drawImage(background, 0, 0, this.getWidth(), this.getHeight(),null);
	}
	
	//Listener for printing out the fraction for where the user clicked.
	private class CoordListener extends MouseAdapter{
		public void mousePressed(MouseEvent event){
			System.out.println(event.getX()/600.0 +  " " + event.getY()/400.0);
		}
	}

}