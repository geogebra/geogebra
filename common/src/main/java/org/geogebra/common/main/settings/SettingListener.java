package org.geogebra.common.main.settings;

/**
 * Listener interface for settings.
 * 
 * A setting listener attached to a setting container is notified if settings
 * were changed. This notification is issued immediately in normal mode. In
 * batch mode this notification is issued after all settings were changed.
 * 
 * @author Florian Sonner
 */
public interface SettingListener {
	/**
	 * Notification that settings were changed.
	 * 
	 * @param settings
	 *            Setting container.
	 */
	public void settingsChanged(AbstractSettings settings);
}
