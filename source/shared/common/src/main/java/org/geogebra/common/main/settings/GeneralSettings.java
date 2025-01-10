package org.geogebra.common.main.settings;

import java.util.LinkedList;

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

	public void setCoordFormat(int coordFormat) {
		this.coordFormat = coordFormat;
	}
}
