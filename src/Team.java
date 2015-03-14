/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

// Toevoegen wordt nu al gedaan en er wordt bijgehouden in welk team de figures zitten, moet deze file dan alsnog wel?

import java.awt.Point;
import java.util.ArrayList;

class Team {
	
	ArrayList<Figure> figures;
	//boolean side; TODO never used
	int size = 0;

    Team() {
    	figures = new ArrayList<Figure>();
    }
    
    public void addToTeam(Figure figure) {
    	figures.add(figure);
    	size++;
    }
    
    public void remove(Figure figure) {
    	figures.remove(figure);
    	size--;
    }
    
    public ArrayList<Figure> getTeam() {
    	return figures;
    }
    
    private int getSize() {
    	return this.size;
    }
    
    public Point getAverageMiddlePoint() {
    	int x = 0;
    	int y = 0;
    	for(Figure currentFigure : figures) {
    		Point currentFigurePoint = currentFigure.getLocation();
    		x += (int)currentFigurePoint.getX();
    		y += (int)currentFigurePoint.getY();
    	}
    	return new Point(x/getSize(), y/getSize());
    }
}
/*    boolean teamIsOrcs; TODO weghalen?
    ArrayList<Figure> figures;
    
    // sets up a team, depending on various factors
    Team(boolean isOrcTeam){
        
        teamIsOrcs = isOrcTeam;
        figures = new ArrayList<Figure>();
        
        if (teamIsOrcs){
            for (int i =0; i < Arborea.AMOUNT_GOBLIN; i++){
                figures.add(new Goblin());
            }
            for (int i =0; i < Arborea.AMOUNT_ORC; i++){
                figures.add(new Orc());
            }
            
        } else {
            for (int i =0; i < Arborea.AMOUNT_SWORD; i++){
                figures.add(new Sword());
            }
            for (int i =0; i < Arborea.AMOUNT_GENERAL; i++){
                figures.add(new General());
            }
        }
    }
}*/