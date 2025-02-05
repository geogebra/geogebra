package org.geogebra.common.util;

import org.geogebra.common.kernel.StringTemplate;

/**
 * Converts and object T to a String representation.
 */
public interface ToStringConverter<T> {

	/**
	 * Convert the object of type T to a String representation.
	 * @param object converted object
	 * @return a String of the object.
	 */
	String convert(T object, StringTemplate tpl);

	default String convert(T object) {
		return convert(object, StringTemplate.defaultTemplate);
	}
}
