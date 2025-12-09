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