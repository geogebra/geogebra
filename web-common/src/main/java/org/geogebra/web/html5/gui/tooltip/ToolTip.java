package org.geogebra.web.html5.gui.tooltip;

import javax.annotation.CheckForNull;

public class ToolTip {
	public final String title;
	public final String helpText;
	public final String buttonTransKey;
	public final String url;
	private final Role role;

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
