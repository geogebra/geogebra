package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.RangeProperty;

/**
 * Abstract property class for numeric properties.
 * @param <T> numeric type
 */
public abstract class AbstractRangeProperty<T extends Number & Comparable<T>>
		extends AbstractProperty implements RangeProperty<T> {

	private final T min;
	private final T max;
	private final T step;

	/**
	 * Create a new AbstractRangeProperty.
	 * @param localization localization
	 * @param name name of property
	 * @param min min value
	 * @param max max value
	 * @param step step value
	 */
	public AbstractRangeProperty(Localization localization, String name, T min, T max, T step) {
		super(localization, name);
		this.min = min;
		this.max = max;
		this.step = step;
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

	@Override
	public T getMin() {
		return min;
	}

	@Override
	public T getMax() {
		return max;
	}

	@Override
	public T getStep() {
		return step;
	}

	/**
	 * Sets the value safely.
	 * @param value safe value
	 */
	protected abstract void setValueSafe(T value);
}
