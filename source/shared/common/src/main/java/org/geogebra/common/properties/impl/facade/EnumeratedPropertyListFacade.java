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

import javax.annotation.Nonnull;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.ValueFilter;

abstract public class EnumeratedPropertyListFacade<T extends EnumeratedProperty<S>, S>
		extends AbstractValuedPropertyListFacade<T, S> implements EnumeratedProperty<S> {

	public EnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public @Nonnull List<S> getValues() {
		return getFirstProperty().getValues();
	}

	@Override
	public int getIndex() {
		return getFirstProperty().getIndex();
	}

	@Override
	public void setIndex(int index) {
		setValue(getValues().get(index));
	}

	@Override
	public void addValueFilter(@Nonnull ValueFilter valueFilter) {
		getFirstProperty().addValueFilter(valueFilter);
	}

	@Override
	public void removeValueFilter(@Nonnull ValueFilter valueFilter) {
		getFirstProperty().removeValueFilter(valueFilter);
	}

	@Override
	public int[] getGroupDividerIndices() {
		return getFirstProperty().getGroupDividerIndices();
	}
}
