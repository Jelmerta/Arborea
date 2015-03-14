import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.awt.Point;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Grid {
    Tile[][] grid;
    //static ArrayList<Tile> gridTiles;
    
    static HashMap<Point, Tile> tiles;
    static Team humans, orcs;    
    
    // default grid construction
    Grid(){
        tiles = new HashMap<Point, Tile>();
        
        int half = Arborea.GRID_SIZE/2;
        Point currentPoint;
        
        int gx = 400, gy = 300;
        
        for (int row = 0; row < Arborea.GRID_SIZE; row++) {
            int cols = Arborea.GRID_SIZE - java.lang.Math.abs(row - half);

            for (int col = 0; col < cols; col++) {
                int xLbl = row < half ? col - row : col - half;
                int yLbl = row - half;
                currentPoint = new Point(xLbl, yLbl);
                Tile currentTile = new Tile(currentPoint);
                tiles.put(currentPoint, currentTile);
                //System.out.println(tiles.values());
                
                int tx = (int) (400 + 72 * ((col * 2 + 1 - cols)/2));
                int ty = (int) (300 + 43 * ((row - half) *2));
                
                if (col%2 == 0 ) ty -= 22;
                
                currentTile.x = tx;
                currentTile.y = ty;
            }
        }
        
        //System.out.println(tiles.size());
        
        // calculate neighbours
		for (Entry<Point, Tile> entry : tiles.entrySet()){
			Tile t = entry.getValue();
			t.calculateNeighbours(tiles);
			//System.out.println(t.toString());
		}
    }
    
    // grid for special world construction using file
    Grid(String gridFile) {
        tiles = new HashMap<Point, Tile>();
        humans = new Team();
        orcs = new Team();
        StringTokenizer st;
        BufferedReader br;
        Tile currentTile;
        Point currentPoint;
        int currentX;
        int currentY;
        int currentUnit;

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(gridFile));
			while ((sCurrentLine = br.readLine()) != null) {
		        st = new StringTokenizer(sCurrentLine);
		        currentX = Integer.parseInt(st.nextToken());
		        currentY = Integer.parseInt(st.nextToken());
		        int unitNumber = Integer.parseInt(st.nextToken());
	            currentUnit = unitNumber;
	            currentPoint = new Point(currentX, currentY);
	            currentTile = new Tile(currentPoint, currentUnit);
	            
	            if((unitNumber == 1 || unitNumber == 2) && (unitNumber != 0)) {
	            	humans.addToTeam(currentTile.getFigure());
	            } else if (unitNumber != 0) {
	            	orcs.addToTeam(currentTile.getFigure());
	            }
                tiles.put(currentPoint, currentTile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Entry<Point, Tile> entry : tiles.entrySet()){
		    Tile t = entry.getValue();
    		t.calculateNeighbours(tiles);
		}
    }

    // returns the tile nearest to the clicked location
	Tile selectTile(Point clickPoint) {
		
		// PROCEDURE :
		// -start at middle
		// -loop over the X coordinate until
		
		Tile currentTile = tiles.get(new Point(0,0));
		Tile nextTile = currentTile;
		
		while (nextTile != null){
			printNeighbours(currentTile);
			if (clickPoint.x > currentTile.pixelCoords.x + currentTile.image.getWidth()){

				System.out.println("greater");
				
				nextTile = currentTile.neighbours[3];
				if (nextTile != null){
					int currentDif = clickPoint.x - currentTile.middle.x;
					int nextDif = clickPoint.x - nextTile.middle.x;
					
					System.out.println(currentTile.middle.x + " " + nextTile.middle.x +  " " + currentDif + " "+ nextDif);
					
					if (currentDif < nextDif) break;
					System.out.println("didnt break from greater");
				}
			}
			else if (clickPoint.x < currentTile.pixelCoords.x) {
				System.out.println("lesser");
				nextTile = currentTile.neighbours[0];
			}
			else {
				System.out.println("breaking");
				
				nextTile = currentTile.neighbours[3];
				if (nextTile != null){
					int currentDif = Math.abs(clickPoint.x - currentTile.middle.x);
					int nextDif = Math.abs(clickPoint.x - nextTile.middle.x);
					
					System.out.println(currentTile.middle.x + " " + nextTile.middle.x +  " " + currentDif + " "+ nextDif);
					
					if (currentDif > nextDif) currentTile = nextTile;
				}
				break;
			}

			//if (nextTile != null)
			//	System.out.println(nextTile.pixelCoords.x);

			currentTile = nextTile;		
		}
		
		nextTile = currentTile;		
		while (nextTile != null){
			//printNeighbours(currentTile);
			
			//System.out.println(currentTile.pixelCoords.y);
			
			if (clickPoint.y > currentTile.pixelCoords.y + currentTile.image.getHeight())
				nextTile = currentTile.neighbours[5];
			else if (clickPoint.y < currentTile.pixelCoords.y)
				nextTile = currentTile.neighbours[2];
			else break;
	
			//if (nextTile != null)
			//	System.out.println(nextTile.pixelCoords.y);
	
			currentTile = nextTile;		
		}		
		return currentTile;
	}
    
    // Prints the coordinates or null for all tiles connected
    void printNeighbours(Tile t) {
		String s = t.coords.x + "," + t.coords.y + ": ";
	    for (Tile tn : t.neighbours){
	        if (tn == null) s += "null ";
	        else s += tn.coords.x + "," + tn.coords.y + " ";
		}
		System.out.println(s);
    }

    public static Tile getTile(Point coordinates) {
        return tiles.get(coordinates);
    }
    
    
    public static ArrayList<Figure> getTeam(boolean side) {
    	return side ? orcs.getTeam() : humans.getTeam();
    }
    
    public static void removeFromTeam(boolean side, Figure figure) {
    	if (side)
    		orcs.remove(figure);
    	else 
    		humans.remove(figure);
    }
    
}