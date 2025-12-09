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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;

/**
 * Input field for MinMaxPanel
 */
class MinMaxAVField extends AutoCompleteTextFieldW {

	private final MinMaxPanel widgets;

	/**
	 * @param columns field width
	 * @param app application
	 */
	public MinMaxAVField(MinMaxPanel widgets, int columns, App app) {
		super(columns, app);
		this.widgets = widgets;
		enableGGBKeyboard();
		prepareShowSymbolButton(false);
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {
		if (Browser.isTabletBrowser()) {
			super.onKeyPress(e);
		}
		e.stopPropagation();
	}

	@Override
	public void onKeyDown(KeyDownEvent e) {
		if (Browser.isTabletBrowser()) {
			super.onKeyDown(e);
		}
		e.stopPropagation();
		if (e.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
			widgets.hide();
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent e) {
		e.stopPropagation();
	}
}
