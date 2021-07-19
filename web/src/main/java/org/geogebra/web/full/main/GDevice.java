package org.geogebra.web.full.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;

/**
 * Device interface, gives access to browser or tablet native features
 */
public interface GDevice {

	/**
	 * @param app
	 *            application
	 * @return file manager
	 */
	FileManager createFileManager(AppW app);

	/**
	 * @param app
	 *            application
	 * @return whether device is offline
	 */
	boolean isOffline(AppW app);

	/**
	 * @param app
	 *            application
	 * @return image input dialog
	 */
	UploadImageDialog getImageInputDialog(AppW app);

	/**
	 * @param app
	 *            application
	 * @return browser view
	 */
	BrowseViewI createBrowseView(AppW app);

	/**
	 * TODO make this browser-dependent, not GDevice dependent
	 * 
	 * @param app
	 *            application
	 * @return construction protocol
	 */
	ConstructionProtocolView getConstructionProtocolView(AppW app);

	/**
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	void resizeView(int width, int height);
}
