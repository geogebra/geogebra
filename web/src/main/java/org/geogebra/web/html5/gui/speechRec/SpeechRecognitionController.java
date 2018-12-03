package org.geogebra.web.html5.gui.speechRec;

import org.geogebra.web.html5.main.AppW;

/**
 * Controller for the speech recognition functionality
 * 
 * @author csilla
 *
 */
public class SpeechRecognitionController {
	private AppW appW;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public SpeechRecognitionController(AppW app) {
		this.appW = app;
	}

	/**
	 * get recognized text in English for now as log msg
	 */
	public native void runSpeechRec() /*-{
		$wnd.speechRec();
	}-*/;
}
