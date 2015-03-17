/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

//this object cycles through animation IDs at a certain interval
class Animator implements Runnable{
	
	// amount of cycle time used when not readily defined
	private static final int DEFAULT_ANIMATION_INTERVAL = 250;
	
	// integers used for looping over animation
	private int animationID, amountOfAnimations, animationInterval;
	
	// the animator object is initialized, but not started
	Animator(int amount){
		this(amount, DEFAULT_ANIMATION_INTERVAL);
	}	
	Animator(int amount, int interval){
		amountOfAnimations = amount;
		animationID = 0;
		animationInterval = interval;
	}
	
	// sets the number of animations the animator will cycle through
	void setAmountOfAnimations(int amount){
		amountOfAnimations = amount;
	}
	
	// returns an index for a a sprite in the animation
	int getAnimationIndex(){
		return animationID;
	}

	// switches index used for a displayed sprite
	@Override
	public void run() {
		while (!Thread.interrupted()){
			try {
				Thread.sleep(animationInterval);
				int tempID = animationID +1;
				if (tempID >= amountOfAnimations) tempID = 0;
				animationID = tempID;
			} catch (InterruptedException e) {
			}
		}
	}
}