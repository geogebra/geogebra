package org.geogebra.common.properties;

public interface EnumerableProperty extends Property {

    int NONE = -1;

    String[] getValues();

    int getCurrent();
}
