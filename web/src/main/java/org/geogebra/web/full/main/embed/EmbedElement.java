package org.geogebra.web.full.main.embed;

import org.geogebra.web.full.main.EmbedManagerW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class EmbedElement {

	private final Widget widget;

	public EmbedElement(Widget widget) {
		this.widget = widget;
	}

	protected Element getElement() {
		return widget.getElement();
	}

	public Widget getGreatParent() {
		return widget.getParent().getParent();
	}

	public String getContentSync() {
		return null;
	}

	public void setSize(int contentWidth, int contentHeight) {
		// overridden for GGB
	}

	public void setContent(String string) {
		// overridden for GM
	}

	/**
	 * @param embedID
	 *            embed ID
	 * @param embedManagerW
	 *            manager
	 */
	public void addListeners(int embedID, EmbedManagerW embedManagerW) {
		// overridden for GM
	}

}
