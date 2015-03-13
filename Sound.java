// Make sure default JRE System library is used in Eclipse

// This is a non-profit project, we hold no rights for the music.

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class Sound implements Runnable {
	
	public void run() {
		Sound.music();
	}
	
	public static void music() {
		FileInputStream input;
		AdvancedPlayer player; 		
		String randomSong = selectRandomSong();
		
		try {
			input = new FileInputStream(randomSong);
			player = new AdvancedPlayer(input);
			player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static String selectRandomSong() {
		Random rnd = new Random();
		int randomSongNumber = rnd.nextInt(5);
		if (randomSongNumber == 0)
			return "src/music/09 - Battle Theme #3.mp3";
		else if (randomSongNumber == 1)
			return "src/music/108 Battle Theme 1.mp3";
		else if (randomSongNumber == 2) 
			return "src/music/113 Battle Theme 2.mp3";
		else if (randomSongNumber == 3)
			return "src/music/Golden Sun Battle Theme.mp3";
		else if (randomSongNumber == 4)
			return "src/music/Guiles theme download.mp3";
		else return "";
	}
}