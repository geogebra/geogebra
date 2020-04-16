package org.geogebra.web.full.html5;

public enum Sandbox {
	SCRIPTS("allow-scripts"),
	SAME_ORIGIN("allow-same-origin"),
	;

	private String privilege;

	Sandbox(String privilege) {
		this.privilege = privilege;
	}

	@Override
	public String toString() {
		return privilege;
	}

	public static String toList(Sandbox... privileges) {
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
