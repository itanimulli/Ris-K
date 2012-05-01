import java.awt.*;
import javax.swing.*;

public class BoardCoordinatesFrame extends JFrame{
	
	public BoardCoordinatesFrame(){
		add(new BoardCoordinates());
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args){
		new BoardCoordinatesFrame();
	}

}
