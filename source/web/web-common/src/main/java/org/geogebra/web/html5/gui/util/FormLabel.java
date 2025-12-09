/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
