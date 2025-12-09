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

package org.geogebra.common.kernel.interval;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;

public class SamplerTest extends BaseUnitTest {

	private static final int DEFAULT_NUMBER_OF_SAMPLES = 100;

	protected IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax) {
		return functionValues(functionDescription, xmin, xmax, ymin, ymax,
				DEFAULT_NUMBER_OF_SAMPLES);
	}

	protected IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax,
			int numberOfSamples) {
		GeoFunction function = add(functionDescription);
		IntervalTuple range = PlotterUtils.newRange(xmin, xmax, ymin, ymax);
		FunctionSampler sampler = PlotterUtils.newSampler(function, range,
				numberOfSamples, null);

		return sampler.tuples();
	}

}
