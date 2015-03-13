/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ArtManager {

	static final BufferedImage tile1 = createImage("tile1");
	static final BufferedImage tile2 = createImage("tile2");
	static final BufferedImage tile3 = createImage("tile3");
	static final BufferedImage tile4 = createImage("tile4");
	
	static final BufferedImage tileMove3 = createImage("tileMove3");
	static final BufferedImage tileAttack3 = createImage("tileAttack3");

	static final BufferedImage mud = createImage("mud");

	static final BufferedImage select = createImage("select");

	static final BufferedImage swordImage = flipHorizontally(createImage("Swordsman"));
	static final BufferedImage swordImage2 = flipHorizontally(createImage("Swordsman2"));
	
	static final BufferedImage generalImage = flipHorizontally(createImage("General"));
	
	static final BufferedImage goblinImage = createImage("Goblin");
	
	static final BufferedImage orcImage = createImage("Orc");

	static final BufferedImage iconAttack = createImage("iconAttack");
	static final BufferedImage iconMove = createImage("iconMove");
	
	// loads images straight from file
	private static BufferedImage createImage(String name) {
		try {
			return ImageIO.read(new File("src/art/" + name + ".png")); // eclipse
		} catch (IOException e1){
			try {
				return ImageIO.read(new File("art/" + name + ".png")); // niet eclipse
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1); // DEBUG
				return null;
			}
		}
	}
    
    // flips image horizontally
    private static BufferedImage flipHorizontally(BufferedImage image){
    	
    	AffineTransform transformer = AffineTransform.getScaleInstance(-1, 1);
    	transformer.translate(-image.getWidth(null), 0);
    	AffineTransformOp transformerOp = new AffineTransformOp(transformer, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    	image = transformerOp.filter(image, null);
    	
		return image;
    }
}