/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

//
class Arborea {

	// dimensions of the window
    final static int WINDOW_HEIGHT = 600;
    final static int WINDOW_WIDTH = 800;
    
    // keeping track of teams is done with a boolean
	static final boolean ORCTEAM = true;
	static final boolean MENTEAM = false;

	//static final String mapFile = "maps/characterlocations2"; // console
	static final String mapFile = "src/maps/characterlocations2"; // eclipse
    
    // static values to keep track of mouse actions
    static boolean leftClicked = false;
    static Point lastClickPoint = new Point(0,0);
    static Point mousePoint = new Point(0,0);
    static Tile selection = null;
    
    // keeps track of what team is currently active
    static boolean currentTeamIsOrcs = ORCTEAM;
    
    // keeps track of who starts
    static boolean orcStarts = false;
    
    // keeps track of the end of a turn
    static boolean turnEnded = false;
    
    // keeps track of the intelligence of a team
    static boolean menIsAI = false;
    static boolean orcsIsAI = true;
    
    // index of used AI type
    static int indexAI = Figure.AI_RANDOM;
    
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
    
    // boolean for secret mode
    static boolean enterTheMatrix = false;
    
    // keeps track of the menu for the begin and end of game
    static Menu menu;
    
	// this is the window in which everything takes place
	private static Screener screener;	
	
	// a music player and the thread it operates in
	private Thread musicThread;
	private MusicPlayer musicPlayer;

	// 30 frames per second =~ 33.3 milliseconds sleep after each frame. 60 =~ 16.6
	private static final int FRAMERATE = 16;
	
	// The grid with all tiles
    static Grid grid;
    
    // The queue with AI moves and attacks for every character
    static LinkedList<Act> AIQueue;
    
    // boolean over which the main program loops
    private boolean active = true;
    
