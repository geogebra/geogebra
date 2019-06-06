package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class AccessibleCheckbox implements AccessibleWidget {
	private CheckBox checkbox;
	private GeoBoolean geo;

	/**
	 * @param geo
	 *            boolean object
	 * @param view
	 *            accessibility view
	 */
	public AccessibleCheckbox(final GeoBoolean geo,
			final AccessibilityView view) {
		this.checkbox = new CheckBox();
		this.geo = geo;
		update();
		checkbox.addDomHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				view.select(geo);
				geo.setValue(checkbox.getValue());
				geo.updateRepaint();
				checkbox.setFocus(true);

			}
		}, ChangeEvent.getType());
	}

	@Override
	public List<CheckBox> getControl() {
		return Collections.singletonList(checkbox);
	}

	@Override
	public void update() {
		checkbox.setValue(geo.getBoolean());
		checkbox.setText(geo.getCaption(StringTemplate.screenReader));
	}
}
