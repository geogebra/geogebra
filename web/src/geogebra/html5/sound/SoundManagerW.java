package geogebra.html5.sound;

import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.sound.SoundManager;
import geogebra.html5.main.AppWeb;

/**
 * @author micro_000
 *
 */
public class SoundManagerW implements SoundManager {


	private AppWeb app;

	/**
	 * @param app App
	 */
	public SoundManagerW(AppWeb app){
		this.app = app;
	}

	public void pauseResumeSound(boolean b) {
	    App.debug("unimplemented");
	    
    }

	public void playSequenceNote(int double1, double double2, int i, int j) {
	    App.debug("unimplemented");
	    
    }

	public void playSequenceFromString(String string, int double1) {
	    App.debug("unimplemented");
    }

	public void playFunction(GeoFunction geoFunction, double double1,
            double double2) {
	    App.debug("unimplemented");
    }

	public void playFile(String url) {
	    
		// TODO check extension, play MIDI .mid files
		// TODO use MADJSto play MP3 in Firefox
		
		if (!url.endsWith(".mp3")) {
			App.warn("assuming MP3 file: "+url);
		}
		playMP3(url);
	    
    }

	public void playFunction(GeoFunction geoFunction, double double1,
            double double2, int double3, int double4) {
	    App.debug("unimplemented");
    }
	
	/**
	 * @param url eg http://www.geogebra.org/static/spelling/spanish/00/00002.mp3
	 */
	native void playMP3(String url) /*-{
	    var audioElement = document.createElement('audio');
	    audioElement.setAttribute('src', url);
	    audioElement.load();
	    audioElement.addEventListener("canplay", function() {
	        audioElement.play();
	    });

	}-*/;


}
