/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Show and resize the dialog.
	 */
	void showAndResize();
	
	/** center WebCam dialog */
	void center();

	/** Called if camera stream is available */
	void onCameraSuccess();
	
	/** Called if something is wrong with the camera */
	void onCameraError(String title, String msg);
}
