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

import java.util.LinkedList;

import org.geogebra.common.kernel.Kernel;

/**
 * General settings of the application.
 */
public class GeneralSettings extends AbstractSettings {

	private int coordFormat = CoordinatesFormat.COORD_FORMAT_DEFAULT;

	public GeneralSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	// TODO add your settings here

	public GeneralSettings() {
		super();
	}

	public int getCoordFormat() {
		return coordFormat;
	}

	public String getPointEditorTemplate() {
		return coordFormat == Kernel.COORD_STYLE_AUSTRIAN ? "$pointAt" : "$point";
	}

	/**
	 * Set coordinate format.
	 * @param coordFormat one of Kernel.COORD_STYLE_* constants
	 */
	public void setCoordFormat(int coordFormat) {
		this.coordFormat = coordFormat;
		settingChanged();
	}
}
