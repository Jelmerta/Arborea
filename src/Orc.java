/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Orc extends Figure {
    Orc(Point pos){
        super(4, pos);
		type = Figure.TYPE_ORC;
		weapon = 8;
		startHitpoints = 10;
		hit = 10;       
        teamIsOrcs = true;
    }

	@Override
	void setUpStandSprites() {
		standSprites = new ArrayList<BufferedImage>(2);
		standSprites.add(ArtManager.orcImage);
		standSprites.add(ArtManager.orcImage2);
	}
}