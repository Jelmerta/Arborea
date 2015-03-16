/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map.Entry;
//import java.util.Queue; TODO don't need it?
import java.util.Random;

//
class Arborea {
	   
    final static int AMOUNT_SWORD = 6;
    final static int AMOUNT_GENERAL = 3;
    final static int AMOUNT_GOBLIN = 8;
    final static int AMOUNT_ORC = 2;
    
    final static int WINDOW_HEIGHT = 600;
    final static int WINDOW_WIDTH = 800;
    
    final static int GRID_SIZE = 9;
	static final boolean ORCTEAM = true;
	static final boolean MENTEAM = false;
    
    // static values to keep track of mouse actions
    static boolean leftClicked = false;
    static boolean rightClicked = false;
    static boolean currentTeamIsOrcs = ORCTEAM;
    static boolean turnEnded = false;
    static boolean menIsAI = false;
    static boolean orcsIsAI = false;
    static boolean orcStarts = false;
    static Point lastClickPoint = new Point(0,0);
    static Point mousePoint = new Point(0,0);
    static Tile selection = null;
    
    // boolean for when browsing a menu
    static boolean browsingMenu = true;
    
    // boolean for whether or not to play music
    static boolean muteSound = false;
    
    // boolean for showing the intro
    static boolean introduced = false;
    
    // boolean for the end of the game, and starting anew
    static boolean gameOver = false;
    static boolean playAgain = false;
    
    // keeps track of the menu for the begin and end of game
    static Menu menu;
    
    // text printed on the Texter [POSSIBLY REMOVE LATER]
    static String text = "";
    
	// this is the window in which everything takes place
	private static Screener screener;	
	
	// a music player and the thread it operates in
	private Thread musicThread;
	private MusicPlayer musicPlayer;

	// 30 frames per second =~ 33.3 milliseconds sleep after each frame. 60 =~ 16.6
	private static final int FRAMERATE = 16;
	
	// The grid with all tiles
    public Grid grid;
    public Grid aiGridAttackBefore; // Updates when the AI for the next character is updates
    public Grid aiGridAttackAfter;
    public Grid aiGrid; // keeps updating with each move
    
    // The queue with AI moves and attacks for every character
    static LinkedList<Act> AIQueue;
    
    // boolean over which the main program loops
    private boolean active = true;
    
    // this is the overlying interface, that when constructed sets up other interfaces
    public Arborea(String windowName) {
    	
    	// TODO setup grid
    	grid = new Grid();
    	
        // painting starts as soon as the screen is made
        // btw dit is waarom ik deze dus onderaan heb in de constructor
    	screener = new Screener(windowName, grid);
    }
    
    public Arborea(String windowName, String fileName, String gameTypeString, String orcStartsString) { 	
    	int gameType = Integer.parseInt(gameTypeString);
    	boolean orcStarts = "1".equals(orcStartsString);
//    	currentTeamIsOrcs = orcStarts;

        grid = new Grid(fileName);
		for (Figure currentFigure : grid.getTeam(currentTeamIsOrcs)) {
			currentFigure.setMoved(false);
			currentFigure.setAttacked(false);
		}
        screener = new Screener(windowName, grid);
        musicPlayer = new MusicPlayer();
        //musicThread.start();
        menu = new Menu();
    }
    
