package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

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
		// TODO if the box remains hidden, it can't be reached with screenreader
		// if it's shown, it blocks touch events
		AccessibleDropDown.updatePosition(geo, inputBox, app);
		formLabel.setText(geo.getAuralText() + getErrorText());
		inputBox.setText(geo.getTextForEditor());
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
