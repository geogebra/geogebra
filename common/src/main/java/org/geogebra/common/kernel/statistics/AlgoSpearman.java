/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds Spearman's correlation coefficient from a list of points or two lists
 * of numbers.
 * 
 * @author G. Sturr
 */
public class AlgoSpearman extends AlgoElement {

	// input
	private GeoList geoListPts;
	private GeoList geoListX;
	private GeoList geoListY;
	// output
	private GeoNumeric result;
	private SpearmansCorrelation sp;

	private double[] valX;
	private double[] valY;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoListX
	 *            list of numbers
	 * @param geoListY
	 *            list of numbers
	 */
	public AlgoSpearman(Construction cons, String label, GeoList geoListX,
			GeoList geoListY) {
		super(cons);
		this.geoListX = geoListX;
		this.geoListY = geoListY;
		this.geoListPts = null;
		result = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list of points
	 * @param label
	 *            output label
	 */
	public AlgoSpearman(Construction cons, String label, GeoList geoList) {
		this(cons, geoList);
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list of points
	 */
	public AlgoSpearman(Construction cons, GeoList geoList) {
		super(cons);
		this.geoListX = null;
		this.geoListY = null;
		this.geoListPts = geoList;

		result = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Spearman;
	}

	@Override
	protected void setInputOutput() {

		if (geoListPts != null) {
			input = new GeoElement[1];
			input[0] = geoListPts;
		} else {
			input = new GeoElement[2];
			input[0] = geoListX;
			input[1] = geoListY;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {
		if (input.length == 1) {
			// input is single list of points
			int size = geoListPts.size();
			if (!geoListPts.isDefined() || size < 2) {
				result.setUndefined();
				return;
			}

			valX = new double[size];
			valY = new double[size];

			for (int i = 0; i < size; i++) {
				GeoElement geo = geoListPts.get(i);
				if (geo instanceof GeoPoint) {
					double x = ((GeoPoint) geo).getX();
					double y = ((GeoPoint) geo).getY();
					double z = ((GeoPoint) geo).getZ();
					valX[i] = x / z;
					valY[i] = y / z;
				} else {
					result.setUndefined();
					return;
				}
			}

		} else {
			// input is two lists
			int sizeX = geoListX.size();
			int sizeY = geoListY.size();
			if (!geoListX.isDefined() || !geoListY.isDefined() || sizeX < 2
					|| sizeX != sizeY) {
				result.setUndefined();
				return;
			}

			valX = new double[sizeX];
			valY = new double[sizeX];

			for (int i = 0; i < sizeX; i++) {
				GeoElement geox = geoListX.get(i);
				GeoElement geoy = geoListY.get(i);
				valX[i] = geox.evaluateDouble();
				valY[i] = geoy.evaluateDouble();
				if (Double.isNaN(valX[i]) || Double.isNaN(valY[i])) {
					result.setUndefined();
					return;
				}
			}
		}

		if (sp == null) {
			sp = new SpearmansCorrelation();
		}

		result.setValue(sp.correlation(valX, valY));
	}

}
