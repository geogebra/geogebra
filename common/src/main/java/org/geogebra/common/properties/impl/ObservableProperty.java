package org.geogebra.common.properties.impl;

import org.geogebra.common.properties.Property;

public interface ObservableProperty extends Property {
	void setObserver(PropertyObserver propertyObserver);

	interface PropertyObserver {
		void onChange();
	}
}
