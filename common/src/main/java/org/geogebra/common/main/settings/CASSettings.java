package org.geogebra.common.main.settings;

import java.util.LinkedList;

/**
 * Stores CAS specific settings
 * 
 * @author tom
 *
 */
public class CASSettings extends AbstractSettings {

	private long timeoutMillis;
	private boolean showExpAsRoots;
	private Boolean enabled = null;

	/**
	 * @param listeners
	 *            listeners
	 */
	public CASSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
		init();
	}

	/**
	 * New settings
	 */
	public CASSettings() {
		super();
		init();
	}

	private void init() {
		showExpAsRoots = true;
	}

	/**
	 * Changes the timeout value for the cas
	 * 
	 * @param value
	 *            new timeout value, in milliseconds
	 */
	public void setTimeoutMilliseconds(long value) {
		if (timeoutMillis != value) {
			timeoutMillis = value;
			settingChanged();
		}
	}

	/**
	 * Changes the showExpAsRoos value for the cas
	 * 
	 * @param value
	 *            new boolean value if exponents should be displayed as roots
	 */
	public void setShowExpAsRoots(boolean value) {
		if (showExpAsRoots != value) {
			showExpAsRoots = value;
			settingChanged();
		}
	}

	/**
	 * @return CAS timeout value in milliseconds.
	 */
	public long getTimeoutMilliseconds() {
		return timeoutMillis;
	}

	/**
	 * @return boolean value if exponents should be displayed as roots
	 */
	public boolean getShowExpAsRoots() {
		return showExpAsRoots;
	}

	/**
	 * @param enable
	 *            whether CAS view and commands are allowed
	 */
	public void setEnabled(boolean enable) {
		if (enabled == null || enabled != enable) {
			this.enabled = enable;
			settingChanged();
		}
	}

	/**
	 * reset cas enable (needed for exam mode)
	 */
	public void resetEnabled() {
		enabled = null;
	}

	/**
	 * @return whether CAS view and commands are allowed
	 */
	public boolean isEnabled() {
		return enabled == null || enabled;
	}

	/**
	 * @return whether CAS was enabled / disabled explicitly
	 */
	public boolean isEnabledSet() {
		return enabled != null;
	}
}
