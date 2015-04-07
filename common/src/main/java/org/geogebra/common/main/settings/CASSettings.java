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

	public CASSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
		init();
	}

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
}
