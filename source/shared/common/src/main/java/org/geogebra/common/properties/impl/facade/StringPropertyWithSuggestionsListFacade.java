package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

public class StringPropertyWithSuggestionsListFacade<T extends StringPropertyWithSuggestions>
		extends AbstractValuedPropertyListFacade<T, String>
		implements StringPropertyWithSuggestions {
	public StringPropertyWithSuggestionsListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public List<String> getSuggestions() {
		return getFirstProperty().getSuggestions();
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
