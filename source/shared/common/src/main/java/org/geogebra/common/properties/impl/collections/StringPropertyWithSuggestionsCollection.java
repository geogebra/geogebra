package org.geogebra.common.properties.impl.collections;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

public class StringPropertyWithSuggestionsCollection<T extends StringPropertyWithSuggestions>
		extends AbstractValuedPropertyCollection<T, String>
		implements StringPropertyWithSuggestions {
	public StringPropertyWithSuggestionsCollection(List<T> properties) {
		super(properties);
	}

	@Override
	public List<String> getSuggestions() {
		return getFirstProperty().getSuggestions();
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		for (T property : getProperties()) {
			String invalidMessage = property.validateValue(value);
			if (invalidMessage != null) {
				return invalidMessage;
			}
		}
		return null;
	}
}
