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

package org.geogebra.common.main.settings;

/**
 * Label settings.
 */
public class LabelSettings extends AbstractSettings {

	private LabelVisibility labelVisibility;
	private LabelVisibility labelVisibilityForMenu;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 */
	LabelSettings() {
		initLabelVisibility();
	}

	private void initLabelVisibility() {
		labelVisibility = LabelVisibility.PointsOnly;
		labelVisibilityForMenu = LabelVisibility.NotSet;
	}

	public LabelVisibility getLabelVisibility() {
		return labelVisibility;
	}

	public LabelVisibility getLabelVisibilityForMenu() {
		return labelVisibilityForMenu;
	}

	/**
	 * Sets the label visibility for the menu as well.
	 * @param labelVisibility label visibility
	 */
	public void setLabelVisibility(LabelVisibility labelVisibility) {
		this.labelVisibility = labelVisibility;
		this.labelVisibilityForMenu = labelVisibility;
		notifyListeners();
	}

	/**
	 * Sets the labelVisibilityForMenu to NotSet and notifies listeners.
	 */
	public void resetDefaultForMenu() {
		labelVisibilityForMenu = LabelVisibility.NotSet;
		notifyListeners();
	}

	@Override
	public void resetDefaults() {
		initLabelVisibility();
	}
}
