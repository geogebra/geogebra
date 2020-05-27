package org.geogebra.common.properties;

import java.util.List;

public class PropertiesList extends AbstractPropertyCollection {

	public PropertiesList(String name, Property... properties) {
	    super(name, properties);
    }

	public PropertiesList(String name, List<Property> properties) {
	    super(name, properties);
	}
}