package org.geogebra.web.html5.webcam;

/**
 * Interface for handling WebCam dialogs
 * @author laszlo
 *
 */
public interface WebcamDialogInterface {
	/** Show the WebCam dialog*/ 
	void show();
	
	/** resize the WebCam dialog */ 
	void resize();
	
	void showAndResize();
	
	/** center WebCam dialog */
	void center();

	/** Called if camera stream is available */
	void onCameraSuccess();
	
	/** Called if something is wrong with the camera */
	void onCameraError(String title, String msg);
}
