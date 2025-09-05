package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.properties.aliases.StringProperty;

public class FilePropertyCollection extends StringPropertyCollection<StringProperty> {
	/**
	 * @param properties properties to handle
	 */
	public FilePropertyCollection(List properties) {
		super(properties);
	}
}
