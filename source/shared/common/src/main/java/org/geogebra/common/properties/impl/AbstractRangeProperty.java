/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.RangeProperty;

/**
 * Abstract property class for numeric properties.
 * @param <T> numeric type
 */
public abstract class AbstractRangeProperty<T extends Number & Comparable<T>>
		extends AbstractValuedProperty<T> implements RangeProperty<T> {

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
	protected void doSetValue(T value) {
		if (value.compareTo(getMin()) >= 0 && value.compareTo(getMax()) <= 0) {
			setValueSafe(value);
		} else {
			throw new RuntimeException("The value " + value
					+ " must be between [" + getMin() + ", " + getMax() + "]");
		}
	}

	@Override
	public @CheckForNull T getMin() {
		return min;
	}

	@Override
	public @CheckForNull T getMax() {
		return max;
	}

	@Override
	public @CheckForNull T getStep() {
		return step;
	}

	/**
	 * Sets the value safely.
	 * @param value safe value
	 */
	protected abstract void setValueSafe(T value);
}
