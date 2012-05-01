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
	
	
	private class CoordListener extends MouseAdapter{
		public void mousePressed(MouseEvent event){
			System.out.println(event.getX()/600.0 +  " " + event.getY()/400.0);
		}
	}

}