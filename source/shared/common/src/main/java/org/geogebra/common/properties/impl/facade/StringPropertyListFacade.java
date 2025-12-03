package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.aliases.StringProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class StringPropertyListFacade<T extends StringProperty>
		extends AbstractValuedPropertyListFacade<T, String> implements StringProperty {

	/**
	 * @param properties properties to handle
	 */
	public StringPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		for (T property : properties) {
			String invalidMessage = property.validateValue(value);
			if (invalidMessage != null) {
				return invalidMessage;
			}
		}
		return null;
	}
}
