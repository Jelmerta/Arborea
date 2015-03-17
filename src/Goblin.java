/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Goblin extends Figure {
    Goblin(Point pos){
        super(3, pos);
		type = Figure.TYPE_GOBLIN;
		weapon = 4;
		startHitpoints = 3;
		hit = 3;
        teamIsOrcs = true;
    }

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