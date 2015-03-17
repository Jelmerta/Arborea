/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//this is the less powerful of the figures in the Orc team
class Goblin extends Figure {
	
	// regular constructor initializes default values
    Goblin(Point pos){
        super(3, pos);
		type = Figure.TYPE_GOBLIN;
		weapon = 4;
		startHitpoints = 3;
		hit = 3;
        teamIsOrcs = true;
    }
    
    // copy constructor
    Goblin(Figure copy){
    	super(copy);
    }

    // image frames used in an animation are initialized
	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.goblinImage);
		standSprites.add(ArtManager.goblinImage2);
		standSpritesL = new ArrayList<BufferedImage>(2);
		standSpritesL.add(ArtManager.goblinImageL);
		standSpritesL.add(ArtManager.goblinImageL2);
	}
}