package org.geogebra.common.properties;

import java.util.List;

public class PropertiesList {

    protected Property[] mProperties;

	public PropertiesList(Property... properties) {
        mProperties = properties;
    }

	public PropertiesList(List<Property> properties) {
		mProperties = properties.toArray(new Property[properties.size()]);
	}

    public Property[] getPropertiesList() {
        return mProperties;
    }
}