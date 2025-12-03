package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.properties.RangeProperty;

/**
 * Handles a collection of RangeProperty objects as a single RangeProperty.
 */
public class RangePropertyListFacade<T extends RangeProperty<Integer>>
		extends AbstractValuedPropertyListFacade<T, Integer> implements RangeProperty<Integer> {

	/**
	 * @param properties properties to handle
	 */
	public RangePropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public Integer getStep() {
		return getFirstProperty().getStep();
	}

	@Override
	public Integer getMin() {
		return getFirstProperty().getMin();
	}

	@Override
	public Integer getMax() {
		return getFirstProperty().getMax();
	}

}
