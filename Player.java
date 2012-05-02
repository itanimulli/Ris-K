/*This class represents the Player object and keeps track of which territories the player has, the cards they have, and if
 * they have conquered a new territory this turn. It has methods to get and set these attributes, as well as methods to check 
 * the number of troops in a particular territory, if they have the ability to turn in cards, if they want to turn in cards,
 * the number of reinforcements gotten from turning in cards, where the player wants to place the troops,if the player can
 * attack, and if the player can move troops. There are also methods to turn in cards, place reinforcements, attack, move, and
 * collect cards.
 * 
 */
import java.util.*;

public abstract class Player {

	protected ArrayList<Territory> territories;
	protected ArrayList<Integer> cards;
	protected boolean hasConquered;
	protected GameManager manager;
	protected String name;
	protected int remainingReinforcements;
	protected boolean resetSwitch;

	//Constructor
	public Player(GameManager gm){
		territories = new ArrayList<Territory>();
		cards = new ArrayList<Integer>();
		hasConquered = false;
		manager = gm;
		name = "Unnamed Player";
		remainingReinforcements = 0;
		resetSwitch = true;
	}
	
	public void addTerritory(Territory t) {
		t.setOwner(this);
		territories.add(t);
	}
	
	public boolean remove(Territory t) {
		return territories.remove(t);
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(ArrayList<Territory> territories) {
		this.territories = territories;
	}

	protected ArrayList<Integer> getCards() {
		return cards;
	}

	protected void setCards(ArrayList<Integer> cards) {
		this.cards = cards;
	}
	
	public int getCardCount() {
		return cards.size();
	}

	protected boolean hasConquered() {
		return hasConquered;
	}

	protected void setHasConquered(boolean hasConquered) {
		this.hasConquered = hasConquered;
	}

	/*This method returns true if the player has a complete set of cards and false if they don't.
	 *A complete set is 3 of any one card or 1 of all three cards. 
	 */
	protected boolean hasCardSet(){
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
	protected boolean hasTriplet(){
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
	protected boolean hasMixedSet(){
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
	 * Returns a hashmap giving the number of troops added to each territory.
	 */
	public abstract HashMap<Territory, Integer> reinforceProcess();

	/*This method returns a list representing where the user wants to place reinforcements
	 * Helper method for reinforceProcess()
	 */
	protected abstract ArrayList<Territory> askReinforcements(int numReinforcements);

	/*
	 * Returns a territory in which to place a reinforcement at the beginning of the game.
	 */
	public abstract Territory askInitReinforce();

	//Places a single troop in a territory t.
	public boolean placeReinforcement(Territory t){
		if (remainingReinforcements < 1) return false;
		t.reinforce();
		remainingReinforcements--;
		return true;
	}

	//Places multiple troops in a territory t.
	public boolean placeReinforcements(Territory t, int num){
		if (num < 1) return false;
		if (remainingReinforcements < num) return false;
		t.reinforce(num);
		remainingReinforcements -= num;
		return true;
	}

	//This method encompasses the attack decision procedure.
	//Returns an array of the attacking and target territories and number of attacking troops, or null to skip.
	//{ AttackingTerritory, DefendingTerritory, NumTroops }
	public abstract Object[] attackProcess();

	//The method returns whether or not a player has the ability to attack.
	public boolean canAttack(){
		return false;//TODO
	}

	//Encompasses the entire moving procedure
	public abstract void moveProcess();

	//returns whether or not the player has the ability to move troops
	public boolean canMove(){
		return false;//TODO
	}

	public void move(Territory from, Territory to, int num){
		from.remove(num);
		to.reinforce(num);
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
	
	public String toString() {
		return name;
	}
	
	public int numTroops() {
		int troops = 0;
		for(int i=0; i<territories.size(); i++) {
			troops += territories.get(i).getTroops();
		}
		return troops;
	}
	
	public abstract Territory fortifyProcess();
	
	public void resetSwitch(boolean value) {
		resetSwitch = value;
	}
	
	public boolean resetSwitch() {
		return resetSwitch;
	}
	
	public void reset() {
		remainingReinforcements += (int)Math.max(Math.floor(territories.size()/4), 3);
		hasConquered = false;
	}
}