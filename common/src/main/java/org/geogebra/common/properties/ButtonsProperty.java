package org.geogebra.common.properties;

/**
 * A property that has multiple buttons to click.
 */
public interface ButtonsProperty extends Property {

	/**
	 * @return localized captions for this property
	 */
	String[] getCaptions();

	/**
	 * @return an array of identifiers for buttons icons
	 */
	PropertyResource[] getIcons();

	/**
	 * Action performed when button is clicked
	 * 
	 * @param i
	 *            button index
	 */
	void onButtonClicked(int i);

}
