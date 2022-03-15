package org.geogebra.web.full.main.embed;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Embedded element for a website or GeoGebra calculator.
 */
public class EmbedElement {

	private final Widget widget;

	/**
	 * @param widget
	 *            UI widget
	 */
	public EmbedElement(Widget widget) {
		this.widget = widget;
	}

	/**
	 * @return element of the widget
	 */
	protected Element getElement() {
		return widget.getElement();
	}

	/**
	 * @return parent of a parent, responsible for scaling
	 */
	public Widget getGreatParent() {
		return widget.getParent().getParent();
	}

	/**
	 * Gets the state if the embed supports it and provides synchronous API.
	 * 
	 * @return JSON representation of state or null
	 */
	public String getContentSync() {
		return null;
	}

	/**
	 * @param contentWidth
	 *            content width
	 * @param contentHeight
	 *            content height
	 */
	public void setSize(int contentWidth, int contentHeight) {
		// overridden for GGB
	}

	/**
	 * @param string
	 *            JSON encoded content
	 */
	public void setContent(String string) {
		// overridden for GM
	}

	/**
	 * @param embedID
	 *            embed ID
	 */
	public void addListeners(int embedID) {
		// overridden for GM
	}

	/**
	 * Execute an action on the embedded element
	 * 
	 * @param action
	 *            action type
	 */
	public void executeAction(EventType action) {
		// only for GGB and GM
	}

	/**
	 * @param visible
	 *            whether this should be visible
	 */
	public void setVisible(boolean visible) {
		getGreatParent().setVisible(visible);
	}

	public void setJsEnabled(boolean b) {
		// only for ggb
	}

	/**
	 * Only for GGB and GM embeds
	 * @return the javascript api object for the embedded element
	 */
	public Object getApi() {
		return null;
	}

	/**
	 * @param g2 graphics
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	public void drawPreview(GGraphics2D g2, int width, int height, double angle) {
		SVGResource resource = ToolbarSvgResourcesSync.INSTANCE.mode_extension();
		MyImageW internalImage = new MyImageW(ImageManagerW.getInternalImage(
				resource), true);
		double s = Math.min(width, height);
		int iconLeft = (int) Math.max((width - s) / 2, 0);
		int iconTop = (int) Math.max((height - s) / 2, 0);
		g2.drawImage(internalImage, iconLeft, iconTop, (int) s, (int) s);
	}
}
