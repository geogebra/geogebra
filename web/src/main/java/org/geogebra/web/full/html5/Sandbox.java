package org.geogebra.web.full.html5;

/**
 * Sandbox privileges for iframe.
 */
public enum Sandbox {
	SCRIPTS("allow-scripts"),
	SAME_ORIGIN("allow-same-origin"),
	POPUPS("allow-popups"),
	FORMS("allow-forms");

	private String privilege;

	Sandbox(String privilege) {
		this.privilege = privilege;
	}

	@Override
	public String toString() {
		return privilege;
	}

	/**
	 *
	 * @return default privileges.
	 */
	public static String defaults() {
		return toList(SCRIPTS, POPUPS, SAME_ORIGIN);
	}

	/**
	 *
	 * @return privileges with form support.
	 */
	public static String forms() {
		return defaults() + " " + FORMS;
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