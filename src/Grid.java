/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.awt.Point;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Grid {    
	
    HashMap<Point, Tile> tiles;
    Team humans, orcs;    
    
    // grid for world construction using file
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
        int humanCount = 0, orcCount = 0;

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
	            	humans.addToTeam(currentTile.getFigure());//, humanCount);
	            	humanCount++;
	            } else if (unitNumber != 0) {
	            	orcs.addToTeam(currentTile.getFigure());//, orcCount);
	            	orcCount++;
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

    // copy constructor, uses copy constructors of its objects
    public Grid(Grid copy) {
        this.tiles = new HashMap<Point, Tile>();
    	for(Entry<Point, Tile> entry : copy.tiles.entrySet()) {
    		Point p = entry.getKey();
    		Tile t = entry.getValue();
    		tiles.put(new Point(p.x,p.y),new Tile(t));
    	}
    	//System.err.println(tiles.size());
        this.humans = new Team(copy.humans);
        this.orcs = new Team(copy.orcs);
    }

    // returns the tile nearest to the clicked location
	Tile selectTile(Point clickPoint) {
		
		// PROCEDURE :
		// -start at middle
		// -loop over the X coordinate until
		
		Tile currentTile = tiles.get(new Point(0,0));
		Tile nextTile = currentTile;
		
		boolean inTriangle = false;
		while (nextTile != null){
			if (clickPoint.x > currentTile.pixelCoords.x + 68){
				nextTile = currentTile.neighbours[3];
			}
			else if (clickPoint.x < currentTile.pixelCoords.x) {
				nextTile = currentTile.neighbours[0];
			}
			else {
				break;
			}
			currentTile = nextTile;		
		}
		if (currentTile != null)
			if (clickPoint.x <= currentTile.pixelCoords.x + 28)
				inTriangle = true;
		
		nextTile = currentTile;		
		while (nextTile != null){			
			if (clickPoint.y > currentTile.pixelCoords.y + currentTile.image.getHeight())
				nextTile = currentTile.neighbours[5];		
			else if (clickPoint.y < currentTile.pixelCoords.y)
				nextTile = currentTile.neighbours[2];
			else break;
			currentTile = nextTile;		
		}
		
		if (currentTile != null && inTriangle){
			int slopeX = clickPoint.x - currentTile.pixelCoords.x;
			int slopeY = currentTile.pixelCoords.y + (currentTile.image.getHeight()/2) - clickPoint.y;				

			// the slope of the line is y(x) = x
			if (clickPoint.y <= currentTile.pixelCoords.y + 29){
				if (slopeY > slopeX)
					currentTile = currentTile.neighbours[1];
			} else {
				if (Math.abs((double)slopeY) > slopeX)
					currentTile = currentTile.neighbours[0];
			}
		}		
		return currentTile;
	}
    void printNeighbours(Tile t) {
		String s = t.coords.x + "," + t.coords.y + ": ";
	    for (Tile tn : t.neighbours){
	        if (tn == null) s += "null ";
	        else s += tn.coords.x + "," + tn.coords.y + " ";
		}
	    System.out.println(s);
    }

    public Tile getTile(Point coordinates) {
        return tiles.get(coordinates);
    }
    
    // returns the boolean side (true orc, false man) of the team
    public ArrayList<Figure> getTeam(boolean side) {
    	return side ? orcs.getTeam() : humans.getTeam();
    }
    
    public void removeFromTeam(boolean side, Figure figure) {
       	if (side)
    		orcs.remove(figure);
    	else 
    		humans.remove(figure);
    }
    
    void setupSecret(){
		for (Entry<Point, Tile> entry : tiles.entrySet()){
			Tile t = entry.getValue();
    		t.setSecret();
		}
    }
}
