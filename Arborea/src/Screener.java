import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

//
class Screener extends JFrame {

	private final Painter painter;
	private final Texter texter;
	private final Clicker clicker;
	private JButton endTurn;
	
	Screener(String windowName){
		// creates default JFrame object
		super(windowName);

		// when the red x at top right of window is clicked, program ends
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// whether the screen can be resized
		this.setResizable(false);
		
		//DEBUG set start location of window
		this.setLocation(500, 150);
		
		// sets black background for in rare case when area is smaller than window
		this.setBackground(Color.BLACK);

		// panel on which is painted
		painter = new Painter();
		painter.setLayout(new FlowLayout(FlowLayout.CENTER, 0,0));		
		
		// mouse listener, to use within the paint panel
		clicker = new Clicker();
		painter.addMouseListener(clicker);
		painter.addMouseMotionListener(clicker);
		
		// panel on which is written
		texter = new Texter();
		//texter.setLayout(new FlowLayout(FlowLayout.CENTER, 0,0));
		
		// button to end turn
		endTurn = new JButton("End turn");
		add(endTurn);
		HandlerClass handler = new HandlerClass();
		endTurn.addActionListener(handler);
		
		// TODO temp
		String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		texter.write(content);

		// the paint and text panels are added to the window
	    this.getContentPane().add(painter, "West");
	    this.getContentPane().add(texter, "East");

		// makes the window the correct size and displays it
		this.pack();
		this.setVisible(true);
	}
	
	// updates text on the text panel
	void rewrite(){
		texter.write(Arborea.text);
	}
	
	private class HandlerClass implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			//JOptionPane.showMessageDialog(null, String.format("%s", event.getActionCommand()));
			Arborea.turnEnded = true;
		}
	}
	
}