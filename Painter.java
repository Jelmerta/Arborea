/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map.Entry;

import javax.swing.JPanel;

class Painter extends JPanel {    
	
	private static final long serialVersionUID = 1L;

	Painter() {
		super();
		this.setBackground(new Color(0,50,100));
		
		//this.addMouseListener(new MouseAdapter());
	}
	
	// paints the graphics on the screen. called in Arborea-> run(){ ... repaint(); ...}
	@Override
	public void paintComponent(Graphics g){		
		super.paintComponent(g);
		
		// TODO check for menu
		
		paintTiles(g);
		paintCharacters(g);
	}
	
	// paints all tiles
	private void paintTiles(Graphics g){

		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Point pixelCoords = t.getPixelCoords();
			g.drawImage(t.getMudImage(), pixelCoords.x , pixelCoords.y, this);
		}
		
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Point pixelCoords = t.getPixelCoords();
			g.drawImage(t.getImage(), pixelCoords.x , pixelCoords.y, this);
		}
		
		Tile s = Arborea.selection;
		if (s != null) {
			Point pixelCoords = s.getPixelCoords();
			g.drawImage(s.getSelectImage(), pixelCoords.x, pixelCoords.y,this);
		}
	}
	
	private void paintCharacters(Graphics g) {
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			//int currentCharType = t.getCharacterType(); TODO unused?
			Point pixelCoords = t.getPixelCoords();
			
			Figure currentFigure = t.getFigure();
			
			// TODO maak eleganter (geen dubbele code)
			// 	-mogelijk door abstract functie in figure
			//	-returnt plaatje , misschien ander returnt x,y
			
			if (currentFigure == null) continue;
			g.drawImage(currentFigure.getStandSprite(), pixelCoords.x+10, pixelCoords.y-20,this);
			
			/*
			if(currentCharType == 1)
				g.drawImage(currentFigure.getStandSprite(), pixelCoords.x+10+72, pixelCoords.y-20, -72, 72, this);
			if(currentCharType == 2)
				g.drawImage(Figure.generalImage, pixelCoords.x+10+72, pixelCoords.y-20, -72, 72, this);
			if(currentCharType == 3)
				g.drawImage(Figure.goblinImage, pixelCoords.x+10, pixelCoords.y-20, this);
			if(currentCharType == 4)
				g.drawImage(Figure.orcImage, pixelCoords.x+10, pixelCoords.y-20, this);
			*/
			if (t.hasFigure() && t.getFigure().hasMovesLeft()){
				g.drawImage(Figure.iconMove, pixelCoords.x + 25, pixelCoords.y, this);
			}
			if (t.hasFigure() && t.getFigure().hasAttacksLeft()){
				g.drawImage(Figure.iconAttack, pixelCoords.x + 65, pixelCoords.y - 10, this);
			}
			
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Arborea.WINDOW_WIDTH,Arborea.WINDOW_HEIGHT);
	}
	
}