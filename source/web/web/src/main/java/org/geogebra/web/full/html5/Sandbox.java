package org.geogebra.web.full.html5;

/**
 * Sandbox privileges for iframe.
 */
public enum Sandbox {
	SCRIPTS("allow-scripts"),
	SAME_ORIGIN("allow-same-origin"),
	POPUPS("allow-popups"),
	FORMS("allow-forms");

	private final String privilege;

	Sandbox(String privilege) {
		this.privilege = privilege;
	}

	@Override
	public String toString() {
		return privilege;
	}

	/**
	 *
	 * @return video privileges.
	 */
	public static String videos() {
		return toList(SCRIPTS, SAME_ORIGIN);
	}

	/**
	 *
	 * @return privileges for web embeds.
	 */
	public static String embeds() {
		return toList(SCRIPTS, SAME_ORIGIN, POPUPS, FORMS);
	}

	private static String toList(Sandbox... privileges) {
		StringBuilder result = new StringBuilder();
		String delimiter = "";
		for (Sandbox privilege: privileges) {
			result.append(delimiter);
			result.append(privilege.toString());
			delimiter = " ";
		}
		return result.toString();
	}
}