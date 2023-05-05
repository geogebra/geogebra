package org.geogebra.common.properties;

/**
 * A property that runs an action when selected.
 */
public interface ActionableProperty extends Property {

	/**
	 * Return the action that should run when this property is selected.
	 * @return runnable action
	 */
	Runnable getAction();
}
