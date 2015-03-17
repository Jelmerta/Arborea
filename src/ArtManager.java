/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
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

	static final BufferedImage swordImage = createImage("Swordsman");
	static final BufferedImage swordImage2 = createImage("Swordsman2");	
	static final BufferedImage swordImageL = flipHorizontally(swordImage);
	static final BufferedImage swordImageL2 = flipHorizontally(swordImage2);
	
	static final BufferedImage generalImage = createImage("General");
	static final BufferedImage generalImage2 = createImage("General2");	
	static final BufferedImage generalImageL = flipHorizontally(generalImage);
	static final BufferedImage generalImageL2 = flipHorizontally(generalImage2);
	
	static final BufferedImage goblinImage = createImage("Goblin");
	static final BufferedImage goblinImage2 = createImage("Goblin2");
	static final BufferedImage goblinImageL = flipHorizontally(goblinImage);
	static final BufferedImage goblinImageL2 = flipHorizontally(goblinImage2);

	static final BufferedImage orcImage = createImage("Orc");
	static final BufferedImage orcImage2 = createImage("Orc2");
	static final BufferedImage orcImageL = flipHorizontally(orcImage);
	static final BufferedImage orcImageL2 = flipHorizontally(orcImage2);

	static final BufferedImage iconAttack = createImage("iconAttack");
	static final BufferedImage iconMove = createImage("iconMove");
	static final BufferedImage iconHealthbar = createImage("healthbar");
	static final BufferedImage iconHealthbarGreen = createImage("healthbarGreen");
	static final BufferedImage iconHealthbarRed = createImage("healthbarRed"); 
	
	static final BufferedImage overlayTurn = createImage("overlayTurn");
	static final BufferedImage overlayMen = createImage("overlayMen");
	static final BufferedImage overlayOrcs = createImage("overlayOrc");
	static final BufferedImage overlaySound = createImage("overlaySound");
	static final BufferedImage overlayMute = createImage("overlayMute");
	
	static final BufferedImage menuIntro = createImage("menuIntro");
	static final BufferedImage menuIntroFade1 = makeTransparent(menuIntro, 0.1f);
	static final BufferedImage menuIntroFade2 = makeTransparent(menuIntro, 0.2f);
	static final BufferedImage menuIntroFade3 = makeTransparent(menuIntro, 0.3f);
	static final BufferedImage menuIntroFade4 = makeTransparent(menuIntro, 0.4f);
	static final BufferedImage menuIntroFade5 = makeTransparent(menuIntro, 0.5f);
	static final BufferedImage menuIntroFade6 = makeTransparent(menuIntro, 0.6f);
	static final BufferedImage menuIntroFade7 = makeTransparent(menuIntro, 0.7f);
	static final BufferedImage menuIntroFade8 = makeTransparent(menuIntro, 0.8f);
	static final BufferedImage menuIntroFade9 = makeTransparent(menuIntro, 0.9f);

	static final BufferedImage menuOrcsVictory = createImage("menuOrcs");
	static final BufferedImage menuMenVictory = createImage("menuMen");
	static final BufferedImage menuOrcsStart = createImage("menuStartOrc");
	static final BufferedImage menuMenStart = createImage("menuStartMen");
	static final BufferedImage menuArborea = createImage("menuArborea");
		
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
    
    // makes an image transparent
    private static BufferedImage makeTransparent(BufferedImage image, float transparency){    	
    	BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = transparentImage.createGraphics();
		g.setComposite(AlphaComposite.SrcOver.derive(transparency)); 
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		return transparentImage;
    }
}
