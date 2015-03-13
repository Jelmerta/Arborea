/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Clicker implements MouseListener, MouseMotionListener {
    
	// button has been pressed, not necessarily released
	@Override
    public void mousePressed(MouseEvent e){
		Arborea.lastClickPoint.x = e.getX();
		Arborea.lastClickPoint.y = e.getY();
		if (e.getButton() == MouseEvent.BUTTON1)
			Arborea.leftClicked = true;
		else if (e.getButton() == MouseEvent.BUTTON3)
			Arborea.rightClicked = true;
	}

	// button has been pressed and released
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	// button was pressed and is released
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	// when mouse enters component
	@Override
	public void mouseEntered(MouseEvent e) {}

	// when mouse leaves component
	@Override
	public void mouseExited(MouseEvent e) {}

    // when mouse is moved
	@Override
	public void mouseMoved(MouseEvent e) {
	    Arborea.mousePoint.x = e.getX();
	    Arborea.mousePoint.y = e.getY();
	}

    // when mouse is pressed (not released yet) and moved
	@Override
	public void mouseDragged(MouseEvent e) {}
	
}