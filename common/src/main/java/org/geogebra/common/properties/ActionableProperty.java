package org.geogebra.common.properties;

/**
 * A property that runs an action when selected. Some properties do not hold a value as most do,
 * but are associated with an action. This action can be called
 * with {@link ActionableProperty#performAction()}.
 * <p>
 * For example, a property that centers all objects in the view can be of this type.
 */
public interface ActionableProperty extends Property {

	/**
	 * Performs the action associated with this property.
	 */
	void performAction();
}
