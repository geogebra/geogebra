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

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Wrapper for the &lt;UL&gt; tag
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