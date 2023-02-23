package org.geogebra.common.properties;

public interface ValuedProperty<S> extends Property {

	S getValue();

	void setValue(S value);
}
