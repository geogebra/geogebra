package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * Accessibility adapter for a checkbox
 */
public class AccessibleCheckbox implements AccessibleWidget {
	private CheckBox checkbox;
	private GeoBoolean geo;
	private AccessibilityView view;

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
		checkbox.addDomHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateGeoElement();
				setFocus(true);
			}
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
		checkbox.setText(geo.getCaption(StringTemplate.screenReader));
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