    // this is the overlying interface, that when constructed sets up other interfaces
    Arborea(String windowName) {
    	grid = new Grid(mapFile);
        menu = new Menu();
    	screener = new Screener(windowName);
        musicPlayer = new MusicPlayer();
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
		}
		musicThread.interrupt();
	}
	
	// changes the state of the menu
	private void changeMenuState(){
		
		// introduction animation
		if (!introduced){
			if (menu.finishedIntro()) {
				introduced = true;
				screener.showMenu(true);
				screener.setCanvasBackground(Screener.MENU_COLOR);
			}
		} else {
			
			// victory screen
			if (gameOver){
				if (playAgain){
					grid = new Grid(mapFile);
					gameOver = false;
					playAgain = false;
					screener.showReplayButton(false);
					screener.showMenu(true);
					screener.setCanvasBackground(Screener.MENU_COLOR);
				}
				
			// regular menu
			} else {
				if (!finishedMenu) return;
				
				if (enterTheMatrix) {
					screener.setCanvasBackground(Screener.SECRET_COLOR);
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
				
				// no need for turn button if only AI
				if (!menIsAI || !orcsIsAI)
					screener.showTurnButton(true);
			}
		}
	}
	
	// Changes the current game state if necessary
	private void changeGameState() {
		
		// when a team has been defeated
		if(grid.getTeam(ORCTEAM).isEmpty() || grid.getTeam(MENTEAM).isEmpty()) {
			gameOver = true;
			browsingMenu = true;
			finishedMenu = false;
			musicThread.interrupt();
			screener.showTurnButton(false);
			screener.showReplayButton(true);
			return;
		}
		
		// when a turn ends
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
	private void handleClicker(){
		if (browsingMenu){
			// all menu clicking is done via JButtons
		} else {
			handleGameClicks();
		}
	}
	
	// performs actions based on mouse input
	private void handleGameClicks(){
		Figure figure = null;
		Figure figureAttacked= null;
	    if (leftClicked){
	    	
	    	// handle clicking on the mute/play button
	    	handleMuteClick();
	    	
	    	// try to select a tile
	    	Tile newSelection = grid.selectTile(lastClickPoint);
    		
	    	if (selection != null){
		        selection.restoreNeighbourImages();   
		        
		        // if selecting a character, perform action
		        if( (selection.hasFigure()) && (newSelection != null) && (!newSelection.hasFigure()) && Arrays.asList(selection.neighbours).contains(newSelection) && (selection.getFigure().getTeam() == currentTeamIsOrcs) ) {
		        	figure = selection.getFigure();
		        	if(figure.hasMovesLeft()) {
			        	figure.move(grid, newSelection);
			        	figure.setMoved(true);
		        	}
		        }       
				if(newSelection != null  && selection.hasFigure() && newSelection.hasFigure() && selection.getFigure().getTeam() == currentTeamIsOrcs && newSelection.getFigure().getTeam() != currentTeamIsOrcs  && Arrays.asList(selection.neighbours).contains(newSelection)) {
					figure = selection.getFigure();
					figureAttacked = newSelection.getFigure();
					if(figure.hasAttacksLeft()) {
						figure.attack(grid, figureAttacked);
						figure.setAttacked(true);
					}		
				}
	    	}	    	
	    	
	    	// change tile images based on mouse input
	        if (newSelection != null && newSelection.hasFigure() && (newSelection.getFigure().getTeam() == currentTeamIsOrcs)) {
		        newSelection.changeNeighbourImages(grid);
	        }
	        
	        selection = newSelection;			
	        leftClicked = false;
	    }
	}
	
	// mute or unmute the music player
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
	
	// think of moves and attacks for characters and perform them
	private void handleAIMoves() {
		LinkedList<Act> ai = new LinkedList<Act>();
		Tile moveTile = null;
		Tile attackTileBefore = null;
		Tile attackTileAfter = null;
		Figure attackedFigure;
		double threshold = 2;
		
		Random random = new Random();
		long seed = System.nanoTime();
		random.setSeed(seed);
		
		// because of concurrent modification, using a COW-list
		CopyOnWriteArrayList<Figure> allFiguresOfTeam = new CopyOnWriteArrayList<Figure>();;
		for (Figure f : grid.getTeam(currentTeamIsOrcs))
			allFiguresOfTeam.add(f);
		
		// shuffle character order
		Collections.shuffle(allFiguresOfTeam, random); 
		ArrayList<Act> allAICurrentFigure = new ArrayList<Act>();

		for (Figure currentFigure : allFiguresOfTeam) {
			
			// intelligence depends on type of AI used
			switch(Arborea.indexAI) {
			case Figure.AI_RANDOM:
				Random randomAI = new Random();
				ArrayList<Tile> neighboursMoveable = currentFigure.getAllMoveableTiles(grid);
				int randomIndex;
				if(neighboursMoveable.size() != 0 ) {
					randomIndex = randomAI.nextInt(neighboursMoveable.size());
					moveTile = grid.getTile(neighboursMoveable.get(randomIndex).getLocation());
					// check with new figure
					Figure figureStepped;
					int startType = currentFigure.type;
			        switch(startType) {
			            case Figure.TYPE_NONE:
			            	figureStepped = null;
			                break;
			            case Figure.TYPE_SWORD:
			            	figureStepped = new Sword(currentFigure);
			                break;
			            case Figure.TYPE_GENERAL:
			            	figureStepped = new General(currentFigure);
			                break;
			            case Figure.TYPE_GOBLIN:
			            	figureStepped = new Goblin(currentFigure);
			                break;
			            case Figure.TYPE_ORC:
			            	figureStepped = new Orc(currentFigure);
			                break;
			            default:
			            	figureStepped = null;
			            figureStepped.move(grid, moveTile);
			        }
			        if(currentFigure.hasAttacksLeft()) {
			        	ArrayList<Tile> neighboursAttackableAfterMove = figureStepped.getAllMoveableTiles(grid);
						if(neighboursAttackableAfterMove.size() != 0) {
							randomIndex = randomAI.nextInt(neighboursAttackableAfterMove.size());
							attackTileBefore = grid.getTile(neighboursAttackableAfterMove.get(randomIndex).getLocation());
						}
			        }
			        figureStepped.move(grid, grid.getTile(currentFigure.getLocation())); //probably not necessary?
				}
				if(currentFigure.hasAttacksLeft()) {
					ArrayList<Tile> neighboursAttackable = currentFigure.getAllAttackableTiles(grid);
					if(neighboursAttackable.size() != 0) {
						randomIndex = randomAI.nextInt(neighboursAttackable.size());
						attackTileBefore = grid.getTile(neighboursAttackable.get(randomIndex).getLocation());
					}
				}
				Act randomAIAct = new Act();
				randomAIAct.setSelectedTile(grid.getTile(currentFigure.getLocation()));
				randomAIAct.setMovingTile(moveTile);
				if(attackTileBefore != null) {
					randomAIAct.setAttackTileBefore(attackTileBefore);
				}
				break;
					
				case Figure.AI_TRUE:
					allAICurrentFigure = currentFigure.getAllPossibleActs();
					Act chosenAI = currentFigure.calculateBestMove(allAICurrentFigure, currentFigure.isNextMoveOffensive(grid, threshold));
					ai.add(chosenAI);
					attackTileBefore = chosenAI.getAttackTileBefore();
					moveTile = chosenAI.getMovingTile();
					attackTileAfter = chosenAI.getAttackTileAfter();
					break;
			}
			
			// actually perform the decided actions
			if (attackTileBefore != null) {
				attackedFigure = attackTileBefore.getFigure();
				currentFigure.attack(grid, attackedFigure);
			}
			if (moveTile != null) {

				currentFigure.move(grid, moveTile);
				currentFigure.setMoved(true);
			}
			if (attackTileAfter != null) {
				attackedFigure = attackTileAfter.getFigure();
				currentFigure.attack(grid, attackedFigure);
			}
		}
	}
}
