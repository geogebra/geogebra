package org.geogebra.web.html5.util;

/**
 * Element with generated data-test attribute, depending on position within its parent.
 */
public interface HasDataTest {
	/**
	 * Update the data-test attribute.
	 * @param index index within parent
	 */
	void updateDataTest(int index);
}
