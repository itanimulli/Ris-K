import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;

public class RiskGui extends JFrame{
	
	
	public RiskGui(){
		getContentPane().add(new RiskPanel());
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args){
		new RiskGui();
	}
}
