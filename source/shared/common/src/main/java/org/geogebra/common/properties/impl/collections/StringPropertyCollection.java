package org.geogebra.common.properties.impl.collections;

import java.util.List;

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
	public StringPropertyCollection(List<T> properties) {
		super(properties);
	}

	@Nullable
	@Override
	public String validateValue(String value) {
		for (T property : getProperties()) {
			String invalidMessage = property.validateValue(value);
			if (invalidMessage != null) {
				return invalidMessage;
			}
		}
		return null;
	}
}
