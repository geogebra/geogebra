package org.geogebra.common.properties;

public class PropertiesList {

    protected Property[] mProperties;

    public PropertiesList(Property[] properties) {
        mProperties = properties;
    }

    public Property[] getPropertiesList() {
        return mProperties;
    }
}