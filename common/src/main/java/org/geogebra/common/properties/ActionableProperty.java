package org.geogebra.common.properties;

public interface ActionableProperty extends Property {

	Runnable getAction();
}
