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

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoLineGraph;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.Nullable;

/**
 * Property for the x/y coordinates lists of a line graph.
 */
public class LineGraphCoordinatesProperty extends AbstractValuedProperty<String>
		implements StringProperty {

	/**
	 * Coordinate represented by this property.
	 */
	public enum Axis {
		X("ListOfXCoordinates", 0),
		Y("ListOfYCoordinates", 1);

		private final String propertyName;
		private final int algoParamIndex;

		Axis(String propertyName, int algoParamIndex) {
			this.propertyName = propertyName;
			this.algoParamIndex = algoParamIndex;
		}
	}

	private final GeoFunction geoFunction;
	private final AlgoLineGraph algoLineGraph;
	private final Axis axis;

	/**
	 * Creates a coordinate-list property.
	 * @param localization localization
	 * @param geoElement chart data function
	 * @param axis coordinate represented by the property
	 * @throws NotApplicablePropertyException if {@code geoElement} is not a function produced by
	 * an {@code AlgoLineGraph}.
	 */
	public LineGraphCoordinatesProperty(Localization localization, GeoElement geoElement, Axis axis)
			throws NotApplicablePropertyException {
		super(localization, axis.propertyName);
		if (!(geoElement instanceof GeoFunction function
				&& function.getParentAlgorithm() instanceof AlgoLineGraph algo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoFunction = function;
		this.algoLineGraph = algo;
		this.axis = axis;
	}

	@Override
	public @Nullable String getValue() {
		return algoLineGraph.getInput(axis.algoParamIndex).toGeoElement()
				.getLabel(StringTemplate.editorTemplate);
	}

	@Override
	public @Nullable String validateValue(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		GeoList list = algoLineGraph.getKernel().getAlgebraProcessor().evaluateToList(value);
		if (list != null) {
			return null;
		}
		return algoLineGraph.getKernel().getLocalization().getError("InvalidInput");
	}

	@Override
	protected void doSetValue(String value) {
		CommandRedefineHelper.redefineWithParam(geoFunction, algoLineGraph, axis.algoParamIndex,
				value, algoLineGraph.getKernel().getApplication());
	}

	@Override
	public boolean isDisplayedInMathFormat() {
		return true;
	}
}
