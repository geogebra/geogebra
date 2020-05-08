package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Widget;

public class AccessibleInputBox implements AccessibleWidget {
	private final GeoInputBox geo;
	private final AutoCompleteTextFieldW inputBox;
	private final FormLabel formLabel;

	/**
	 * @param geo input box
	 * @param app app
	 * @param view accessibility view
	 */
	public AccessibleInputBox(GeoInputBox geo, AppW app, final AccessibilityView view) {
		this.geo = geo;
		this.inputBox = new AutoCompleteTextFieldW(-1, app);
		this.formLabel = new FormLabel();
		formLabel.setFor(inputBox);
		inputBox.setUsedForInputBox(geo);
		inputBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent focusEvent) {
				view.show();
			}
		});
		update();
	}

	@Override
	public List<? extends Widget> getWidgets() {
		return Arrays.asList(formLabel, inputBox);
	}

	@Override
	public void update() {
		formLabel.setText(geo.getAuralText());
		inputBox.setText(geo.getTextForEditor());
	}

	@Override
	public void setFocus(boolean focus) {
		inputBox.setFocus(focus);
	}
}
