package org.geogebra.common.properties.util;

import java.util.List;

import org.geogebra.common.properties.aliases.StringProperty;

public interface StringPropertyWithSuggestions extends StringProperty {

	/**
	 * @return list of suggested values
	 */
	List<String> getSuggestions();
}
