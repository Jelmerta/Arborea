/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;


// note that music must be .wav files !

class MusicPlayer implements Runnable {
	
	// boolean for when to play music
	private boolean readyForNextSong = true;
	
	// array that contains all music files
	File[] musicFiles;

	// randomizer object
	Random randomizer;
	
	// integer to keep track of last used index, to avoid doubling
	int lastIndex;
	
	// sets up the object by creating a list of all music files
	MusicPlayer(){
		updateMusicFiles();
		randomizer = new Random();
		lastIndex = -1;
	}
	
	// adapted from http://stackoverflow.com/questions/577724/trouble-playing-wav-in-java
	private void playMusic(File musicFile) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
		class AudioListener implements LineListener {
			private boolean done = false;
			@Override public synchronized void update(LineEvent event) {
				Type eventType = event.getType();
				if (eventType == Type.STOP || eventType == Type.CLOSE) {
					done = true;
					notifyAll();
				}
			}
			public synchronized void waitUntilDone() throws InterruptedException {
				while (!done) wait();
			}
		}
		AudioListener listener = new AudioListener();
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
		try {
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(audioInputStream);
			try {
				clip.start();
				listener.waitUntilDone();
			} finally {
				clip.close();
			}
		} finally {
			audioInputStream.close();
			readyForNextSong = true;
		}
	}
	
	void updateMusicFiles(){
		String folder;
		if (Arborea.enterTheMatrix) folder = "secret";
		else folder = "music";

		musicFiles = new File("src/" + folder).listFiles(); //eclipse
		//musicFiles = new File(folder).listFiles(); //console
	}
	
	@Override
	public void run() {
	    while (true){
		    if(readyForNextSong){
		    	readyForNextSong = false;
			    try {
			    	playMusic(selectRandomSong());
				} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					break;
				}
		    }
		}
	}
	
	// selects a random song
	private File selectRandomSong() {
		int randomIndex = randomizer.nextInt(musicFiles.length);
		
		// make sure not to use last song, but only if enough songs
		if (musicFiles.length > 1)
			while (randomIndex == lastIndex)
				randomIndex = randomizer.nextInt(musicFiles.length);
		lastIndex = randomIndex;
		return musicFiles[randomIndex];
	}
}