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

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.EventType;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Accessibility adapter for any element that may or may not have attached
 * scripts
 */
public class AccessibleGeoElement implements AccessibleWidget {

	private final AccessibilityView view;
	private final Button button;
	private final GeoElement geo;
	private final Label label;
	private Widget activeWidget;

	/**
	 * @param geo
	 *            construction element
	 * @param app
	 *            app
	 * @param view
	 *            accessibility view
	 * @param factory
	 *            controls factory
	 */
	public AccessibleGeoElement(final GeoElement geo, final App app,
			final AccessibilityView view, BaseWidgetFactory factory) {
		this.geo = geo;
		this.label = factory.newLabel();
		this.button = factory.newButton();
		this.view = view;
		label.getElement().setAttribute("role", "status");
		label.getElement().setAttribute("aria-live", "polite");
		update();
		button.addClickHandler(event -> {
			view.select(geo);
			app.handleSpaceKey();
			setFocus(true);
		});
	}

	@Override
	public void update() {
		button.setText(getAction(geo));
		label.setText(geo.getAuralText(getBuilder()));
		activeWidget = geo.getScript(EventType.CLICK) == null ? label : button;
	}

	private ScreenReaderBuilder getBuilder() {
		return new ScreenReaderBuilder(geo.getKernel().getLocalization(),
				NavigatorUtil.isMobile());
	}

	@Override
	public List<Widget> getWidgets() {
		return Collections.singletonList(activeWidget);
	}

	@Override
	public void setFocus(boolean focus) {
		if (activeWidget == button) {
			button.setFocus(focus);
		}
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		// slider and dropdown may change type without redefinition: check here
		return !(geo instanceof GeoList && ((GeoList) geo).drawAsComboBox())
				&& !(geo instanceof GeoNumeric && ((GeoNumeric) geo).isSlider());
	}

	/**
	 * @param sel
	 *            selected element associated with the action
	 * @return action description (eg "press space to activate")
	 */
	private String getAction(GeoElement sel) {
		if (sel instanceof GeoButton || sel instanceof GeoBoolean) {
			return view.getCaption(sel);
		}
		if (sel != null && sel.getScript(EventType.CLICK) != null) {
			return ScreenReader.getAuralText(sel, getBuilder());
		}

		return null;
	}

}
