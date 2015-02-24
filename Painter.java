import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map.Entry;

import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Painter extends JPanel {    
	
	Painter() {
		super();
		this.setBackground(new Color(0,50,100));
		
		//this.addMouseListener(new MouseAdapter());
	}
	
	// paints the graphics on the screen. called in Arborea-> run(){ ... repaint(); ...}
	@Override
	public void paintComponent(Graphics g){		
		super.paintComponent(g);
		
		paintTiles(g);
		paintCharacters(g);
	}
	
	// paints all tiles
	private void paintTiles(Graphics g){
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Point pixelCoords = t.getPixelCoords();
			g.drawImage(t.mudImage, pixelCoords.x , pixelCoords.y, this);
		}
		
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Point pixelCoords = t.getPixelCoords();
			g.drawImage(t.image, pixelCoords.x , pixelCoords.y, this);
		}
		
		Tile s = Arborea.selection;
		if (s != null) {
			Point pixelCoords = s.getPixelCoords();
			g.drawImage(s.selectImage, pixelCoords.x, pixelCoords.y,this);
		}
	}
	
	private void paintCharacters(Graphics g) {
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			int currentCharType = t.getCharacterType();
			Point pixelCoords = t.getPixelCoords();
			
			if(currentCharType == 1)
				g.drawImage(Figure.swordImage, pixelCoords.x+10+72, pixelCoords.y-20, -72, 72, this);
			if(currentCharType == 2)
				g.drawImage(Figure.generalImage, pixelCoords.x+10+72, pixelCoords.y-20, -72, 72, this);
			if(currentCharType == 3)
				g.drawImage(Figure.goblinImage, pixelCoords.x+10, pixelCoords.y-20, this);
			if(currentCharType == 4)
				g.drawImage(Figure.orcImage, pixelCoords.x+10, pixelCoords.y-20, this);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Arborea.WINDOW_WIDTH,Arborea.WINDOW_HEIGHT);
	}
	
}