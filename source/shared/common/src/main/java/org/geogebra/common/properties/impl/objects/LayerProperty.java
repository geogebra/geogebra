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

package org.geogebra.common.properties.impl.objects;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for configuring the layer (z-order) of drawable elements.
 */
public class LayerProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final GeoElement element;

	/**
	 * Creates a layer property for the given element.
	 * @param localization the localization
	 * @param element the element
	 * @throws NotApplicablePropertyException if the element is not drawable
	 */
	public LayerProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Layer");
		if (!element.isDrawable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;

		setValues(IntStream
				.range(0, EuclidianStyleConstants.MAX_LAYERS + 1)
				.boxed()
				.collect(Collectors.toUnmodifiableList()));
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream().map(String::valueOf).toArray(String[]::new);
	}

	@Override
	protected void doSetValue(Integer value) {
		element.setLayer(value);
	}

	@Override
	public Integer getValue() {
		return element.getLayer();
	}
}
