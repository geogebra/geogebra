package org.geogebra.common.properties.util;

import java.util.List;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

/**
 * Helper class for properties with GeoElements. To use this class, the property must
 * implement {@link GeoPropertyDelegate}. All properties are set through this delegate.
 * This class implement handling list of GeoElements.
 *
 * @param <T> property type
 */
public class GeoListPropertyHelper<T> {

	private App app;
	private GeoPropertyDelegate<T> delegate;
	private List<GeoElementND> elements;

	/**
	 * Create a new GeoListPropertyHelper. The properties are set through the delegate.
	 *
	 * @param app      app
	 * @param delegate delegate
	 */
	public GeoListPropertyHelper(App app, GeoPropertyDelegate<T> delegate) {
		this.app = app;
		this.delegate = delegate;
	}

	/**
	 * List of elements for the helper.
	 *
	 * @param elements elements
	 */
	public void setGeoElements(List<GeoElementND> elements) {
		this.elements = elements;
	}

	/**
	 * Set the value for the list of GeoElements.
	 *
	 * @param value value
	 */
	public void setValue(T value) {
		if (!isEmpty()) {
			for (GeoElementND element : elements) {
				delegate.setPropertyValue(element, value);
			}
			app.setPropertiesOccured();
		}
	}

	/**
	 * Get the value of the list.
	 *
	 * @return value
	 */
	public T getValue() {
		if (isEmpty()) {
			return null;
		}
		GeoElementND element = elements.get(0);
		return delegate.getPropertyValue(element);
	}

	/**
	 * Check if the property is enabled for the list.
	 *
	 * @return true if it is enabled.
	 */
	public boolean isEnabled() {
		if (isEmpty()) {
			return false;
		}
		for (GeoElementND element : elements) {
			if (!delegate.hasProperty(element)) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmpty() {
		return elements == null || elements.isEmpty();
	}
}
