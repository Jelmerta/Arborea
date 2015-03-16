/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.image.BufferedImage;

class Menu {
	
	final static int ORC_PI_MEN_PI = 1;
	final static int ORC_PI_MEN_AI = 2;
	final static int ORC_AI_MEN_PI = 3;
	final static int ORC_AI_MEN_AI = 4;
	
	Animator introAnimator;
	Thread animationThread;
	
	int menuOption;
	
    Menu(){
        prepareIntro();
        menuOption = -1;
    }
    
    void showOptions(){
    	
    }

    // the end number of the animator is higher than amount frames
    // this is to ensure the other thread can access it
	void prepareIntro() {
		introAnimator = new Animator(35,75);
		animationThread = new Thread(introAnimator);
		animationThread.start();
	}
	
	boolean finishedIntro(){
		if (introAnimator.getAnimationIndex() >= 29){
			animationThread.interrupt();
			introAnimator = null;
			return true;
		} else {
			return false;
		}
	}
	
	// returns an image for a frame in the introduction scene
	BufferedImage getMenuIntroImage(){
		switch (introAnimator.getAnimationIndex()){
			case 2: return ArtManager.menuIntroFade2;
			case 3: return ArtManager.menuIntroFade3;
			case 4: return ArtManager.menuIntroFade4;
			case 5: return ArtManager.menuIntroFade5;
			case 6: return ArtManager.menuIntroFade6;
			case 7: return ArtManager.menuIntroFade7;
			case 8: return ArtManager.menuIntroFade8;
			case 9: return ArtManager.menuIntroFade9;
			case 10: case 11: case 12: case 13: case 14:
			case 15: case 16: case 17: case 18: case 19:
			case 20: return ArtManager.menuIntro;
			case 21: return ArtManager.menuIntroFade9;
			case 22: return ArtManager.menuIntroFade8;
			case 23: return ArtManager.menuIntroFade7;
			case 24: return ArtManager.menuIntroFade6;
			case 25: return ArtManager.menuIntroFade5;
			case 26: return ArtManager.menuIntroFade4;
			case 27: return ArtManager.menuIntroFade3;
			case 28: return ArtManager.menuIntroFade2;
			default: return ArtManager.menuIntroFade1;
		}
	}
}