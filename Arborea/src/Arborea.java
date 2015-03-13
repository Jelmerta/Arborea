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
	private static final boolean ORCTEAM = true;
    
    // static values to keep track of mouse actions
    static boolean leftClicked = false;
    static boolean rightClicked = false;
    static boolean currentTeamIsOrcs = ORCTEAM;
    static boolean turnEnded = false;
    static boolean humansIsAI = false;
    static boolean orcsIsAI = false;
    static boolean orcStarts = false;
    static Point lastClickPoint = new Point(0,0);
    static Point mousePoint = new Point(0,0);
    static Tile selection = null;
    
    // text printed on the Texter [POSSIBLY REMOVE LATER]
    static String text = "";
    
	// this is the window in which everything takes place
	private static Screener screener;	
	
	// a music player and the thread it operates in
	private Thread musicThread;
	private MusicPlayer musicPlayer;

	// 30 frames per second =~ 33.3 milliseconds sleep after each frame. 60 =~ 16.6
	private static final int FRAMERATE = 100;
	
	// The grid with all tiles
    public static Grid grid;
    public static Grid aiGrid;
    
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
    	screener = new Screener(windowName);
    }
    
    public Arborea(String windowName, String fileName, String gameTypeString, String orcStartsString) { 	
    	int gameType = Integer.parseInt(gameTypeString);
    	boolean orcStarts = "1".equals(orcStartsString);
    	currentTeamIsOrcs = orcStarts;
    	if(gameType == 0) {
    		humansIsAI = false;
    		orcsIsAI = false;
    	} else if(gameType == 1) {
    		humansIsAI = false;
    		orcsIsAI = true;
    	} else if(gameType == 2) {
    		humansIsAI = true;
    		orcsIsAI = false;
    	} else if(gameType == 3) {
    		humansIsAI = true;
    		orcsIsAI = true;
    	}
        grid = new Grid(fileName);
		for (Figure currentFigure : grid.getTeam(currentTeamIsOrcs)) {
			currentFigure.setMoved(false);
			currentFigure.setAttacked(false);
		}
        screener = new Screener(windowName);
        musicPlayer = new MusicPlayer();
        musicThread = new Thread(musicPlayer);
        musicThread.start();
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
			changeGameState();
			if((!currentTeamIsOrcs && humansIsAI) || (humansIsAI && orcsIsAI) || (currentTeamIsOrcs && orcsIsAI)) {
				handleAIMoves();
				turnEnded = !turnEnded;
			} else 
				handleClicker();
			screener.repaint();
			screener.rewrite();
		}
		//musicThread.stop(); // TODO dont use stop. weet je zeker dat het nut heeft? muziek stopt bij mij gewoon
	}
	
	// Changes the current game state if necessary
	void changeGameState() {
		if(grid.getTeam(true).isEmpty()) {
			System.out.println("Humans won!");
			// print losing screen
			active = false;
		} else if(grid.getTeam(false).isEmpty()) {
			System.out.println("Orcs won!");
			// print winning screen
			active = false;
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
		Figure figure = null;
		Figure figureAttacked= null;
	    if (leftClicked){
	    	Tile newSelection = grid.selectTile(lastClickPoint);
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
		        newSelection.changeNeighbourImages();
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
	
	private void handleAIMoves() {
		aiGrid = grid;
		LinkedList<Act> ai = new LinkedList<Act>();
		Act currentAI = new Act();
		Tile thisTile;
		Figure thisFigure;
		Tile moveTile;
		Tile attackTileBefore;
		Tile attackTileAfter;
		Figure attackedFigure;
		
		long seed = System.nanoTime();
		ArrayList<Figure> allFiguresOfTeam = grid.getTeam(currentTeamIsOrcs);
		Collections.shuffle(allFiguresOfTeam, new Random(seed));
		//TODO make an actual good order
		for (Figure currentFigure : allFiguresOfTeam) {	
			thisTile = grid.getTile(currentFigure.getLocation());
			currentAI.setSelectedTile(thisTile); 
	
			//attack when possible if it has a good outcome (you have high adjacency and other guy doesnt or he has 1 hp left)
			//if(above prox threshold, go closer to average location of teammates)
				//if still attack left, check to attack again (should compare best attack before and after and pick best one)
			//else go closer to enemy
				//if attack left try attack (lowest character pref. (or maybe the general if on orc team)) (should compare best attack before and after and pick best one)
	
			Point[] currentAIPoints = currentFigure.getAI();
			attackTileBefore = grid.getTile(currentAIPoints[0]);
			moveTile = grid.getTile(currentAIPoints[1]);
			attackTileAfter = grid.getTile(currentAIPoints[2]);
			currentAI.setMovingTile(moveTile);
			currentAI.setAttackTileBefore(attackTileBefore);
			currentAI.setAttackTileAfter(attackTileAfter);
			ai.push(currentAI);
			
			// Simulate the new situation on a different grid than the one used to play the game.
			thisFigure = thisTile.getFigure();
			if(attackTileBefore != null) {
				attackedFigure = attackTileBefore.getFigure();
				thisFigure.attack(grid, attackedFigure);
			}
			if(moveTile != null) {
					thisFigure.move(grid, moveTile);
				thisFigure.setMoved(true);
			}
			if(attackTileAfter != null) {
				attackedFigure = attackTileAfter.getFigure();
				thisFigure.attack(grid, attackedFigure);
			}			
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