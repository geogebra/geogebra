package org.geogebra.common.properties;

public interface EnumerableProperty extends StringProperty {
    
    String[] getValues();

    int getCurrent();
}
