/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

// Toevoegen wordt nu al gedaan en er wordt bijgehouden in welk team de figures zitten, moet deze file dan alsnog wel?

import java.awt.Point;
import java.util.ArrayList;

class Team {
	
	ArrayList<Figure> figures;

    Team() {
    	figures = new ArrayList<Figure>();
    }
    
    // copy constructor
    Team(Team copy){
    	figures = new ArrayList<Figure>();
    	for (Figure f : copy.figures){
    		switch (f.type){
    			case Figure.TYPE_SWORD:
    				figures.add(new Sword(f));
    				break;
    			case Figure.TYPE_GENERAL:
    				figures.add(new General(f));
    				break;
    			case Figure.TYPE_ORC:
    				figures.add(new Orc(f));
    				break;
    			case Figure.TYPE_GOBLIN:
    				figures.add(new Goblin(f));
    				break;
    		}
    	}
    }
    
    public void addToTeam(Figure figure) {
    	figures.add(figure);
    }
    
    public void remove(Figure figure) {
    	figures.remove(figure);
    }
    
    public ArrayList<Figure> getTeam() {
    	return figures;
    }
    
    private int getSize() {
    	return figures.size();
    }
    
    public void update(int index, Figure updatedFigure) {
    	figures.set(index, updatedFigure);
    }
    
    public double[] getAverageMiddlePointOfTeam() {
    	double x = 0;
    	double y = 0;
    	double[] teamMiddlePoint = new double[2];
    	for(Figure currentFigure : figures) {
    		if(currentFigure != null) {
	    		Point currentFigurePoint = currentFigure.getLocation();
	    		x += currentFigurePoint.getX();
	    		y += currentFigurePoint.getY();
    		}
    	}
    	teamMiddlePoint[0] = x/getSize();
    	teamMiddlePoint[1] = y/getSize();
    	return teamMiddlePoint;
    }
}
