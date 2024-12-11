package org.geogebra.web.html5.main;

import org.geogebra.gwtutil.JsConsumer;

/**
 * Interface for GeoGebra applet frame
 * @author gabor
 */
public interface HasAppletProperties {

	/**
	 * @param width
	 * 
	 *            sets the geogebra-web applet width
	 */
	void setWidth(int width);

	/**
	 * @param height
	 * 
	 *            sets the geogebra-web applet height
	 */
	void setHeight(int height);

	/**
	 * sets the geogebra-web applet size (width, height)
	 * 
	 * @param width
	 *            width in px
	 * @param height
	 *            height in px
	 */
	void setSize(int width, int height);

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	void resetAutoSize();

	/**
	 * @param show
	 * 
	 *            whether to show the reseticon in geogebra-web applets or not
	 */
	void showResetIcon(boolean show);

	/**
	 * @return callback passed to renderArticleElementWithFrame
	 */
	JsConsumer<Object> getOnLoadCallback();

	/**
	 * @return whether keyboard is visible
	 */
	boolean isKeyboardShowing();

	/**
	 * Flag keyboard to be shown next time applet is focused
	 */
	void showKeyboardOnFocus();

	/**
	 * Update layout for keyboard height change
	 */
	void updateKeyboardHeight();

	/**
	 * @return keyboard height in pixels (0 if not showing)
	 */
	double getKeyboardHeight();

	/**
	 * Remove from DOM and prepare for garbage collection
	 */
	void remove();

	/**
	 * Update the CSS height of the article
	 */
	void updateArticleHeight();

	/**
	 * @param appW
	 *            app
	 */
	void initPageControlPanel(AppW appW);
}