    // this is the main function that controls everything
	public void run(){
		
		// TODO while getting input from menu
		// while (menu.gettingInfo){	
		//}
		while (active) {
			try {
				Thread.sleep(FRAMERATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (browsingMenu){
				changeMenuState();
			} else {
				changeGameState();		
				if((!currentTeamIsOrcs && menIsAI) || (menIsAI && orcsIsAI) || (currentTeamIsOrcs && orcsIsAI)) {
					handleAIMoves();
					turnEnded = !turnEnded;
				}
			}
			handleClicker();
			screener.repaint();
			screener.rewrite();
		}
		musicThread.interrupt();
	}
	
	// changes the state of the menu
	void changeMenuState(){
		if (!introduced){
			if (menu.finishedIntro()) {
				introduced = true;
				screener.showMenu(true);
				screener.initCanvasBackground();
				// TODO white is ook mooi
				//screener.setCanvasBackground(new Color(0,50,100));
			}
		} else {
			if (gameOver){
				if (playAgain){
					grid = new Grid("src/characterlocations2");
					// TODO ^^^^^^^^^^^^^^^^^^
					gameOver = false;
					playAgain = false;
					screener.showReplayButton(false);
					screener.showMenu(true);
				}				
			} else {
				switch (menu.menuOption){
					case (Menu.ORC_PI_MEN_PI):
						menIsAI = false;
						orcsIsAI = false;
						break;
					case (Menu.ORC_PI_MEN_AI):
						menIsAI = true;
						orcsIsAI = false;					
						break;
					case (Menu.ORC_AI_MEN_PI):
						menIsAI = false;
						orcsIsAI = true;
						break;
					case (Menu.ORC_AI_MEN_AI):
						menIsAI = true;
						orcsIsAI = true;
						break;
					default:
						return;
				}
				
				menu.menuOption = -1;
				browsingMenu = false;
				screener.showMenu(false);
		        musicThread = new Thread(musicPlayer);
				musicThread.start();
				
				for (Figure currentFigure : grid.getTeam(currentTeamIsOrcs)) {
					currentFigure.setMoved(false);
					currentFigure.setAttacked(false);
				}
				
				// don't show button if only AI
				if (!menIsAI || !orcsIsAI)
					screener.showTurnButton(true);
			}
		}
	}
	
	// Changes the current game state if necessary
	void changeGameState() {
		if(grid.getTeam(ORCTEAM).isEmpty() || grid.getTeam(MENTEAM).isEmpty()) {
			gameOver = true;
			browsingMenu = true;
			musicThread.interrupt();
			screener.showTurnButton(false);
			screener.showReplayButton(true);
			return;
			
			// TODO yo moet hier turnEnded nog false worden?
		}
		
		// TODO een turn begins waarin de setbools op true komen		
		if(turnEnded) {
			for (Figure currentFigure : grid.getTeam(currentTeamIsOrcs)) {
				currentFigure.setMoved(true);
				currentFigure.setAttacked(true);
			}
			for (Figure currentFigure : grid.getTeam(!currentTeamIsOrcs)) {
				currentFigure.setMoved(false);
				currentFigure.setAttacked(false);
			}
			currentTeamIsOrcs = !currentTeamIsOrcs;
			turnEnded = false;		
		}
	}
	
	// handles mouse button input
	void handleClicker(){
		if (browsingMenu){
			//browsingMenu = false;
		} else {
			handleGameClicks();
		}
	}
	
	private void handleGameClicks(){
		Figure figure = null;
		Figure figureAttacked= null;
	    if (leftClicked){
	    	
	    	// handle clicking on the mute/play button
	    	handleMuteClick();
	    	
	    	Tile newSelection = grid.selectTile(lastClickPoint);
	    	
    		if(newSelection != null && newSelection.hasFigure()) {
    			figure = newSelection.getFigure();
    			text = "This figure has " + figure.getHitpoints() + " HP left \n";
    			//texter.write("This figure has " + figure.getHitpoints() + " left \n");
    			screener.rewrite();
    		}
    		
	    	if (selection != null){
		        selection.restoreNeighbourImages();   
		        //selection.getFigure().inRange(newSelection.getFigure()) (inRange doesn't work, syntax looks better though)
		        
		        // TODO dude wat is dit voor condition lol
				if( (selection.hasFigure()) && (newSelection != null) && (!newSelection.hasFigure()) && Arrays.asList(selection.getNeighbours()).contains(newSelection) && (selection.getFigure().getTeam() == currentTeamIsOrcs) ) {
		        	figure = selection.getFigure();
		        	if(figure.hasMovesLeft()) {
			        	figure.move(grid, newSelection);
			        	figure.setMoved(true);
		        	}
		        }        	 // De newSelection != null lijkt me in moves/attacks al overbodig, maar kan geen kwaad te checken en eerder te short-circuiten, geldt ook voor de hasFigures?
				if(newSelection != null  && selection.hasFigure() && newSelection.hasFigure() && selection.getFigure().getTeam() == currentTeamIsOrcs && newSelection.getFigure().getTeam() != currentTeamIsOrcs  && Arrays.asList(selection.getNeighbours()).contains(newSelection)) {
					figure = selection.getFigure();
					figureAttacked = newSelection.getFigure();
					if(figure.hasAttacksLeft()) {
						figure.attack(grid, figureAttacked);
						figure.setAttacked(true);
					}		
				}
	    	}	    	
	    	
	        if (newSelection != null && newSelection.hasFigure() && (newSelection.getFigure().getTeam() == currentTeamIsOrcs)) {
		        newSelection.changeNeighbourImages(grid);
	        }
	        
	        selection = newSelection;			
	        leftClicked = false;
	    }
	    if (rightClicked){
	        rightClicked = false;
	    }
	    // DEBUG v
	    text = lastClickPoint.x + "," + lastClickPoint.y
		 + "\n" + mousePoint.x + "," + mousePoint.y;
		 
		 if (selection != null) {
		     text += "\n" + "Selected : " + selection.toString();
		 }

		 text += "\n\n";
		for (Entry<Point, Tile> entry : grid.tiles.entrySet()){
		    Tile t = entry.getValue();
		    text += t.toString() + t.pixelCoords.x + "," + t.pixelCoords.y + "\n"; 
		}
		// END DEBUG ^
	}
	
	private void handleMuteClick(){
		if (leftClicked && lastClickPoint.x >= 700 && lastClickPoint.x <= 760 && lastClickPoint.y >= 20 && lastClickPoint.y <= 80) {
			if (muteSound){
				musicThread = new Thread(musicPlayer);
				musicThread.start();
			} else {
				musicThread.interrupt();
			}
			muteSound = !muteSound;
		}
	}
	
	//attack when possible if it has a good outcome (you have high adjacency and other guy doesnt or he has 1 hp left)
	//if(above prox threshold, go closer to average location of teammates)
		//if still attack left, check to attack again (should compare best attack before and after and pick best one)
	//else go closer to enemy
		//if attack left try attack (lowest character pref. (or maybe the general if on orc team)) (should compare best attack before and after and pick best one)	
	private void handleAIMoves() {
		//Grid aiGrid = grid;
		LinkedList<Act> ai = new LinkedList<Act>();
		Act currentAI;
		Tile thisTile;
		Figure thisFigure;
		Tile moveTile;
		Tile attackTileBefore;
		Tile attackTileAfter;
		Figure attackedFigure;
		Grid aiGridAttackBefore = new Grid(grid);
		Grid aiGridAttackAfter = new Grid(grid);
		
		long seed = System.nanoTime();
		ArrayList<Figure> allFiguresOfTeam = grid.getTeam(currentTeamIsOrcs);
		Collections.shuffle(allFiguresOfTeam, new Random(seed));
		System.out.println("figures " + allFiguresOfTeam);
		//TODO make an actual good order
		int count = 0;
		for (Figure currentFigure : allFiguresOfTeam) {
			//aiGridAttackBefore = aiGrid;
			//aiGridAttackAfter = aiGrid;
			thisTile = grid.getTile(currentFigure.getLocation());
			currentAI = new Act();
			currentAI.setSelectedTile(thisTile); 
	
			Point[] currentAIPoints = currentFigure.getAI(grid, aiGridAttackBefore, aiGridAttackAfter); //getAI changes the states of the grid
			attackTileBefore = aiGridAttackBefore.getTile(currentAIPoints[0]);
			moveTile = aiGridAttackBefore.getTile(currentAIPoints[1]);
			attackTileAfter = aiGridAttackAfter.getTile(currentAIPoints[2]);
			currentAI.setMovingTile(moveTile);
			currentAI.setAttackTileBefore(attackTileBefore);
			currentAI.setAttackTileAfter(attackTileAfter);
			System.out.println("hoi deze tile is: " + currentAI.movingTile);
			ai.add(currentAI);
			
			// Simulate the new situation on a different grid than the one used to play the game.
			//thisFigure = thisTile.getFigure();
			/*if(attackTileBefore != null) {
				System.out.println("attackbefore: " + count);
				attackedFigure = attackTileBefore.getFigure();
				currentFigure.attack(grid, attackedFigure);
			}
			if(moveTile != null) {
				System.out.println("move "  + count + " " + moveTile);
				currentFigure.move(grid, moveTile);
				currentFigure.setMoved(true);
			}
			if(attackTileAfter != null) {
				System.out.println("attackafter: " + count);
				attackedFigure = attackTileAfter.getFigure();
				currentFigure.attack(grid, attackedFigure);
			}*/
			count++;
		}
		Act currentAct;
		int length = ai.size();
		for(int i = 0; i < length; i++) {
			currentAct = ai.pollFirst();
			System.out.println(count + " act Tile " + currentAct.selectedTile +  " " + currentAct.movingTile);
		}
	}
    
	public Grid getGrid() {
		return grid;
	}
	
    void setup(String gridFile) { //TODO deze kan weg?
        //orcs = new Team(true);
        //humans = new Team(false);
        grid = new Grid(gridFile);
    }
}
