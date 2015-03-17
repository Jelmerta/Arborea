/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class General extends Figure {
	General(Point pos){
        super(2, pos);
		type = Figure.TYPE_GENERAL;
		weapon = 8;
		startHitpoints = 5;
		hit = 5;
        teamIsOrcs = false;
        facingRight = false;
    }

	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.generalImage);
		standSprites.add(ArtManager.generalImage2);
		standSpritesL = new ArrayList<BufferedImage>(2);
		standSpritesL.add(ArtManager.generalImageL);
		standSpritesL.add(ArtManager.generalImageL2);
	}
}