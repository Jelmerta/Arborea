/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//this is the more powerful of the figures in the Orc team
class Orc extends Figure {
	
	// regular constructor initializes default values
    Orc(Point pos){
        super(4, pos);
		type = Figure.TYPE_ORC;
		weapon = 8;
		startHitpoints = 10;
		hit = 10;       
        teamIsOrcs = true;
    }
    
    // copy constructor
    Orc(Figure copy){
    	super(copy);
    }

    // image frames used in an animation are initialized
	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.orcImage);
		standSprites.add(ArtManager.orcImage2);
		standSpritesL = new ArrayList<BufferedImage>(2);
		standSpritesL.add(ArtManager.orcImageL);
		standSpritesL.add(ArtManager.orcImageL2);
	}
}