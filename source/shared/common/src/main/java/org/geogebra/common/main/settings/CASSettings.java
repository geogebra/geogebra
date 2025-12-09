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
