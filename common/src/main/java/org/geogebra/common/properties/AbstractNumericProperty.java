package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

/**
 * Abstract property class for numeric properties.
 * @param <T> numeric type
 */
public abstract class AbstractNumericProperty<T extends Number & Comparable<T>>
		extends AbstractProperty implements NumericProperty<T> {

	/**
	 * Create a new AbstractNumericProperty.
	 *
	 * @param localization localization
	 * @param name name of property
	 */
	public AbstractNumericProperty(Localization localization, String name) {
		super(localization, name);
	}

	@Override
	public void setValue(T value) {
		if (value.compareTo(getMin()) >= 0 && value.compareTo(getMax()) <= 0) {
			setValueSafe(value);
		} else {
			throw new RuntimeException("The value " + value
					+ " must be between [" + getMin() + ", " + getMax() + "]");
		}
	}

	/**
	 * Sets the value safely.
	 *
	 * @param value safe value
	 */
	protected abstract void setValueSafe(T value);
}
