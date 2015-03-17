/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;

// this object is a window that contains a canvas and buttons
@SuppressWarnings("serial")
class Screener extends JFrame {
	
	private static final Font MENU_FONT = new Font("Franklin Gothic Demi", Font.BOLD, 16);

	static final Color MENU_COLOR = new Color(50,1,2);
	static final Color BUTTON_COLOR = new Color(130,130,150);
	static final Color GAME_COLOR = new Color(0,50,100);
	static final Color SECRET_COLOR = new Color(0,0,0);
	
	// objects contained in this window
	private final Painter painter;
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
	
	// all objects are immediately setup in this constructor
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
		
		// button to start the game
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
		
		// button for the intelligence of the Orc team
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
		
		// button for the intelligence of the Men team
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
		
		// button for selecting the type of AI
		menuButtonAI = new JButton("Random AI");
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
		
		// button for selecting which team moves first
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

		// a button to start a new game on the victory screen
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
		
		// I wonder what this does
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

		// buttons are added
		painter.add(startButton);
		painter.add(menuButtonOrcs);
		painter.add(menuButtonMen);
		painter.add(menuButtonAI);
		painter.add(menuButtonFirstMove);
		painter.add(replayButton);
		painter.add(secretButton);
		
		// the paint panel is added to the window
	    this.getContentPane().add(painter, "West");

		// makes the window the correct size and displays it
		this.pack();
		this.setVisible(true);
	}
	
	// sets the color of the canvas background
	void setCanvasBackground(Color color){
		painter.setBackground(color);
	}
	
	// shows or hides buttons on the menu
	void showMenu(boolean bool){
		menuButtonOrcs.setVisible(bool);
		menuButtonMen.setVisible(bool);
		menuButtonAI.setVisible(bool);
		menuButtonFirstMove.setVisible(bool);
		startButton.setVisible(bool);
		secretButton.setVisible(bool);
	}
	
	// shows or hides the turn button
	void showReplayButton(boolean bool){
		this.replayButton.setVisible(bool);
	}
	
	// shows or hides the turn button
	void showTurnButton(boolean bool){
		this.turner.setVisible(bool);
	}
}
