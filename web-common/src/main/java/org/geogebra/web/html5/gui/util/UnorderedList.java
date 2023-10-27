package org.geogebra.web.html5.gui.util;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Wrapper for the &lt;UL&rt; tag
 */
public class UnorderedList extends ComplexPanel {

	/**
	 * Create new UL
	 */
	public UnorderedList() {
		setElement(Document.get().createULElement());
	}

	@Override
	public void add(Widget w) {
		Element el = getElement();
		add(w, el);
	}
}