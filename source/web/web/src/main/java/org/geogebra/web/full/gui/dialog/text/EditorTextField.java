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

package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.web.awt.GFontW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;

public class EditorTextField extends GTextBox implements KeyUpHandler {

	Element target;

	/**
	 * Create new textfield.
	 */
	public EditorTextField() {
		super();
		// TODO: use CSS style
		getStyleElement().setAttribute("spellcheck", "false");
		getStyleElement().setAttribute("oncontextmenu", "return false");

		addKeyUpHandler(this);
	}

	/** TODO: use CSS style */
	public void setFont(GFontW font) {
		int fontSize = font.getFontSize();
		String fontFamily = font.getFontFamily();

		getStyleElement().setAttribute("style",
				"font-family:" + fontFamily + "; font-size:" + fontSize + "pt");
	}

	protected void updateTarget() {
		if (target != null) {
			target.setPropertyString("value", getText());
		}
	}

	public void setTarget(Element target) {
		this.target = target;
	}

	@Override
	public void onKeyUp(KeyUpEvent e) {
		updateTarget();
	}

}
