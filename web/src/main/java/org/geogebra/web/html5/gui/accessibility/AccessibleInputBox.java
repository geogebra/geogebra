package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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
	 * @param view accessibility view
	 */
	public AccessibleInputBox(GeoInputBox geo, AppW app, final AccessibilityView view) {
		this.geo = geo;
		this.app = app;
		this.inputBox = new GTextBox();
		this.formLabel = new FormLabel();
		formLabel.setFor(inputBox);
		inputBox.addStyleName("accessibileInput");
		inputBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
					geo.updateLinkedGeo(inputBox.getText());
				}
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
		AccessibleDropDown.updatePosition(geo, inputBox, app);
		formLabel.setText(geo.getAuralText());
		inputBox.setText(geo.getTextForEditor());
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
