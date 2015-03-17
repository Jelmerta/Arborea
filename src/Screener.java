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

	static final Color MENU_COLOR = new Color(50,1,2);
	static final Color BUTTON_COLOR = new Color(130,130,150);
	static final Color GAME_COLOR = new Color(0,50,100);
	static final Color MATRIX_COLOR = new Color(0,0,0);
	
	private static final Font MENU_FONT = new Font("Franklin Gothic Demi", Font.BOLD, 16);
	
	private final Painter painter;
	private final Texter texter;
	private final Clicker clicker;
	
	// buttons for turn ending, menu options and replay
	private final JButton turner;
	private final JButton menuButtonOrcs;
	private final JButton menuButtonMen;
	private final JButton menuButtonAI;
	private final JButton menuButtonFirstMove;
	private final JButton startButton;
	private final JButton secretButton;
	private final JButton replayButton;
	
	Screener(String windowName, Grid grid){
		
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
		painter = new Painter(grid);
		painter.setLayout(null); // button won't position if there is a layout

		
		// mouse listener, to use within the paint panel
		clicker = new Clicker();
		painter.addMouseListener(clicker);
		painter.addMouseMotionListener(clicker);

		// button to end turn
		turner = new JButton("End turn");
		turner.setBackground(new Color(210,190,190));
		turner.setBounds(660,540,112,30);
		turner.setVisible(false);
		turner.setBorderPainted(false);
		turner.setFocusPainted(false);
		turner.setFont(MENU_FONT);
		
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
		
		startButton = new JButton("Start Game");
		startButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.finishedMenu = true;
			}
		});
		startButton.setBounds(330,475,140,30);
		startButton.setBackground(BUTTON_COLOR);
		startButton.setVisible(false);
		startButton.setFocusPainted(false);
		startButton.setFont(MENU_FONT);
		
		// buttons for in the title screen menu
		menuButtonOrcs = new JButton("AI");
		menuButtonOrcs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.orcsIsAI = !Arborea.orcsIsAI;
				if (Arborea.orcsIsAI)
					menuButtonOrcs.setText("AI");
				else
					menuButtonOrcs.setText("Player");
			}
		});
		menuButtonOrcs.setBounds(155,435,90,30);
		menuButtonOrcs.setBackground(BUTTON_COLOR);
		menuButtonOrcs.setVisible(false);
		menuButtonOrcs.setFocusPainted(false);
		menuButtonOrcs.setFont(MENU_FONT);
		
		menuButtonMen = new JButton("Player");
		menuButtonMen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.menIsAI = !Arborea.menIsAI;
				if (Arborea.menIsAI)
					menuButtonMen.setText("AI");
				else
					menuButtonMen.setText("Player");
			}
		});
		menuButtonMen.setBounds(555,435,90,30);
		menuButtonMen.setBackground(BUTTON_COLOR);
		menuButtonMen.setVisible(false);
		menuButtonMen.setFocusPainted(false);
		menuButtonMen.setFont(MENU_FONT);
		
		menuButtonAI = new JButton("True AI");
		menuButtonAI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int tempIndex = Arborea.indexAI +1;
				if (tempIndex > 1) tempIndex = 0;
				Arborea.indexAI = tempIndex;
				switch (Arborea.indexAI){
					case 0:
						menuButtonAI.setText("Random AI"); 
						break;
					case 1:
						menuButtonAI.setText("True AI");
						break;
				}
			}
		});
		menuButtonAI.setBounds(0,560,378,40);
		menuButtonAI.setBackground(BUTTON_COLOR);
		menuButtonAI.setVisible(false);
		menuButtonAI.setFocusPainted(false);
		menuButtonAI.setFont(MENU_FONT);		
		
		menuButtonFirstMove = new JButton("Orcs have First Turn");
		menuButtonFirstMove.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.currentTeamIsOrcs = !Arborea.currentTeamIsOrcs;
				
				if (Arborea.currentTeamIsOrcs)
					menuButtonFirstMove.setText("Orcs have First Turn");
				else
					menuButtonFirstMove.setText("Men have First Turn");					
			}
		});
		menuButtonFirstMove.setBounds(378,560,378,40);
		menuButtonFirstMove.setBackground(BUTTON_COLOR);
		menuButtonFirstMove.setVisible(false);
		menuButtonFirstMove.setFont(MENU_FONT);

		painter.add(startButton);
		painter.add(menuButtonOrcs);
		painter.add(menuButtonMen);
		painter.add(menuButtonAI);
		painter.add(menuButtonFirstMove);
		
		replayButton = new JButton("New Game");
		replayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.playAgain = true;				
			}
		});
		replayButton.setBounds(340,550,120,30);
		replayButton.setVisible(false);
		replayButton.setBackground(BUTTON_COLOR);
		replayButton.setBorderPainted(false);
		replayButton.setFocusPainted(false);
		replayButton.setFont(MENU_FONT);
		
		painter.add(replayButton);
		
		secretButton = new JButton("?");
		secretButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Arborea.enterTheMatrix = !Arborea.enterTheMatrix;
				
				if (Arborea.enterTheMatrix){
					secretButton.setText("!");
				} else {
					secretButton.setText("?");
				}
			}
		});
		secretButton.setBounds(756,560,44,40);
		secretButton.setBackground(BUTTON_COLOR);
		secretButton.setVisible(false);
		secretButton.setFocusPainted(false);
		secretButton.setFont(MENU_FONT);
		
		painter.add(secretButton);
		
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
	void setCanvasBackground(Color color){
		painter.setBackground(color);
	}
	
	void showMenu(boolean bool){
		menuButtonOrcs.setVisible(bool);
		menuButtonMen.setVisible(bool);
		menuButtonAI.setVisible(bool);
		menuButtonFirstMove.setVisible(bool);
		startButton.setVisible(bool);
		secretButton.setVisible(bool);
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
