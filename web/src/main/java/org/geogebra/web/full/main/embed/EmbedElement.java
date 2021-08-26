package org.geogebra.web.full.main.embed;

import org.geogebra.common.plugin.EventType;

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
}
