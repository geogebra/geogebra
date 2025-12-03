package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.properties.aliases.StringProperty;

public class FilePropertyListFacade extends StringPropertyListFacade<StringProperty> {
	/**
	 * @param properties properties to handle
	 */
	public FilePropertyListFacade(List properties) {
		super(properties);
	}
}
