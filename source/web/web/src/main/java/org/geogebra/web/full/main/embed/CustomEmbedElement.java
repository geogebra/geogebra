package org.geogebra.web.full.main.embed;

import org.gwtproject.user.client.ui.Widget;

public class CustomEmbedElement extends EmbedElement {
	/**
	 * @param widget UI widget
	 */
	public CustomEmbedElement(Widget widget) {
		super(widget);
	}

	public void setInnerHTML(String content) {
		getElement().setInnerHTML(content);
	}
}
