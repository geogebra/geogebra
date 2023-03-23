package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.user.client.ui.CheckBox;

/**
 * Accessibility adapter for a checkbox
 */
public class AccessibleCheckbox implements AccessibleWidget {
	private final CheckBox checkbox;
	private final GeoBoolean geo;
	private final AccessibilityView view;

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
		this.view = view;
		update();
		checkbox.addDomHandler(event -> {
			updateGeoElement();
			setFocus(true);
		}, ChangeEvent.getType());
	}

	/**
	 * Update geo from checkbox
	 */
	protected void updateGeoElement() {
		view.select(geo);
		geo.setValue(checkbox.getValue());
		geo.updateRepaint();
	}

	@Override
	public List<CheckBox> getWidgets() {
		return Collections.singletonList(checkbox);
	}

	@Override
	public void update() {
		checkbox.setValue(geo.getBoolean());
		checkbox.setText(view.getCaption(geo));
	}

	@Override
	public void setFocus(boolean focused) {
		checkbox.setFocus(focused);
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		return true;
	}

}
