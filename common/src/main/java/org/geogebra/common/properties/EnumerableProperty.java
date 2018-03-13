package org.geogebra.common.properties;

public interface EnumerableProperty extends Property {

    String[] getValues();

    int getCurrent();
}
