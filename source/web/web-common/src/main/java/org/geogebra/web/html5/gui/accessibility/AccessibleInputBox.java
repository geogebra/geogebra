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

package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.Localization;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class AccessibleInputBox implements AccessibleWidget {
	private final GeoInputBox geo;
	private final GTextBox inputBox;
	private final FormLabel formLabel;
	private final AppW app;

	/**
	 * @param geo input box
	 * @param app app
	 */
	public AccessibleInputBox(GeoInputBox geo, AppW app) {
		this.geo = geo;
		this.app = app;
		this.inputBox = new GTextBox();
		this.formLabel = new FormLabel();
		formLabel.setFor(inputBox);
		inputBox.addStyleName("accessibleInput");
		inputBox.addKeyDownHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
				geo.updateLinkedGeo(inputBox.getText());
			}
		});
		Dom.addEventListener(inputBox.getElement(), "touchstart", e -> {
			e.preventDefault();
			app.getSelectionManager().addSelectedGeoForEV(geo);
		});
		update();
	}

	@Override
	public List<? extends Widget> getWidgets() {
		return Arrays.asList(formLabel, inputBox);
	}

	@Override
	public void update() {
		AccessibleDropDown.updatePosition(geo, inputBox, app);
		formLabel.setText(geo.getAuralText() + getErrorText());
		inputBox.setText(geo.toValueString(app.getScreenReaderTemplate()));
	}

	private String getErrorText() {
		if (geo.hasError()) {
			return " " + getErrorText(app.getLocalization());
		}
		return "";
	}

	/**
	 * @param loc localization
	 * @return message for syntax error
	 */
	public static String getErrorText(Localization loc) {
		return loc.getMenuDefault("InputContainsSyntaxError",
				"The input you entered contains a syntax error");
	}

	@Override
	public void setFocus(boolean focus) {
		inputBox.setFocus(focus);
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		return true;
	}
}
