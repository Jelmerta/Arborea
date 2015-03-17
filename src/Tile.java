/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.HashMap;

// this object represents a part of the game-world
class Tile {
	
	// a hexagonal tile is made up of a rectangle and two triangles
	final static int SELECT_RECT = 40;
	final static int SELECT_TRI = 28;
	
	// these images can vary and so require specific get-methods
	private final static BufferedImage tileImage = ArtManager.tile3;
	private final static BufferedImage tileSecret = ArtManager.tile4;
	private final static BufferedImage tileMove = ArtManager.tileMove3;
	private final static BufferedImage tileAttack = ArtManager.tileAttack3;
	private final static BufferedImage mudImage = ArtManager.mud;
	private final static BufferedImage selectImage = ArtManager.select;
	
	// displayed images on the canvas
	private BufferedImage usedTileImage = tileImage;
	BufferedImage image = tileImage;
	
	// the tile keeps track of who is standing on it
    private Figure currentFigure;
    
    // coordinates, both for Euclidean system and in pixels
    private Point coords;
    private Point pixelCoords;
    
    // all neighboring tiles are stored for easy access
    Tile[] neighbours;
    
    // upon constructing a tile, a character is initialized also
    Tile(Point pos, int startType) {
        this.coords = pos;
        this.pixelCoords = calcPixelCoords(pos);
        switch(startType) {
            case Figure.TYPE_NONE:
            	currentFigure = null;
                break;
            case Figure.TYPE_SWORD:
            	currentFigure = new Sword(pos);
                break;
            case Figure.TYPE_GENERAL:
            	currentFigure = new General(pos);
                break;
            case Figure.TYPE_GOBLIN:
            	currentFigure = new Goblin(pos);
                break;
            case Figure.TYPE_ORC:
            	currentFigure = new Orc(pos);
                break;
            default:
            	currentFigure = null;
        }
    }
    
    // copy constructor
    Tile(Tile copy){
    	this.pixelCoords = new Point(copy.coords.y,copy.coords.y);
    	this.coords = new Point(copy.coords.x,copy.coords.y);
    	this.usedTileImage = copy.usedTileImage;
    	this.image = copy.image;
    	
    	if (copy.currentFigure == null)
    		this.currentFigure = null;
    	else {
			switch (copy.currentFigure.type){
				case Figure.TYPE_SWORD:
					this.currentFigure = new Sword(copy.currentFigure);
					break;
				case Figure.TYPE_GENERAL:
					this.currentFigure = new General(copy.currentFigure);
					break;
				case Figure.TYPE_ORC:
					this.currentFigure = new Orc(copy.currentFigure);
					break;
				case Figure.TYPE_GOBLIN:
					this.currentFigure = new Goblin(copy.currentFigure);
					break;
			}
    	}
    }
    
    // calculates the pixel coordinates of the top left point of the title
    private Point calcPixelCoords(Point coords) {
        int padding = (int)(tileImage.getWidth() * 0.3)+3;
        int originX = 800 / 2 - (int)(tileImage.getWidth() /2);
        int originY = 600 / 2 - (int)(tileImage.getHeight() /2);
        int offsetX = (int)(tileImage.getWidth())-padding;
        int offsetY = (int)(tileImage.getHeight())-1;
        
        int pixelCoordX = originX + coords.x * offsetX;
        int pixelCoordY;
        
        if(Math.abs(coords.x) % 2 == 1)
            pixelCoordY = originY - coords.y*offsetY - (coords.x/(Math.abs(coords.x)))*(offsetY/2) - (coords.x/2)*offsetY;
        else
            pixelCoordY = originY - coords.y*offsetY - (coords.x/2)*offsetY;
        return new Point(pixelCoordX, pixelCoordY);
    }
    
    // finds and stores all neighbors for the tile
    void calculateNeighbours(HashMap<Point, Tile> tiles){
        this.neighbours = new Tile[6];
        
        Point dl = new Point(coords.x-1, coords.y);
        Point ul = new Point(coords.x-1, coords.y+1);
        Point u = new Point(coords.x, coords.y+1);
        Point ur = new Point(coords.x+1, coords.y);
        Point dr = new Point(coords.x+1, coords.y-1);
        Point d = new Point(coords.x, coords.y-1);
        
        if (tiles.containsKey(dl))
            this.neighbours[0] = tiles.get(dl);
        else neighbours[0] = null;
        
        if (tiles.containsKey(ul))
            this.neighbours[1] = tiles.get(ul);
        else neighbours[1] = null;
        
        if (tiles.containsKey(u))
            this.neighbours[2] = tiles.get(u);
        else neighbours[2] = null;
        
        if (tiles.containsKey(ur))
            this.neighbours[3] = tiles.get(ur);
        else neighbours[3] = null;
        
        if (tiles.containsKey(dr))
            this.neighbours[4] = tiles.get(dr);
        else neighbours[4] = null;
        
        if (tiles.containsKey(d))
            this.neighbours[5] = tiles.get(d);
        else neighbours[5] = null;
    }
    
    // returns whether or not a character is standing on the tile
    boolean hasFigure() {
        return currentFigure != null;
    }

    // returns the index of the type of the character on the tile
    int getCharacterType() {
        if(currentFigure != null)
        	return currentFigure.returnType();
        else
        	return 0;
    }
    
    // returns the character standing on the tile
    Figure getFigure() {
    	return currentFigure;
    }

    // places a character on the tjle
    void setFigure(Figure figure) {
    	currentFigure = figure;
    }
    
    // restores images of selected tiles neighbors
    void restoreNeighbourImages(){
        for (int i = 0; i < 6; i++){
        	if (this.neighbours[i] != null)
        		this.neighbours[i].image = usedTileImage;
        }
    }
    
    // changes images of selected tiles neighbors to their alternative
    void changeNeighbourImages(Grid grid){
        for (int i = 0; i < 6; i++){
        	if (this.neighbours[i] != null) {
        		if (!this.neighbours[i].hasFigure()) {
        			if (this.getFigure().hasMovesLeft())
        				this.neighbours[i].image = tileMove;
        		} else {
        			// if (figure .belongsTo -> opposite team)
        			// this.neighbours[i].image = tileAttack;
        			if (this.getFigure().hasAttacksLeft()) {
	        			if (this.neighbours[i].hasFigure()) {
	        			    Figure neighbourFigure = this.neighbours[i].getFigure(); 
	        				if (!grid.getTeam(neighbourFigure.getTeam()).contains(currentFigure))
	        					this.neighbours[i].image = tileAttack;
	        			}
        			}
        		}
        	}
        }
    }
    
    // returns the Euclidean coordinates of the tile
    Point getLocation() {
    	return coords;
    }
    
    // returns the pixel coordinates of top left corner over tile
    Point getPixelCoords() {
        return pixelCoords;
    }
    
    // returns an image representing the tile
    BufferedImage getImage(){
    	return image;
    }
    
    // returns an image for the base of the tile
    BufferedImage getMudImage(){
    	return mudImage;
    }
    
    // returns an image used when selecting a tile
    BufferedImage getSelectImage(){
    	return selectImage;
    }
    
    // I wonder what this does
    void setSecret(){
    	if (Arborea.enterTheMatrix)
    		usedTileImage = tileSecret;
    	else 
    		usedTileImage = tileImage;
		image = usedTileImage;
    }
	
	// returns a String representation of the tile
	@Override
	public String toString(){
		String s = "T(" + coords.x + "," + coords.y + ")";
		
		if (currentFigure != null)
			s += " " + currentFigure.toString();
		
		return s;
	}
}
