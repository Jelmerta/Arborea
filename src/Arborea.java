/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map.Entry;
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
	
	static final String mapFile = "src/maps/characterlocations2";
	//static final String mapFile = "maps/characterlocations2";
    
    // static values to keep track of mouse actions
    static boolean leftClicked = false;
    static boolean rightClicked = false;
    static boolean currentTeamIsOrcs = ORCTEAM;
    static boolean turnEnded = false;
    static boolean menIsAI = false;
    static boolean orcsIsAI = true;
    static boolean orcStarts = false;
    static Point lastClickPoint = new Point(0,0);
    static Point mousePoint = new Point(0,0);
    static Tile selection = null;
    
    // index of AI
    static int indexAI = Figure.AI_TRUE;
    
    // boolean for when browsing a menu
    static boolean browsingMenu = true;
    
    // boolean for whether or not to play music
    static boolean muteSound = false;
    
    // boolean for showing the intro
    static boolean introduced = false;

    // boolean for finishing the menu
    static boolean finishedMenu = false;
    
    // boolean for the end of the game, and starting anew
    static boolean gameOver = false;
    static boolean playAgain = false;
    
    // boolean for matrix mode
    static boolean enterTheMatrix = false;
    
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
    	grid = new Grid(mapFile);
    	screener = new Screener(windowName, grid);
        musicPlayer = new MusicPlayer();
        menu = new Menu();
    }
    
    // this is the main function that controls everything
	public void run(){

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
				//screener.initCanvasBackground();
				screener.setCanvasBackground(Screener.MENU_COLOR);
			}
		} else {
			if (gameOver){
				if (playAgain){
					grid = new Grid(mapFile);
					gameOver = false;
					playAgain = false;
					screener.showReplayButton(false);
					screener.showMenu(true);
				}				
			} else {
				if (!finishedMenu) return;
				
				if (enterTheMatrix) {
					screener.setCanvasBackground(Screener.MATRIX_COLOR);
				} else
					screener.setCanvasBackground(Screener.GAME_COLOR);
				
				grid.setupSecret();
				
				browsingMenu = false;
				screener.showMenu(false);
				musicPlayer.updateMusicFiles();
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
			finishedMenu = false;
			musicThread.interrupt();
			screener.showTurnButton(false);
			screener.showReplayButton(true);
			return;
		}		
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
		        }       
				// De newSelection != null lijkt me in moves/attacks al overbodig, maar kan geen kwaad te checken en eerder te short-circuiten, geldt ook voor de hasFigures?
				if(newSelection != null  && selection.hasFigure() && newSelection.hasFigure() && selection.getFigure().getTeam() == currentTeamIsOrcs && newSelection.getFigure().getTeam() != currentTeamIsOrcs  && Arrays.asList(selection.getNeighbours()).contains(newSelection)) {
					figure = selection.getFigure();
					figureAttacked = newSelection.getFigure();
					if(figure.hasAttacksLeft()) {
						figure.attack(grid, figureAttacked, true);
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
		Grid aiGrid = grid;
		LinkedList<Act> ai = new LinkedList<Act>();



		Tile moveTile;
		Tile attackTileBefore;
		Tile attackTileAfter;
		Figure attackedFigure;
		double threshold = 2;


		
		Random random = new Random();
		long seed = System.nanoTime();
		random.setSeed(seed);
		ArrayList<Figure> allFiguresOfTeam = grid.getTeam(currentTeamIsOrcs);
		Collections.shuffle(allFiguresOfTeam, random); //TODO use 1 Random object, setSeed
		ArrayList<Act> allAICurrentFigure = new ArrayList<Act>();

		for (Figure currentFigure : allFiguresOfTeam) {
			allAICurrentFigure = currentFigure.getAllPossibleActs(grid, aiGrid);
			Act chosenAI = currentFigure.calculateBestMove(allAICurrentFigure, grid, currentFigure.isNextMoveOffensive(grid, threshold));
			ai.add(chosenAI);







			attackTileBefore = chosenAI.getAttackTileBefore();
			moveTile = chosenAI.getMovingTile();
			attackTileAfter = chosenAI.getAttackTileAfter();
			//chosenAI.printAct();










			if(attackTileBefore != null) {

				attackedFigure = attackTileBefore.getFigure();
				currentFigure.attack(grid, attackedFigure, true);
			}
			if(moveTile != null) {

				currentFigure.move(grid, moveTile);
				currentFigure.setMoved(true);
			}
			if(attackTileAfter != null) {

				attackedFigure = attackTileAfter.getFigure();
				currentFigure.attack(grid, attackedFigure, true);
			}


		}






	}
    
	public Grid getGrid() {
		return grid;
	}
	
//    void setup(String gridFile) { //TODO deze kan weg?
//        //orcs = new Team(true);
//        //humans = new Team(false);
//        grid = new Grid(gridFile);
//    }
}
