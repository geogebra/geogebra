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

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Histogram algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author M. Borcherds
 *
 */
public class AlgoHistogram extends AlgoFunctionAreaSums {

	/**
	 * Creates histogram from class boundaries and heights or data (no label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            list of boundaries
	 * @param list2
	 *            list of heights or raw data
	 * @param right
	 *            right histogram?
	 */
	public AlgoHistogram(Construction cons, GeoList list1, GeoList list2,
			boolean right) {
		super(cons, list1, list2, right);
	}

	/**
	 * Creates histogram from class boundaries and heights or data (has label)
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the histogram
	 * @param list1
	 *            list of boundaries
	 * @param list2
	 *            list of heights or raw data
	 * @param right
	 *            right histogram?
	 */
	public AlgoHistogram(Construction cons, String label, GeoList list1,
			GeoList list2, boolean right) {
		super(cons, label, list1, list2, right);
	}

	private AlgoHistogram(Construction cons, double[] vals, double[] borders,
			int N) {
		super(cons, vals, borders, N);
	}

	/**
	 * Creates histogram from data. Provides with optional features (has label)
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the histogram
	 * @param isCumulative
	 *            flag
	 * @param list1
	 *            list of boundaries
	 * @param list2
	 *            list of data
	 * @param list3
	 *            list of frequencies
	 * @param useDensity
	 *            flag
	 * @param density
	 *            density scaling factor
	 * @param right
	 *            flag
	 */
	public AlgoHistogram(Construction cons, String label,
			GeoBoolean isCumulative, GeoList list1, GeoList list2,
			GeoList list3, GeoBoolean useDensity, GeoNumeric density,
			boolean right) {
		super(cons, label, isCumulative, list1, list2, list3, useDensity,
				density, right);
	}

	private AlgoHistogram(GeoBoolean isCumulative, GeoBoolean useDensity,
			GeoNumeric density, double[] vals, double[] borders, int N) {
		super(isCumulative, useDensity, density, vals, borders, N);
	}

	/**
	 * Creates histogram from data. Provides with optional features (no label)
	 * 
	 * 
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            flag
	 * @param list1
	 *            list of boundaries
	 * @param list2
	 *            list of data
	 * @param list3
	 *            list of frequencies
	 * @param useDensity
	 *            flag
	 * @param density
	 *            density scaling factor
	 * @param right
	 *            flag
	 */
	public AlgoHistogram(Construction cons, GeoBoolean isCumulative,
			GeoList list1, GeoList list2, GeoList list3, GeoBoolean useDensity,
			GeoNumeric density, boolean right) {
		super(cons, isCumulative, list1, list2, list3, useDensity, density,
				right);
	}

	@Override
	public Commands getClassName() {
		return isRight() ? Commands.HistogramRight : Commands.Histogram;
	}

	@Override
	public AlgoHistogram copy() {
		int N = getIntervals();
		if (getType() == SumType.HISTOGRAM_DENSITY) {
			return new AlgoHistogram((GeoBoolean) getIsCumulative().copy(),
					(GeoBoolean) getUseDensityGeo().copy(),
					(GeoNumeric) getDensityGeo().copy(),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()),
					N);
		}
		return new AlgoHistogram(kernel.getConstruction(),
				Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
	}

}
