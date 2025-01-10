package org.geogebra.web.html5.gui.util;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Panel based on li tag
 */
public class ListItem extends ComplexPanel {

	/**
	 * New list element panel
	 */
	public ListItem() {
		Element el = Document.get().createLIElement();
		setElement(el);
		el.setTabIndex(-1);
	}

	@Override
	public void add(Widget w) {
		Element el = getElement();
		add(w, el);
	}

	/**
	 * Focus or blur this.
	 * 
	 * @param focused
	 *            whether to focus
	 */
	public void setFocus(boolean focused) {
		if (focused) {
			getElement().focus();
		} else {
			getElement().blur();
		}
	}

	public void setText(String text) {
		getElement().setInnerText(text);
	}
}