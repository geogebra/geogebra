package org.geogebra.web.web.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;

/**
 * Device interface, gives access to browser or tablet native features
 */
public interface GDevice {

	/**
	 * @param app
	 *            application
	 * @return file manager
	 */
	public FileManager createFileManager(AppW app);

	/**
	 * @param app
	 *            application
	 * @return whether device is offline
	 */
	public boolean isOffline(AppW app);

	/**
	 * @param app
	 *            application
	 * @return image input dialog
	 */
	public UploadImageDialog getImageInputDialog(AppW app);

	/**
	 * @param app
	 *            application
	 * @return browser view
	 */
	public BrowseViewI createBrowseView(AppW app);

	/**
	 * TODO make this browser-dependent, not GDevice dependent
	 * 
	 * @param app
	 *            application
	 * @return construction protocol
	 */
	public ConstructionProtocolView getConstructionProtocolView(AppW app);

	/**
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	public void resizeView(int width, int height);
}
