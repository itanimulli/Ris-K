/*This class represents the Player object and keeps track of which territories the player has, the cards they have, and if
 * they have conquered a new territory this turn. It has methods to get and set these attributes, as well as methods to check 
 * the number of troops in a particular territory, if they have the ability to turn in cards, if they want to turn in cards,
 * the number of reinforcements gotten from turning in cards, where the player wants to place the troops,if the player can
 * attack, and if the player can move troops. There are also methods to turn in cards, place reinforcements, attack, move, and
 * collect cards.
 * 
 */
import java.util.*;

public class Player {
	
	private ArrayList<ArrayList<Integer>> territories;
	private ArrayList<Integer> cards;
	private boolean hasConquered;
	
	//Constructor
	public Player(){
		territories = new ArrayList<ArrayList<Integer>>();
		cards = new ArrayList<Integer>();
		hasConquered = false;
	}
	
	public ArrayList<ArrayList<Integer>> getTerritories() {
		return territories;
	}
	
	public void setTerritories(ArrayList<ArrayList<Integer>> territories) {
		this.territories = territories;
	}
	
	public ArrayList<Integer> getCards() {
		return cards;
	}
	
	public void setCards(ArrayList<Integer> cards) {
		this.cards = cards;
	}
	
	public boolean isHasConquered() {
		return hasConquered;
	}
	
	public void setHasConquered(boolean hasConquered) {
		this.hasConquered = hasConquered;
	}
	
	public int troopsAt(Territory T){
		return territories.get(0).get(0);//TODO
	}
	
	
	/*This method returns true if the player has a complete set of cards and false if they don't.
	 *A complete set is 3 of any one card or 1 of all three cards. 
	 */
	public boolean hasCardSet(){
		Iterator<Integer> i = cards.iterator();
		int ones = 0;
		int twos = 0;
		int threes = 0;
		while(i.hasNext()){
			switch(i.next()){
			case 1:
				ones++;
				break;
			case 2:
				twos++;
				break;
			case 3:
				threes++;
				break;
			}	
		}
		if(ones == 3 || twos == 3 || threes == 3 || (ones>=1 && twos >=1 && threes>=1)){
			return true;
		}else{
			return false;
		}
	}
	
	/* Returns whether the player has a set of three identical cards.
	 */
	public boolean hasTriplet(){
		int ones=0, twos=0, threes=0;
		Iterator<Integer> i = cards.iterator();
		while(i.hasNext()){
			switch(i.next()){
			case 1:
				ones++; break;
			case 2: 
				twos++; break;
			case 3: 
				threes++; break;
			}
		}
		return (ones >= 3 || twos >= 3 || threes >= 3);
	}
	
	/* Returns whether the player has one of each type of card.
	 */
	public boolean hasMixedSet(){
		int ones=0, twos=0, threes=0;
		Iterator<Integer> i = cards.iterator();
		while(i.hasNext()){
			switch(i.next()){
			case 1:
				ones++; break;
			case 2: 
				twos++; break;
			case 3: 
				threes++; break;
			}
		}
		return (ones > 0 && twos > 0 && threes > 0);
	}
	
	/*Asks the player if they want to turn in cards, although I (Trey) don't think this method should be here. It would
	 * probably be better in GameManager.
	 */
	public boolean askTurnIn(){
		if(cards.size() > 4)
			return true;
		else if(!hasCardSet())
			return false;
		else
			return false;//TODO to be implemented once the UI is implemented.
	}
	
	
	/* Turns in cards and returns the number of reinforcements gotten. It asks the GameManager what the current reward for
	 * turning in cards. Possibly handle the user choosing which combination to turn in if more than one is possible.
	 */
	public int turnInCards(){
		return 0;//TODO
	}
	
	/*Helper method for turnInCards()
	 */
	private int numReinforcements(){
		return 0;//TODO
	}
	
	/* This method comprises the entire process of placing reinforcements.
	 */
	public void reinforceProcess(){
		//TODO
	}
	
	/*This method returns a jagged matrix representing where the user wants to place reinforcements
	 */
	public ArrayList<ArrayList<Integer>> askReinforcemnts(){//TODO
		return null;
	}
	
	/*
	 * Returns a territory in which to place a reinforcement at the beginning of the game.
	 */
	public Territory askInitReinforce(){
		return null; //TODO
	}
	
	//Places a single troop in a territory t.
	public void placeReinforcement(Territory t){
		//TODO
	}
	
	//Places multiple troops in a territory t.
	public void placeReinforcements(Territory t, int num){
		//TODO
	}
	
	//This method encompasses the entire attack procedure. Returns whether the player won at least one battle.
	public boolean attackProcess(){
		return false;//TODO
	}
	
	//The method returns whether or not a player has the ability to attack.
	public boolean canAttack(){
		return false;//TODO
	}
	
	//attack a specific territory from another territory with a certain number of troops. Returns whether the player won.
	public boolean attack(Territory from, Territory to, int troops){
		return false;//TODO
	}
	
	//Encompasses the entire moving procedure
	public void moveProcess(){//TODO
		
	}
	
	//returns whether or not the player has the ability to move troops
	public boolean canMove(){
		return false;//TODO
	}
	
	public void move(Territory from, Territory to, int num){
		//TODO
	}
	
	//adds a card to the players list if they have conquered this turn. To be called at the end of a turn
	public void collectCard(){
		int card = (int)(Math.random()*3 +1);
		cards.add(card);
	}
	
	//adds all the cards that belong to a player to another players list. To be called when a player is defeated.
	public void collectCards(Player p){
		cards.addAll(p.getCards());
	}
}
