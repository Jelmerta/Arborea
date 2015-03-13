/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Texter extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	JTextArea ta;
	
	Texter(){
		ta = new JTextArea();
		ta.setColumns(17);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
        
		ta.setEditable(false);
		ta.setFocusable(false);
		ta.setHighlighter(null);
		ta.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane scroll = new JScrollPane (ta, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				   
		this.add(scroll);
		//this.add(ta);
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	void write(String text){
		ta.setText(text);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Arborea.WINDOW_WIDTH/4,Arborea.WINDOW_HEIGHT);
	}
}
