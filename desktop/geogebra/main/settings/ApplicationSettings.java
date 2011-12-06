package geogebra.main.settings;

import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;

import java.util.LinkedList;

/**
 * General settings of the application.
 */
public class ApplicationSettings extends AbstractSettings {

	public ApplicationSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}
	// TODO add your settings here

	public ApplicationSettings() {
		super();
	}
}
