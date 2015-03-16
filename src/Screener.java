/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;

//
@SuppressWarnings("serial")
class Screener extends JFrame {
	
	private final Painter painter;
	private final Texter texter;
	private final Clicker clicker;
	private final JButton turner;

	private final JButton menuButton1;
	private final JButton menuButton2;
	private final JButton menuButton3;
	private final JButton menuButton4;
	private final JButton menuButton5;
	
	private final JButton replayButton;
	
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
		painter.setLayout(null); // button won't position if there is a layout
		
		// mouse listener, to use within the paint panel
		clicker = new Clicker();
		painter.addMouseListener(clicker);
		painter.addMouseMotionListener(clicker);

		// button to end turn
		turner = new JButton("End turn");
		turner.setBackground(new Color(210,190,190));
		turner.setBounds(680,540,87,30);
		turner.setVisible(false);
		turner.setBorderPainted(false);
		turner.setFocusPainted(false);
		turner.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 12));
		
		turner.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.turnEnded = true;
			}
		});
		painter.add(turner);
		
		// panel on which is written
		texter = new Texter();
		
		// TODO temp
		String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		texter.write(content);
		
		// buttons for in the title screen menu
		menuButton1 = new JButton("<html>Orcs - AI<br />Men - Player</html>");
		menuButton1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.menu.menuOption = Menu.ORC_AI_MEN_PI;
			}
		});
		menuButton1.setBounds(0,0,400,280);
		menuButton1.setVisible(false);
		menuButton1.setBackground(new Color(160,160,180));
		menuButton1.setBorderPainted(false);
		menuButton1.setFocusPainted(false);
		menuButton1.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		menuButton2 = new JButton("<html>Orcs - Player<br />Men - AI</html>");
		menuButton2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.menu.menuOption = Menu.ORC_PI_MEN_AI;
			}
		});
		menuButton2.setBounds(400,0,400,280);
		menuButton2.setVisible(false);
		menuButton2.setBackground(new Color(160,160,180));
		menuButton2.setBorderPainted(false);
		menuButton2.setFocusPainted(false);
		menuButton2.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		menuButton3 = new JButton("<html>Orcs - Player<br />Men - Player</html>");
		menuButton3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.menu.menuOption = Menu.ORC_PI_MEN_PI;
			}
		});
		menuButton3.setBounds(0,320,400,280);
		menuButton3.setVisible(false);
		menuButton3.setBackground(new Color(160,160,180));
		menuButton3.setBorderPainted(false);
		menuButton3.setFocusPainted(false);
		menuButton3.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		menuButton4 = new JButton("<html>Orcs - AI<br />Men - AI</html>");
		menuButton4.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.menu.menuOption = Menu.ORC_AI_MEN_AI;
			}
		});
		menuButton4.setBounds(400,320,400,280);
		menuButton4.setVisible(false);
		menuButton4.setBackground(new Color(160,160,180));
		menuButton4.setBorderPainted(false);
		menuButton4.setFocusPainted(false);
		menuButton4.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		menuButton5 = new JButton("Orcs have First Turn");
		menuButton5.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.currentTeamIsOrcs = !Arborea.currentTeamIsOrcs;
				
				if (Arborea.currentTeamIsOrcs)
					menuButton5.setText("Orcs have First Turn");
				else
					menuButton5.setText("Men have First Turn");					
			}
		});
		menuButton5.setBounds(0,280,800,40);
		menuButton5.setVisible(false);
		menuButton5.setBackground(new Color(160,160,180));
		menuButton5.setFocusPainted(false);
		menuButton5.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		painter.add(menuButton1);
		painter.add(menuButton2);
		painter.add(menuButton3);
		painter.add(menuButton4);
		painter.add(menuButton5);
		
		
		replayButton = new JButton("New Game");
		replayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.playAgain = true;				
			}
		});
		replayButton.setBounds(340,550,120,30);
		replayButton.setVisible(false);
		replayButton.setBackground(new Color(160,160,180));
		replayButton.setBorderPainted(false);
		replayButton.setFocusPainted(false);
		replayButton.setFont(new Font("Franklin Gothic Demi", Font.BOLD, 16));
		
		painter.add(replayButton);
		
		// the paint and text panels are added to the window
	    this.getContentPane().add(painter, "West");
	    //this.getContentPane().add(texter, "East");

		// makes the window the correct size and displays it
		this.pack();
		this.setVisible(true);
	}
	
	// updates text on the text panel
	void rewrite(){
		texter.write(Arborea.text);
	}
	
	// sets the color of the canvas background
	void initCanvasBackground(){
		setCanvasBackground(new Color(0,50,100));
	}
	
	// sets the color of the canvas background
	void setCanvasBackground(Color color){
		painter.setBackground(color);
	}
	
	void showMenu(boolean bool){
		menuButton1.setVisible(bool);
		menuButton2.setVisible(bool);
		menuButton3.setVisible(bool);
		menuButton4.setVisible(bool);
		menuButton5.setVisible(bool);
	}
	
	// shows the turn button
	void showReplayButton(boolean bool){
		this.replayButton.setVisible(bool);
	}
	
	// shows the turn button
	void showTurnButton(boolean bool){
		this.turner.setVisible(bool);
	}
}