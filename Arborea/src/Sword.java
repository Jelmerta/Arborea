/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Sword extends Figure {
    Sword(Point pos){
        super(1, pos);
		type = Figure.TYPE_SWORD;
		weapon = 6;
		hit = 4;
        teamIsOrcs = false;
    }

	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.swordImage);
		standSprites.add(ArtManager.swordImage2);
	}
}