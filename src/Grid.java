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

// this object contains the tiles and teams in the game-world
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

        // initial characters are loaded from text file
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
        
        // neighbors are calculated for each tile
		for (Entry<Point, Tile> entry : tiles.entrySet()){
		    Tile t = entry.getValue();
    		t.calculateNeighbours(tiles);
		}
    }

    // copy constructor, uses copy constructors of its objects
    Grid(Grid copy) {
        this.tiles = new HashMap<Point, Tile>();
    	for(Entry<Point, Tile> entry : copy.tiles.entrySet()) {
    		Point p = entry.getKey();
    		Tile t = entry.getValue();
    		this.tiles.put(new Point(p.x,p.y),new Tile(t));
    	}
		for (Entry<Point, Tile> entry : this.tiles.entrySet()){
		    Tile t = entry.getValue();
    		t.calculateNeighbours(this.tiles);
		}
    	
    	this.humans = new Team();
    	this.orcs = new Team();
    	
        for (Entry<Point,Tile> entry : this.tiles.entrySet()){
        	Tile t = entry.getValue();
        	if (t.hasFigure()) {
        		if (t.getFigure().teamIsOrcs)
        			orcs.addToTeam(t.getFigure());
        		else
        			humans.addToTeam(t.getFigure());
        	}
        }
    }

    // returns the tile nearest to the clicked location
	Tile selectTile(Point clickPoint) {
		
		// PROCEDURE :
		// -start at middle
		// -loop over X until found square or triangle
		// -loop over Y
		// -if in triangle, adjust X and Y
		
		Tile currentTile = tiles.get(new Point(0,0));
		Tile nextTile = currentTile;
		
		boolean inTriangle = false;
		while (nextTile != null){
			if (clickPoint.x > currentTile.getPixelCoords().x + Tile.SELECT_RECT + Tile.SELECT_TRI){
				nextTile = currentTile.neighbours[3];
			}
			else if (clickPoint.x < currentTile.getPixelCoords().x) {
				nextTile = currentTile.neighbours[0];
			}
			else {
				break;
			}
			currentTile = nextTile;		
		}
		if (currentTile != null)
			if (clickPoint.x <= currentTile.getPixelCoords().x + Tile.SELECT_TRI)
				inTriangle = true;
		
		nextTile = currentTile;		
		while (nextTile != null){			
			if (clickPoint.y > currentTile.getPixelCoords().y + currentTile.image.getHeight())
				nextTile = currentTile.neighbours[5];		
			else if (clickPoint.y < currentTile.getPixelCoords().y)
				nextTile = currentTile.neighbours[2];
			else break;
			currentTile = nextTile;		
		}
		
		if (currentTile != null && inTriangle){
			int slopeX = clickPoint.x - currentTile.getPixelCoords().x;
			int slopeY = currentTile.getPixelCoords().y + (currentTile.image.getHeight()/2) - clickPoint.y;				

			// the slope of the line is y(x) = x
			if (clickPoint.y <= currentTile.getPixelCoords().y + Tile.SELECT_TRI){
				if (slopeY > slopeX)
					currentTile = currentTile.neighbours[1];
			} else {
				if (Math.abs((double)slopeY) > slopeX)
					currentTile = currentTile.neighbours[0];
			}
		}		
		return currentTile;
	}
	
	// prints the neighbors of a tile
    void printNeighbours(Tile t) {
		String s = t.getLocation().x + "," + t.getLocation().y + ": ";
	    for (Tile tn : t.neighbours){
	        if (tn == null) s += "null ";
	        else s += tn.getLocation().x + "," + tn.getLocation().y + " ";
		}
	    System.out.println(s);
    }

    // returns a tile located at specific Euclidean coordinates
    Tile getTile(Point coordinates) {
        return tiles.get(coordinates);
    }
    
    // returns the boolean side (TRUE orc, FALSE men) of the team
    ArrayList<Figure> getTeam(boolean side) {
    	return side ? orcs.getTeam() : humans.getTeam();
    }
    
    // removes a character from a team
    void removeFromTeam(boolean isOrcs, Figure figure) {
       	if (isOrcs)
    		orcs.remove(figure);
    	else 
    		humans.remove(figure);
    }
    
    // I wonder what this does
    void setupSecret(){
		for (Entry<Point, Tile> entry : tiles.entrySet()){
			Tile t = entry.getValue();
    		t.setSecret();
		}
    }
}
