package org.geogebra.common.properties;

public interface ActionableEnumeratedProperty<V> extends EnumeratedProperty<V> {

	/**
	 * Returns the array of actions to be executed for the options in the property
	 * @return array of callbacks
	 */
	Runnable[] getActions();
}
