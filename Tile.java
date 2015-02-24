import java.awt.image.BufferedImage;

import java.awt.Point;
import java.util.HashMap;

class Tile {
	
	private final static BufferedImage tileImage = ArtManager.tile3;
	private final static BufferedImage tileMove = ArtManager.tileMove3;
	final static BufferedImage mudImage = ArtManager.mud;
	final static BufferedImage selectImage = ArtManager.select;
	
	BufferedImage image = tileImage;
	
    //Point[] destinations;
    private Figure currentFigure;
    
    Tile[] neighbours;
    
    // coordinates
    Point coords;
    Point pixelCoords;
    Point middle;
    
    // use alternative tile image when in selection radius
    boolean inRadius = false;
    
    // coordinates for painting ?
    // note that if index (not coordinate) of X is odd,
    // coordinate y should be + tilImage.getHeight()/2
    // although that CAN be done in paintComponents, that
    // will mean it has to calculate everytime.
    // preferablly it can be calculated once
    int x, y;
    
    // so this does mean we need indexes ?
    //int ix, iy;
    
    // if ix % 2 == 1, y += (int)(tileImage.getHeight()/2)
    
    Tile(){
        this.coords = new Point(0,0);
        this.neighbours = new Tile[6];
    }
    
    // deze is ws het verstandigst, of eentje met destinations
    Tile(Point pos){
        this.coords = pos;
        this.pixelCoords = calcPixelCoords(pos);
        
        this.middle = calcMiddlePoint();
    }
    //Tile(Point pos, Point[] dests){
    //    this.coords = pos;
    //    this.neighbours = dests;
    //}
    
    Tile(Point pos, Figure tileFigure){
        this.coords = pos;
        this.pixelCoords = calcPixelCoords(pos);
        this.currentFigure = tileFigure;
    }
    
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
            	System.out.println("An error has occurred");
                System.exit(1);
        }
        
        this.middle = calcMiddlePoint();
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
    
    // calculates the pixel coordinates of the middle of the tile
    private Point calcMiddlePoint(){
    	return new Point(pixelCoords.x + tileImage.getWidth() /2, pixelCoords.y + tileImage.getHeight() /2);
    }
    
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
    
    public Tile[] getNeighbours() {
        return neighbours;
    }
    
    public Point getPixelCoords() {
        return pixelCoords;
    }
    
    public boolean hasFigure() {
        return currentFigure != null;
    }

    public int getCharacterType() {
        if(currentFigure != null)
        	return currentFigure.returnType();
        else
        	return 0;
    }
    
    public Figure getFigure() {
    	return currentFigure;
    }

    void setFigure(Figure figure) {
    	currentFigure = figure;
    }
    
    // restores images of selected tiles neighbors
    void restoreNeighbourImages(){
        for (int i = 0; i < 6; i++){
        	if (this.neighbours[i] != null)
        		this.neighbours[i].image = tileImage;
        }
    }
    
    // changes images of selected tiles neighbors to their alternative
    void changeNeighbourImages(){
        for (int i = 0; i < 6; i++){
        	if (this.neighbours[i] != null)
        		if (!this.neighbours[i].hasFigure())
        			this.neighbours[i].image = tileMove;
        		else {
        			// if (figure .belongsTo -> opposite team)
        			// this.neighbours[i].image = tileAttack;
        		}
        }
    }
    
    public Point getLocation() {
    	return coords;
    }
	
	// returns string representation of tile
	@Override
	public String toString(){
		String s = "T(" + coords.x + "," + coords.y + ")";
		
		if (currentFigure != null)
			s += " " + currentFigure.toString();
		
		return s;
	}
}