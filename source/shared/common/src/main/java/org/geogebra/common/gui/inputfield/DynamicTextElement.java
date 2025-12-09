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

package org.geogebra.common.gui.inputfield;

/**
 * Class to store substring elements of a GeoText string. A GeoText string can
 * be divided into static and dynamic substrings. Dynamic substrings reference
 * the labels of other GeoElements.
 * 
 * GeoText strings use quotes to separate static from dynamic substrings. These
 * are difficult for users to manage, so GeoGebra text editors simplify user
 * editing by inserting dynamic strings into special gui containers (e.g. an
 * embedded text field).
 * 
 * @author G. Sturr
 * 
 */
public class DynamicTextElement {

	/**
	 * Types of dynamic text elements.
	 */
	public enum DynamicTextType {
		VALUE, DEFINITION, FORMULA_TEXT, STATIC
	}

	public final DynamicTextType type;
	public final String text;

	/**
	 * @param text
	 *            element content
	 * @param type
	 *            element type
	 */
	public DynamicTextElement(String text, DynamicTextType type) {
		this.text = text;
		this.type = type;
	}

	@Override
	public String toString() {
		return type + ": \"" + text + "\"";
	}

}
