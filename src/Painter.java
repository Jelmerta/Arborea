/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

// this object paints everything on the canvas
@SuppressWarnings("serial")
class Painter extends JPanel {
	
	// the initial screen is black for the intro animation
	Painter() {
		super();
		this.setBackground(Color.BLACK);
	}
	
	// paints the graphics on the screen. called in Arborea-> run(){ ... repaint(); ...}
	@Override
	public void paintComponent(Graphics g){		
		super.paintComponent(g);
		
		if (Arborea.browsingMenu) paintMenu(g);
		else {
			paintSecret(g);
			paintTiles(g);
			paintCharacters(g);
			paintOverlay(g);
		}
	}
	
	// paints the menu
	private void paintMenu(Graphics g){
		// end of game
		if (Arborea.gameOver){
			if (Arborea.grid.getTeam(Arborea.ORCTEAM).isEmpty()) {
				g.drawImage(ArtManager.menuMenVictory,0,0,this);
			} else if (Arborea.grid.getTeam(Arborea.MENTEAM).isEmpty()){
				g.drawImage(ArtManager.menuOrcsVictory,0,0,this);
			}
			g.drawImage(ArtManager.menuVictory,0,400,this);
			
		}
		// start of game
		else {
			if (!Arborea.introduced){
				g.drawImage(Arborea.menu.getMenuIntroImage(),0,0,this);
			} else {
				g.drawImage(ArtManager.menuArborea,0,10,this);
				g.drawImage(ArtManager.menuOrcsStart,50,180,this);
				g.drawImage(ArtManager.menuMenStart,450,180,this);
			}
		}
	}
	
	// paints all tiles
	private void paintTiles(Graphics g){
		if (!Arborea.enterTheMatrix){ 
			for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
				Tile t = entry.getValue();
				Point pixelCoords = t.getPixelCoords();
				g.drawImage(t.getMudImage(), pixelCoords.x , pixelCoords.y, this);
			}
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

	// paints characters, as well as remaining actions and health
	private void paintCharacters(Graphics g) {
		for (Entry<Point, Tile> entry : Arborea.grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Figure currentFigure = t.getFigure();
			
			// ignore tiles that hold no figure
			if (currentFigure == null) continue;

			Point pixelCoords = t.getPixelCoords();
			float figureHealth, figureStartHP;
			int drawnPixels;
			
			g.drawImage(currentFigure.getStandSprite(), pixelCoords.x+10, pixelCoords.y-20,this);
				
			if (currentFigure.hasMovesLeft())
				g.drawImage(ArtManager.iconMove, pixelCoords.x + 25, pixelCoords.y, this);
			if (currentFigure.hasAttacksLeft())
				g.drawImage(ArtManager.iconAttack, pixelCoords.x + 65, pixelCoords.y - 10, this);

			figureHealth = currentFigure.getHitpoints();
			figureStartHP = currentFigure.getStartHitpoints();
			drawnPixels = Math.round((figureHealth/figureStartHP)*50);
			g.drawImage(ArtManager.iconHealthbar, pixelCoords.x + 20, pixelCoords.y + 40, this);
			g.drawImage(ArtManager.iconHealthbarGreen, pixelCoords.x + 20 + 1, pixelCoords.y + 40 + 1, drawnPixels, 3, this);
		}
	}
	
	// paints overlay images
	private void paintOverlay(Graphics g){
		if (Arborea.currentTeamIsOrcs)
			g.drawImage(ArtManager.overlayOrcs, 80, 20, this);
		else
			g.drawImage(ArtManager.overlayMen, 80, 20, this);
		
		if (Arborea.muteSound)
			g.drawImage(ArtManager.overlayMute, 700, 20, this);			
		else
			g.drawImage(ArtManager.overlaySound, 700, 20, this);
	}

	// paints the secret background
	void paintSecret(Graphics g){
		if (Arborea.enterTheMatrix)
			//g.drawImage(new ImageIcon("art/Matrix.gif").getImage(),0,0,800,600,this); // console
			g.drawImage(new ImageIcon("src/art/Matrix.gif").getImage(),0,0,800,600,this); // eclipse
	}
	
	// returns the size of the canvas
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Arborea.WINDOW_WIDTH,Arborea.WINDOW_HEIGHT);
	}	
}