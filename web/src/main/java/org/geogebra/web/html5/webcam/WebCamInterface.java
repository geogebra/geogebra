package org.geogebra.web.html5.webcam;

import elemental2.dom.MediaStream;

/**
 * Interface to interact with the WebCamAPI 
 * 
 * @author laszlo
 *
 */
public interface WebCamInterface {
	/**
	 * Called when API requests permission to use the camera.
	 */
	void onRequest();
	
	/**
	 * Called on API success.
	 * 
	 * @param mediaStream 
	 * 				the video stream of the camera.
	 */
	void onCameraSuccess(MediaStream mediaStream);
	
	/**
	 * Called on camera error.
	 * 
	 * @param error
	 * 			 the error message.	
	 */
	void onCameraError(String error);

	/**
	 * Called when video metadata is ready.
	 * 
	 * @param width
	 * 				of the video.
	 * 
	 * @param height
	 * 				of the video.
	 */
	void onLoadedMetadata(int width, int height);
	
	/**
	 * Called whe webcam is not supported by browser.
	 */
	void onNotSupported();
}