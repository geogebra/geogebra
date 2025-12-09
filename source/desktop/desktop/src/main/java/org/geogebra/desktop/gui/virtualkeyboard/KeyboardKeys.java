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

package org.geogebra.desktop.gui.virtualkeyboard;

public class KeyboardKeys {
	private String UpperCase = null;
	private String LowerCase = null;

	/**
	 * This method returns a ready keys, just hand him (LowerCase, UpperCase)
	 *
	 * @return keys
	 */
	public KeyboardKeys setKeys(String LowerCase, String UpperCase) {
		this.LowerCase = LowerCase;
		this.UpperCase = UpperCase;
		return this;
	}

	/**
	 * This method returns LowerCase
	 *
	 * @return java.lang.String
	 */
	public String getUpperCase() {
		return this.UpperCase;
	}

	/**
	 * This method sets UpperCase
	 */
	public void setUpperCase(String UpperCase) {
		this.UpperCase = UpperCase;
	}

	/**
	 * This method returns LowerCase
	 *
	 * @return java.lang.String
	 */
	public String getLowerCase() {
		return this.LowerCase;
	}

	/**
	 * This method sets UpperCase
	 */
	public void setLowerCase(String LowerCase) {
		this.LowerCase = LowerCase;
	}
}
