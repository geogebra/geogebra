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
	
	/** center WebCam dialog */
	void center();

}
