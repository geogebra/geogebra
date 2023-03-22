package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.LabelElement;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.UIObject;

/**
 * Label for form elements
 * 
 * @author zbynek
 */
public class FormLabel extends FlowPanel {
	/**
	 * Interface for objects that wrap an input element
	 */
	public interface HasInputElement {
		/**
		 * @return wrapped input element
		 */
		Element getInputElement();
	}

	/**
	 * @param string
	 *            plain text content
	 */
	public FormLabel(String string) {
		this();
		getElement().setInnerText(string);
	}

	/**
	 * Create empty form label
	 */
	public FormLabel() {
		super(LabelElement.TAG);
		addStyleName("gwt-Label");
	}

	/**
	 * @param string
	 *            (plain) text content
	 */
	public void setText(String string) {
		getElement().setInnerText(string);
	}

	/**
	 * @param ui
	 *            UI element to be labeled by this
	 * @return this
	 */
	public FormLabel setFor(UIObject ui) {
		Element target = ui.getElement();
		if (ui instanceof HasInputElement) {
			target = ((HasInputElement) ui).getInputElement();
		}
		if (StringUtil.empty(target.getId())) {
			target.setId(DOM.createUniqueId());
		}
		getElement().setAttribute("for", target.getId());
		return this;
	}
}
