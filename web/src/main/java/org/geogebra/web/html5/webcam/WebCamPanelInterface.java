package org.geogebra.web.html5.webcam;

/**
 * Interface to interact with the WebCamAPI 
 * 
 * @author laszlo
 *
 */
public interface WebCamPanelInterface {
	/**
	 * Called when API requests permission to use the camera.
	 */
	void onRequest();
	
	/**
	 * Called on API success.
	 */
	void onCameraSuccess();
	
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