import java.util.*;

public class BoardChecker {
	//Here for debugging purposes
	
	public static void main(String[] args) {
		
		Board b = BoardImporter.makeBoard("board.txt");
		
		List<Continent> cs = b.getContinents();
		
		System.out.println("\n==Board==");		
		for(int i=0; i<cs.size(); i++) {
			System.out.print(cs.get(i).getName()+": ");
			for(int k=0; k<cs.get(i).getTerritories().size(); k++) {
				System.out.print(cs.get(i).getTerritories().get(k).getName()+", ");
			}
			System.out.print("\n");
		}
		
	}

}
