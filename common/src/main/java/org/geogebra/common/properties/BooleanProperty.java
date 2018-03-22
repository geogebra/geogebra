package org.geogebra.common.properties;

public interface BooleanProperty extends Property {

    String TRUE = "T";
    String FALSE = "F";

    boolean getValue();
}
