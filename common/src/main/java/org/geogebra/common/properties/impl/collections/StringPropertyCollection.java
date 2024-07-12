package org.geogebra.common.properties.impl.collections;

import javax.annotation.Nullable;

import org.geogebra.common.properties.aliases.StringProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class StringPropertyCollection<T extends StringProperty>
		extends AbstractValuedPropertyCollection<T, String> implements StringProperty {

	/**
	 * @param properties properties to handle
	 */
	public StringPropertyCollection(T[] properties) {
		super(properties);
	}

	@Nullable
	@Override
	public String validateValue(String value) {
		T[] properties = getProperties();
		for (int i = 0; i < properties.length; i++) {
			String invalidMessage = properties[i].validateValue(value);
			if (invalidMessage != null) {
				return invalidMessage;
			}
		}
		return null;
	}
}
