/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * @author Kamalaruban Parameswaran
 * @version 2012-03-06
 */

public abstract class AlgoStats1DGrouped extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; // input
	private GeoList geoList2; // input
	public GeoNumeric Truncate; // input
	public GeoNumeric result; // output

	private int stat;

	final static int STATS_MEAN = 0;
	final static int STATS_VARIANCE = 1;
	final static int STATS_SIGMAX = 2;
	final static int STATS_SIGMAXX = 3;
	final static int STATS_SD = 4;
	final static int STATS_PRODUCT = 5;
	final static int STATS_SXX = 6;
	final static int STATS_SAMPLE_VARIANCE = 7;
	final static int STATS_SAMPLE_SD = 8;

	// OK
	public AlgoStats1DGrouped(Construction cons, String label, GeoList geoList,
			GeoList geoList2, int stat) {
		this(cons, label, geoList, geoList2, null, stat);
	}

	// OK
	AlgoStats1DGrouped(Construction cons, String label, GeoList geoList,
			GeoList geoList2, GeoNumeric Truncate, int stat) {
		this(cons, geoList, geoList2, Truncate, stat);
		result.setLabel(label);
	}

	// OK
	AlgoStats1DGrouped(Construction cons, GeoList geoList, GeoList geoList2,
			GeoNumeric Truncate, int stat) {
		super(cons);
		this.geoList = geoList;
		this.geoList2 = geoList2;
		this.stat = stat;
		this.Truncate = Truncate;

		if (geoList.size() > 0 && geoList.get(0).isAngle())
			result = new GeoAngle(cons);
		else
			result = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	// OK
	public AlgoStats1DGrouped(Construction cons, GeoList geoList,
			GeoList geoList2, int stat) {
		this(cons, geoList, geoList2, null, stat);
	}

	// OK
	public abstract Algos getClassName();

	// OK
	protected void setInputOutput() {
		if (Truncate == null) {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = geoList2;
		} else {
			input = new GeoElement[3];
			input[0] = geoList;
			input[1] = geoList2;
			input[2] = Truncate;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	// OK
	public GeoNumeric getResult() {
		return result;
	}

	public final void compute() {

		// TODO: remove
		// Application.debug("compute: " + geoList);
		if (!geoList.isDefined()) {
			result.setUndefined();
			return;
		}

		int truncate;
		int size = geoList.size();

		if (Truncate != null) {
			truncate = (int) Truncate.getDouble();
			if (truncate == 0) {
				result.setValue(0);
				return;
			}
			if (truncate < 1 || truncate > size) {
				result.setUndefined();
				return;
			}
			size = truncate; // truncate the list
		}

		if (size == 0) {
			switch (stat) {
			case STATS_SIGMAX:
			case STATS_SIGMAXX:
				result.setValue(0);
				return;
			case STATS_PRODUCT:
				result.setValue(1);
				return;
			default:
				result.setUndefined();
				return;
			}
		}

		double sumVal = 0;
		double sumFreq = 0;
		double sumSquares = 0;
		double product = 1;
		double val;
		double frequency;
		double val_by_freq;
		for (int i = 0; i < size; i++) {
			GeoElement geo = geoList.get(i);
			GeoElement geo2 = geoList2.get(i);
			if (geo instanceof NumberValue && geo2 instanceof NumberValue) {
				NumberValue num = (NumberValue) geo;
				NumberValue freq = (NumberValue) geo2;
				val = num.getDouble();
				frequency = freq.getDouble();
				val_by_freq = val * frequency;
				sumVal += val_by_freq;
				sumSquares += val * val_by_freq;
				sumFreq += frequency;
				product *= Math.pow(val, frequency);
			} else {
				result.setUndefined();
				return;
			}
		}

		double mu = sumVal / sumFreq; // OK
		double var;

		switch (stat) {
		case STATS_MEAN: // OK
			result.setValue(mu);
			break;
		case STATS_SD: // OK
			var = sumSquares / sumFreq - mu * mu;
			result.setValue(Math.sqrt(var));
			break;
		case STATS_SAMPLE_SD: // OK
			var = (sumSquares - sumVal * sumVal / sumFreq) / (sumFreq - 1);
			result.setValue(Math.sqrt(var));
			break;
		case STATS_VARIANCE: // OK
			var = sumSquares / sumFreq - mu * mu;
			result.setValue(var);
			break;
		case STATS_SAMPLE_VARIANCE: // OK
			var = (sumSquares - sumVal * sumVal / sumFreq) / (sumFreq - 1);
			result.setValue(var);
			break;
		case STATS_SXX: // OK
			var = sumSquares - (sumVal * sumVal) / sumFreq;
			result.setValue(var);
			break;
		case STATS_SIGMAX: // OK
			result.setValue(sumVal);
			break;
		case STATS_SIGMAXX: // OK
			result.setValue(sumSquares);
			break;
		case STATS_PRODUCT: // OK
			result.setValue(product);
			break;
		}
	}

}
