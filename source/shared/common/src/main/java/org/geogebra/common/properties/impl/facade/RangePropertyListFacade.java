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

	@Override
	public boolean isValueDisplayedAsPercentage() {
		return getFirstProperty().isValueDisplayedAsPercentage();
	}
}
