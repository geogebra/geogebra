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

package org.geogebra.common.main;

/** Material visibility */
public enum MaterialVisibility {
	/** private */
	Private(0, "P"),
	/** shared with link */
	Shared(1, "S"),
	/** public */
	Public(2, "O");

	private int index;
	private String token;

	MaterialVisibility(int index, String tok) {
		this.index = index;
		this.token = tok;
	}

	/**
	 * @return index 0-2
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * @return string representation P/S/O
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * @param token
	 *            string representation.
	 * @return Enum from token.
	 */
	public static MaterialVisibility value(String token) {
		if ("O".equals(token)) {
			return Public;
		} else if ("S".equals(token)) {
			return Shared;
		}
		return Private;
	}

	/**
	 *
	 * @param index representation
	 * @return the corresponding enum.
	 */
	public static MaterialVisibility value(int index) {
		switch (index) {
		case 1:
			return MaterialVisibility.Shared;
		case 2:
			return MaterialVisibility.Public;
		case 0:
		default:
			return MaterialVisibility.Private;
		}
	}
}