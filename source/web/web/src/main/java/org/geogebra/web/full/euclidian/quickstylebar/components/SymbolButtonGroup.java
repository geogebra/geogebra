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

package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.web.full.euclidian.quickstylebar.SpecialSymbolProperty;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;

class SymbolButtonGroup extends FlowPanel {

	/**
	 * @param symbolProperty special symbol property
	 */
	SymbolButtonGroup(SpecialSymbolProperty symbolProperty) {
		addStyleName("buttonRow");
		String fontName = symbolProperty.getFontFamily().cssName();
		for (SpecialSymbolProperty.SpecialSymbol symbol: symbolProperty.getValues()) {
			StandardButton btn = new StandardButton(symbol.description);
			add(btn);
			btn.addStyleName("insertSymbolButton");
			Element symbolDiv = DOM.createDiv();
			symbolDiv.addClassName("symbol");
			symbolDiv.setInnerText(symbol.symbol);
			btn.getElement().insertFirst(symbolDiv);
			symbolDiv.getStyle().setProperty("fontFamily", fontName);
			btn.addFastClickHandler(ignore -> symbolProperty.insertSymbol(symbol));
		}
	}
}
