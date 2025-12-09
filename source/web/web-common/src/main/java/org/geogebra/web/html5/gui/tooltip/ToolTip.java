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

package org.geogebra.web.html5.gui.tooltip;

import javax.annotation.CheckForNull;

public class ToolTip {
	public final String title;
	public final String helpText;
	public final String buttonTransKey;
	public final String url;
	private final Role role;

	/**
	 * Tooltip role.
	 */
	public enum Role {
		INFO, ALERT
	}

	/**
	 * @param text text content
	 * @param role role for accessibility purposes
	 */
	public ToolTip(String text, Role role) {
		this(text, null, null, null, role);
	}

	/**
	 * @param title localized title
	 * @param helpText localized additional text
	 * @param buttonTransKey optional button text (translation key)
	 * @param url url to be opened when button clicked
	 */
	public ToolTip(String title, String helpText, @CheckForNull String buttonTransKey,
			@CheckForNull String url) {
		this(title, helpText, buttonTransKey, url, Role.INFO);
	}

	/**
	 * @param title localized title
	 * @param helpText localized additional text
	 * @param buttonTransKey optional button text (translation key)
	 * @param url url to be opened when button clicked
	 * @param role role for accessibility purposes
	 */
	public ToolTip(String title, String helpText, @CheckForNull String buttonTransKey,
			@CheckForNull String url, Role role) {
		this.title = title;
		this.helpText = helpText;
		this.buttonTransKey = buttonTransKey;
		this.url = url;
		this.role = role;
	}

	public boolean isAlert() {
		return role == Role.ALERT;
	}
}
