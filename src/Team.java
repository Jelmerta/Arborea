/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.util.ArrayList;

// this object holds and handles allied figures
class Team {
	
	// a list containing all figures in a team
	ArrayList<Figure> figures;

	// list is declared. there is no need for a copy constructor
    Team() {
    	figures = new ArrayList<Figure>();
    }
    
    // adds or removes a character from the team
    void addToTeam(Figure figure) {
    	figures.add(figure);
    }    
    void remove(Figure figure) {
    	figures.remove(figure);
    }
    
    // returns the list of characters
    ArrayList<Figure> getTeam() {
    	return figures;
    }
    
    // returns the amount of characters left in the team
    private int getSize() {
    	return figures.size();
    }
    
    // returns a point based on team positions usable by the AI
    double[] getAverageMiddlePointOfTeam() {
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
