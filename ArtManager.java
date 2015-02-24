import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ArtManager {

	static final BufferedImage tile1 = createImage("tile1");
	static final BufferedImage tile2 = createImage("tile2");
	static final BufferedImage tile3 = createImage("tile3");
	
	static final BufferedImage tileMove3 = createImage("tileMove3");
	static final BufferedImage tileAttack3 = createImage("tileAttack3");

	static final BufferedImage mud = createImage("mud");

	static final BufferedImage select = createImage("select");

	static final BufferedImage swordImage = createImage("Swordsman");
	static final BufferedImage generalImage = createImage("General");
	static final BufferedImage goblinImage = createImage("Goblin");
	static final BufferedImage orcImage = createImage("Orc");
	
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
}
