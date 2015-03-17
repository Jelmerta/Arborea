/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.image.BufferedImage;

// this is an object that handles the introduction movie 
class Menu {
	
	private static final int INTRO_FRAMES = 30;
	
	Animator introAnimator;
	Thread animationThread;
	
	// an animator is created and started
	// the number of animator frames is higher than number in intro
	// this is to ensure compatibility with other threads
    Menu(){
		introAnimator = new Animator(50,75);
		animationThread = new Thread(introAnimator);
		animationThread.start();
    }
	
	// once the animation has shown all frames, the animation ends
	boolean finishedIntro(){
		if (introAnimator.getAnimationIndex() >= INTRO_FRAMES){
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