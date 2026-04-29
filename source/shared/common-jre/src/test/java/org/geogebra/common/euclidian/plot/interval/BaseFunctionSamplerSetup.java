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

package org.geogebra.common.euclidian.plot.interval;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;

public class BaseFunctionSamplerSetup extends BaseAppTestSetup {

	private EuclidianViewBoundsMock bounds;

	@BeforeEach
	void setUp() {
		setupClassicApp();
	}

	protected QueryFunctionData query(String definition) {
		assertNotNull(bounds, "Define bounds and screen");
		GeoFunction function = evaluateGeoElement(definition);
		IntervalTupleList tuples = new IntervalTupleList();
		IntervalFunctionData data = new IntervalFunctionData(function,
				new GeoFunctionConverter(), bounds, tuples);
		new FunctionSampler(data, bounds).resample(bounds.domain());

		return new QueryFunctionDataImpl(tuples);
	}

	protected void withDefaultScreen() {
		withBounds(-15, 15, -8, -8);
		withScreenSize(1920, 1250);
	}

	protected void withHighZoomScreen() {
		withBounds(-1E15, 1E15, -1E15, 1E15);
		withScreenSize(1920, 1250);
	}

	protected void withBounds(double xmin, double xmax, double ymin, double ymax) {
		bounds = new EuclidianViewBoundsMock(xmin, xmax, ymin, ymax);
	}

	private void withScreenSize(int width, int height) {
		bounds.setSize(width, height);
	}
}
