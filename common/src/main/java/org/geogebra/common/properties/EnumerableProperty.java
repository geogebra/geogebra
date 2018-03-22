package org.geogebra.common.properties;

public interface EnumerableProperty extends StringProperty {

    int NONE = -1;

    String[] getValues();

    int getCurrent();
}
