package geogebra.html5.main;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

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
	 * @param width
	 * @param height
	 * 
	 *            sets the geogebra-web applet size (width, height)
	 */
	public void setSize(int width, int height);

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	public void resetAutoSize();

	/**
	 * @param enable
	 *            wheter geogebra-web applet rightclick enabled or not
	 */
	public void enableRightClick(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	public void enableLabelDrags(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	public void enableShiftDragZoom(boolean enable);

	/**
	 * @param show
	 * 
	 *            wheter show the toolbar in geogebra-web applets or not
	 */
	public void showToolBar(boolean show);

	/**
	 * @param show
	 * 
	 *            wheter show the menubar in geogebra-web applets or not
	 */
	public void showMenuBar(boolean show);

	/**
	 * @param show
	 * 
	 *            wheter show the algebrainput in geogebra-web applets or not
	 */
	public void showAlgebraInput(boolean show);

	/**
	 * @param show
	 * 
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show);

	public JavaScriptObject getOnLoadCallback();

	public void showKeyBoard(boolean b, Widget textField, boolean forceShow);

	public boolean isKeyboardShowing();
}
