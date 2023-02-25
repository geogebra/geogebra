package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.StringProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class StringPropertyCollection<T extends StringProperty>
		extends AbstractTypedPropertyCollection<T, String> implements StringProperty {

	/**
	 * @param properties properties to handle
	 */
	public StringPropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public boolean isValid(String value) {
		boolean isValid = true;
		for (StringProperty property : getProperties()) {
			isValid = isValid && property.isValid(value);
		}
		return isValid;
	}

	@Override
	public String getInvalidInputErrorMessage() {
		StringProperty first = getFirstProperty();
		if (first != null) {
			return first.getInvalidInputErrorMessage();
		}
		return "";
	}
}
