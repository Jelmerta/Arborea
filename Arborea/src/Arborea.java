import java.awt.Point;
import java.util.Arrays;
import java.util.Map.Entry;

/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

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
	private static final boolean GENERALTEAM = false;
    
    // static values to keep track of mouse actions
    static boolean leftClicked = false;
    static boolean rightClicked = false;
    static boolean currentTeam = ORCTEAM;
    static boolean turnEnded = false;
    static Point lastClickPoint = new Point(0,0);
    static Point mousePoint = new Point(0,0);
    static Tile selection = null;
    
    // text printed on the Texter [POSSIBLY REMOVE LATER]
    static String text = "";
    
	// this is the window in which everything takes place
	private static Screener screener;	
	
	// The audio player for music and SFX
	private static Sound soundPlayer;

	// 30 frames per second =~ 33.3 milliseconds sleep after each frame. 60 =~ 16.6
	private static final int FRAMERATE = 100;
	
	// The grid with all tiles
    static Grid grid;
    
    // this is the overlying interface, that when constructed sets up other interfaces
    public Arborea(String windowName) {
    	
    	// TODO setup grid
    	grid = new Grid();
    	
        // painting starts as soon as the screen is made
        // btw dit is waarom ik deze dus onderaan heb in de constructor
    	screener = new Screener(windowName);
    }
    
    public Arborea(String windowName, String fileName) {
        grid = new Grid(fileName);
        screener = new Screener(windowName);
        soundPlayer = new Sound();
        Thread soundPlayer = new Thread(new Sound());
        soundPlayer.start();
    }
    
    // this is the main function that controls everything
	public void run(){
		while (true) {
			try {
				Thread.sleep(FRAMERATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			changeGameState();
			handleClicker();
			screener.repaint();
			screener.rewrite();
		}
	}
	
	// Changes the current game state if necessary
	void changeGameState() {
		if(grid.getTeam(true).isEmpty()) {
			System.out.println("Humans won!");
			// print losing screen
			System.exit(1);
		} else if(grid.getTeam(false).isEmpty()) {
			System.out.println("Orcs won!");
			// print winning screen
			System.exit(1);
		}
		if(turnEnded) {
			for (Figure currentFigure : grid.getTeam(currentTeam)) {
				currentFigure.setMoved(false);
				currentFigure.setAttacked(false);
			}
			currentTeam = !currentTeam;
			turnEnded = false;		
		}

	}
	
	// handles mouse button input
	void handleClicker(){	
		Figure figure = null;
		Figure figureAttacked= null;
	    if (leftClicked){
	    	Tile newSelection = grid.selectTile(lastClickPoint);
	    	System.out.println(newSelection);
	    	if (selection != null){
		        selection.restoreNeighbourImages();   
		        //selection.getFigure().inRange(newSelection.getFigure()) (inRange doesn't work, syntax looks better though)
				if( (selection.hasFigure()) && (newSelection != null) && (!newSelection.hasFigure()) && Arrays.asList(selection.getNeighbours()).contains(newSelection) && (selection.getFigure().getTeam() == currentTeam) ) {
		        	figure = selection.getFigure();
		        	if(figure.hasMovesLeft()) {
			        	figure.move(newSelection);
			        	figure.setMoved(true);
			        	selection.setFigure(null);
			        	//newSelection.setFigure(figure);
		        	}
		        }        	 // De newSelection != null lijkt me in moves/attacks al overbodig, maar kan geen kwaad te checken en eerder te short-circuiten, geldt ook voor de hasFigures?
				if(newSelection != null  && selection.hasFigure() && newSelection.hasFigure() && selection.getFigure().getTeam() == currentTeam && newSelection.getFigure().getTeam() != currentTeam  && Arrays.asList(selection.getNeighbours()).contains(newSelection)) {
					figure = selection.getFigure();
					figureAttacked = newSelection.getFigure();
					if(figure.hasAttacksLeft()) {
						figure.attack(figureAttacked);						
						figure.setAttacked(true);
					
					}		
				}
	    	}	    	
	    	
	        if (newSelection != null && newSelection.hasFigure() && (newSelection.getFigure().getTeam() == currentTeam)) {
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
    
    void setup(String gridFile) {
        //orcs = new Team(true);
        //humans = new Team(false);
        grid = new Grid(gridFile);
    }
}