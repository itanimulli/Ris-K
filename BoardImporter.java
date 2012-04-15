import java.io.*;
import java.util.*;
import structure5.GraphMatrixUndirected;

public class BoardImporter {
	
	public static Board makeBoard(String filename) {
		ArrayList<Continent> continents = new ArrayList<Continent>();
		ArrayList<Territory> territories = new ArrayList<Territory>();
		ArrayList<ArrayList<Territory>> edges = new ArrayList<ArrayList<Territory>>();
		Board finalBoard = null;
		
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(filename));
			Scanner s = new Scanner(inputStream);
			
			int currentLine = 0;
			//import list of continents
			for(boolean addingContinents = true; s.hasNext() && addingContinents; currentLine++) {
				
				String currentToken = s.nextLine();
				
				if (currentToken.equals("/-")) { //Break to indicate change to territory list
					addingContinents = false;
				} else {
					String[] values = currentToken.split(":");
					if (values.length != 2) {
						throw new Exception("Corrupt or invalid file: line "+currentLine+": "+currentToken);
					}
					
					String continentName = values[0];
					int continentValue = Integer.parseInt(values[1]);
					
					continents.add(new Continent(continentName, continentValue));
					//System.out.println("Added continent '"+continentName+"' with value "+continentValue);
				}
				
			}
			
			//Import list of territories
			for (int i=0; s.hasNext(); i++) {
				currentLine++;
				
				String currentToken = s.nextLine();
				
				String[] values = currentToken.split(":");
				if (values.length != 3) {
					throw new Exception("Corrupt or invalid file: line "+currentLine+": "+currentToken);
				}
				
				Continent associatedContinent = continents.get(Integer.parseInt(values[0])-1);
				String territoryName = values[1];
				
				String[] connections = values[2].split("-");
				
				Territory newTerritory = new Territory(territoryName);
				territories.add(newTerritory);
				associatedContinent.addTerritory(newTerritory); //Add territory to the continent it belongs to
				
				//System.out.println("Added territory '"+territoryName+"' to continent '"+associatedContinent.getName()+"'");
				
				//Keep a running list of connections for later
				for (int k=0; k<connections.length; k++) {
					if (Integer.parseInt(connections[k])-1 < i) {
						ArrayList<Territory> edgeList = new ArrayList<Territory>();
						edgeList.add(territories.get(i));
						edgeList.add(territories.get(Integer.parseInt(connections[k])-1));
						
						edges.add(edgeList);
					}
				}
			}
			
			//Create the graph
			GraphMatrixUndirected<Territory, String> graph = new GraphMatrixUndirected<Territory, String>(territories.size());
			//Add territories
			for(int i=0; i<territories.size(); i++) {
				graph.add(territories.get(i));
			}
			//Add edges
			for(int i=0; i<edges.size(); i++) {
				Territory firstTerritory = edges.get(i).get(0);
				Territory secondTerritory = edges.get(i).get(1);
				graph.addEdge(firstTerritory, secondTerritory, firstTerritory.getName()+"-"+secondTerritory.getName());
				System.out.println("Added edge '"+firstTerritory.getName()+"-"+secondTerritory.getName()+"'");
			}
			
			finalBoard = new Board(graph, continents);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			finalBoard = null;
		}
		
		return finalBoard;
		
	}

}
