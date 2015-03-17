/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

// Toevoegen wordt nu al gedaan en er wordt bijgehouden in welk team de figures zitten, moet deze file dan alsnog wel?

import java.awt.Point;
import java.util.ArrayList;

class Team {
	
	ArrayList<Figure> figures;
	int size = 0;

    Team() {
    	figures = new ArrayList<Figure>();
    }
    
    public void addToTeam(Figure figure, int index) {
    	figure.setIndex(index);
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
    
    public void update(int index, Figure updatedFigure) {
    	figures.set(index, updatedFigure);
    }
    
    public double[] getAverageMiddlePointOfTeam() {
    	double x = 0;
    	double y = 0;
    	double[] teamMiddlePoint = new double[2];
    	for(Figure currentFigure : figures) {
    		Point currentFigurePoint = currentFigure.getLocation();
    		x += currentFigurePoint.getX();
    		y += currentFigurePoint.getY();
    	}
    	teamMiddlePoint[0] = x/getSize();
    	teamMiddlePoint[1] = y/getSize();
    	return teamMiddlePoint;
    }
}