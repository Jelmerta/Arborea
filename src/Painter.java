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

@SuppressWarnings("serial")
class Painter extends JPanel {    
	Grid gridToDraw;
	Painter(Grid grid) {
		super();

		//this.setBackground(new Color(0,50,100));
		this.setBackground(Color.BLACK);
		gridToDraw = grid;
		//this.addMouseListener(new MouseAdapter());
	}
	
	// paints the graphics on the screen. called in Arborea-> run(){ ... repaint(); ...}
	@Override
	public void paintComponent(Graphics g){		
		super.paintComponent(g);
		
		paintSecret(g);
		
		if (Arborea.browsingMenu){
			paintMenu(g, gridToDraw);
			
		} else {
			paintTiles(g, gridToDraw);
			paintCharacters(g, gridToDraw);
			paintOverlay(g);
		}
	}
	
	// paints the menu
	private void paintMenu(Graphics g, Grid grid){
		// end of game
		if (Arborea.gameOver){
			if (grid.getTeam(Arborea.ORCTEAM).isEmpty()) {
				g.drawImage(ArtManager.menuMenVictory,0,0,this);
			} else if (grid.getTeam(Arborea.MENTEAM).isEmpty()){
				g.drawImage(ArtManager.menuOrcsVictory,0,0,this);
			}
			
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
	private void paintTiles(Graphics g, Grid grid){

		if (!Arborea.enterTheMatrix){ 
			for (Entry<Point, Tile> entry : grid.tiles.entrySet()){
				Tile t = entry.getValue();
				Point pixelCoords = t.getPixelCoords();
				g.drawImage(t.getMudImage(), pixelCoords.x , pixelCoords.y, this);
			}
		}
		
		for (Entry<Point, Tile> entry : grid.tiles.entrySet()){
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
	
	private void paintCharacters(Graphics g, Grid grid) {
		for (Entry<Point, Tile> entry : grid.tiles.entrySet()){
			Tile t = entry.getValue();
			Point pixelCoords = t.getPixelCoords();
			
			Figure currentFigure = t.getFigure();
			float figureHealth, figureStartHP;
			int drawnPixels; 
			// TODO direction
			if (currentFigure == null) continue;
			g.drawImage(currentFigure.getStandSprite(), pixelCoords.x+10, pixelCoords.y-20,this);
				
			if (t.hasFigure() && t.getFigure().hasMovesLeft()){
				g.drawImage(ArtManager.iconMove, pixelCoords.x + 25, pixelCoords.y, this);
			}
			if (t.hasFigure() && t.getFigure().hasAttacksLeft()){
				g.drawImage(ArtManager.iconAttack, pixelCoords.x + 65, pixelCoords.y - 10, this);
			}
			if(t.hasFigure()) {
				figureHealth = currentFigure.getHitpoints();
				figureStartHP = currentFigure.getStartHitpoints();
				drawnPixels = Math.round((figureHealth/figureStartHP)*50);
				//System.out.println("drawn pixels: " + figureHealth + " " + figureStartHP + " " + drawnPixels);
				g.drawImage(ArtManager.iconHealthbar, pixelCoords.x + 20, pixelCoords.y + 40, this); // ik vind onder toch nog best wel lelijk
				// max width of healthbarGreen is 50, minimum 0 (figure disappears)
				g.drawImage(ArtManager.iconHealthbarGreen, pixelCoords.x + 20 + 1, pixelCoords.y + 40 + 1, drawnPixels, 3, this); //get new image using percentages
			}		
		}
	}
	
	// paints overlay images
	private void paintOverlay(Graphics g){

		//g.drawImage(ArtManager.overlayTurn, 30, 20, this);
		if (Arborea.currentTeamIsOrcs){
			g.drawImage(ArtManager.overlayOrcs, 80, 20, this);
		} else {
			g.drawImage(ArtManager.overlayMen, 80, 20, this);
		}
		
		if (Arborea.muteSound){
			g.drawImage(ArtManager.overlayMute, 700, 20, this);			
		} else {
			g.drawImage(ArtManager.overlaySound, 700, 20, this);
		}
	}
	
	void paintSecret(Graphics g){
		if (!Arborea.browsingMenu && Arborea.enterTheMatrix)
			g.drawImage(new ImageIcon("src/art/Matrix.gif").getImage(),0,0,800,600,this);
			//g.drawImage(new ImageIcon("src/art/Matrix_Code.gif").getImage(),0,0,800,600,this);	
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Arborea.WINDOW_WIDTH,Arborea.WINDOW_HEIGHT);
	}
	
}
