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

package org.geogebra.common.properties.impl;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.jspecify.annotations.Nullable;

public class ZoomProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, SettingsDependentProperty {
	private final NumericPropertyUtil util;
	private final EuclidianView view;

	/**
	 * @param algebraProcessor algebra processor
	 * @param localization localization
	 */
	public ZoomProperty(
			AlgebraProcessor algebraProcessor, Localization localization, EuclidianView view) {
		super(localization, "Zoom");
		this.view = view;
		this.util = new NumericPropertyUtil(algebraProcessor);
	}

	@Override
	public List<String> getSuggestions() {
		return List.of(
				"25%", "50%", "75%", "100%", "150%", "200%", "400%", getShowAll(), getStandardView());
	}

	private String getStandardView() {
		return getLocalization().getMenu("StandardView");
	}

	private String getShowAll() {
		return getLocalization().getMenu("ShowAllObjects");
	}

	@Override
	public AbstractSettings<?> getSettings() {
		return view.getSettings();
	}

	@Override
	protected void doSetValue(String value) {
		App app = view.getApplication();
		if (value.equals(getShowAll())) {
			app.closeMenuHideKeyboard();
			app.setViewShowAllObjects();
			return;
		}
		if (value.equals(getStandardView())) {
			app.closeMenuHideKeyboard();
			view.setStandardView(true);
			app.getAccessibilityManager().focusFirstElement();
			return;
		}
		GeoNumberValue parsedValue = util.parseInputString(value.replace("%", ""));
		if (parsedValue != null) {
			double newScale = parsedValue.getDouble() / 2;
			view.zoom(
					view.getWidth() / 2.0, view.getHeight() / 2.0, newScale / view.getXscale(), 3, false);
		}
	}

	@Override
	public @Nullable String validateValue(String value) {
		if (getStandardView().equals(value) || getShowAll().equals(value)) {
			return null;
		}
		GeoNumberValue parsed = util.parseInputString(value.replace("%", ""));
		double maxPercentage = EuclidianView.MAX_SCALE_NOTES / EuclidianView.SCALE_STANDARD * 100;
		return parsed != null
						&& parsed.getDouble() > Kernel.STANDARD_PRECISION
						&& parsed.getDouble() <= maxPercentage
				? null
				: "";
	}

	@Override
	public String getValue() {
		double percentage = view.getSettings().getXscale() * 2;
		return percentage < 1 ? "< 1%" : ((int) Math.round(percentage)) + "%";
	}

	/**
	 * @param index item index
	 * @return icon related to given item
	 */
	public @Nullable PropertyResource getIcon(int index) {
		return switch (index) {
			case 7 -> PropertyResource.ICON_ZOOM_TO_FIT;
			case 8 -> PropertyResource.ICON_STANDARD_VIEW;
			default -> null;
		};
	}

	/**
	 * @return list of indices of items that should be preceded by a divider
	 */
	public List<Integer> getDividerLocations() {
		return List.of(7);
	}
}
