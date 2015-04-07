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

	public enum DynamicTextType {
		VALUE, DEFINITION, FORMULA_TEXT, STATIC
	};

	public DynamicTextType type = DynamicTextType.STATIC;
	public String text = "";

	/**
	 * @param text
	 * @param mode
	 */
	public DynamicTextElement(String text, DynamicTextType type) {
		this.text = text;
		this.type = type;
	}

}
