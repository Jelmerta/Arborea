/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//this is the less powerful of the figures in the Men team
class Sword extends Figure {
	
	// regular constructor initializes default values
    Sword(Point pos){
        super(1, pos);
		type = Figure.TYPE_SWORD;
		weapon = 6;
		startHitpoints = 4;
		hit = 4;
        teamIsOrcs = false;
        facingRight = false;
    }
    
    // copy constructor
    Sword(Figure copy){
    	super(copy);
    }

    // image frames used in an animation are initialized
	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.swordImage);
		standSprites.add(ArtManager.swordImage2);
		standSpritesL = new ArrayList<BufferedImage>(2);
		standSpritesL.add(ArtManager.swordImageL);
		standSpritesL.add(ArtManager.swordImageL2);
	}
}