package org.geogebra.web.html5.main;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor suggests wheter the AppWapplet, AppWsimple has applet
 *         properties, so behaves like an applet.
 */
public interface HasAppletProperties {

	/**
	 * @param width
	 * 
	 *            sets the geogebra-web applet widht
	 */
	public void setWidth(int width);

	/**
	 * @param height
	 * 
	 *            sets the geogebra-web applet height
	 */
	public void setHeight(int height);

	/**
	 * sets the geogebra-web applet size (width, height)
	 * 
	 * @param width
	 *            width in px
	 * @param height
	 *            height in px
	 */
	public void setSize(int width, int height);

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	public void resetAutoSize();

	/**
	 * @param show
	 * 
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show);

	/**
	 * @return callback passed to renderArticleElementWithFrame
	 */
	public JavaScriptObject getOnLoadCallback();

	/**
	 * @return whether keyboard is visible
	 */
	public boolean isKeyboardShowing();

	/**
	 * Flag keyboard to be shown next time applet is focused
	 */
	public void showKeyboardOnFocus();

	/**
	 * Update layout for keyboard height change
	 */
	public void updateKeyboardHeight();

	/**
	 * @return keyboard height in pixels (0 if not showing)
	 */
	public double getKeyboardHeight();

	/**
	 * Remove from DOM and prepare for garbage collection
	 */
	public void remove();

	/**
	 * Can be called to handle the back button event.
	 */
	public void onBackPressed();

	/**
	 * Update the CSS height of the article
	 */
	public void updateArticleHeight();

	/**
	 * @param appW
	 *            app
	 */
	public void initPageControlPanel(AppW appW);
}
