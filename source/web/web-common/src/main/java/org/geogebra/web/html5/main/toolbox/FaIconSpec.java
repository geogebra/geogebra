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

package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;

public class FaIconSpec implements IconSpec {
	String name;

	public FaIconSpec(String name) {
		this.name = name;
	}

	@Override
	public Element toElement() {
		Element icon = DOM.createElement("I");
		icon.setClassName(name);
		icon.addClassName("fa-light");
		return icon;
	}

	@Override
	public IconSpec withFill(String color) {
		// not needed, solved through css
		return this;
	}
}
