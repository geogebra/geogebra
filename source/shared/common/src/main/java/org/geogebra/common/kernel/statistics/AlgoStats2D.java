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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Mean, covariance, sum, sum of squares, etc from two lists or a list of points
 * adapted from AlgoListMin to replace AlgoMean, AlgoSum
 * 
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public abstract class AlgoStats2D extends AlgoElement {

	private GeoList geoListx; // input
	private GeoList geoListy; // input
	private GeoNumeric result; // output

	private int mode;

	final static int MODE_DOUBLELIST = 0;
	final static int MODE_LISTOFPOINTS = 1;

	private int stat;

	final static int STATS_MEANX = 0;
	final static int STATS_MEANY = 1;
	final static int STATS_COVARIANCE = 2;
	final static int STATS_SIGMAXY = 3;
	final static int STATS_SXX = 4;
	final static int STATS_SYY = 5;
	final static int STATS_SXY = 6;
	final static int STATS_PMCC = 7;
	final static int STATS_SIGMAXX = 8;
	final static int STATS_SIGMAYY = 9;
	final static int STATS_SAMPLESDX = 10;
	final static int STATS_SAMPLESDY = 11;
	final static int STATS_SDX = 12;
	final static int STATS_SDY = 13;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoListx
	 *            x datapoints
	 * @param geoListy
	 *            y datapoints
	 * @param stat
	 *            stat type
	 */
	public AlgoStats2D(Construction cons, String label, GeoList geoListx,
			GeoList geoListy, int stat) {
		super(cons);
		mode = MODE_DOUBLELIST;
		this.geoListx = geoListx;
		this.geoListy = geoListy;
		this.stat = stat;

		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoListx
	 *            list of points
	 * @param stat
	 *            stat type
	 */
	public AlgoStats2D(Construction cons, String label, GeoList geoListx,
			int stat) {
		this(cons, geoListx, stat);
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoListx
	 *            list of points
	 * @param stat
	 *            stat type
	 */
	public AlgoStats2D(Construction cons, GeoList geoListx, int stat) {
		super(cons);
		mode = MODE_LISTOFPOINTS;
		this.geoListx = geoListx;
		this.stat = stat;

		result = new GeoNumeric(cons);

		// setInputOutput();
		input = new GeoElement[1];
		input[0] = geoListx;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
		compute();
	}

	@Override
	protected final void setInputOutput() {
		input = new GeoElement[2];
		input[0] = geoListx;
		input[1] = geoListy;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return statistic
	 */
	public GeoNumeric getResult() {
		return result;
	}

	@Override
	final public void compute() {
		double sumx = 0;
		double sumy = 0;
		double sumxx = 0;
		double sumxy = 0;
		double sumyy = 0;
		double valx, valy;
		int sizex = geoListx.size();
		int sizey = sizex;
		if (mode == MODE_DOUBLELIST) {
			sizey = geoListy.size();
			if (!geoListx.isDefined() || !geoListy.isDefined() || sizex == 0
					|| sizex != sizey) {
				result.setUndefined();
				return;
			}

			for (int i = 0; i < sizex; i++) {
				GeoElement geox = geoListx.get(i);
				GeoElement geoy = geoListy.get(i);
				if (geox instanceof NumberValue
						&& geoy instanceof NumberValue) {
					valx = geox.evaluateDouble();
					valy = geoy.evaluateDouble();
					sumx += valx;
					sumy += valy;
					sumxx += valx * valx;
					sumyy += valy * valy;
					sumxy += valx * valy;
				} else {
					result.setUndefined();
					return;
				}
			}
		} else { // MODE_LISTOFPOINTS
			for (int i = 0; i < sizex; i++) {
				GeoElement geo = geoListx.get(i);
				if (geo.isGeoPoint()) {
					Coords coords = ((GeoPointND) geo).getInhomCoordsInD3();
					double x = coords.getX();
					double y = coords.getY();

					valx = x;
					valy = y;
					sumx += valx;
					sumy += valy;
					sumxx += valx * valx;
					sumyy += valy * valy;
					sumxy += valx * valy;
				} else {
					result.setUndefined();
					return;
				}
			}

		}

		double mux = sumx / sizex;
		double muy = sumy / sizex;
		double var;

		switch (stat) {
		default:
			result.setValue(Double.NaN);
			break;
		case STATS_MEANX:
			result.setValue(mux);
			break;
		case STATS_MEANY:
			result.setValue(muy);
			break;
		case STATS_COVARIANCE:
			result.setValue(sumxy / sizex - mux * muy);
			break;
		case STATS_SIGMAXY:
			result.setValue(sumxy);
			break;
		case STATS_SIGMAXX:
			result.setValue(sumxx);
			break;
		case STATS_SIGMAYY:
			result.setValue(sumyy);
			break;
		case STATS_SXX:
			result.setValue(sumxx - sumx * sumx / sizex);
			break;
		case STATS_SYY:
			result.setValue(sumyy - sumy * sumy / sizex);
			break;
		case STATS_SXY:
			result.setValue(sumxy - sumx * sumy / sizex);
			break;
		case STATS_PMCC:
			result.setValue((sumxy * sizex - sumx * sumy)
					/ Math.sqrt((sumxx * sizex - sumx * sumx)
							* (sumyy * sizex - sumy * sumy)));
			break;
		case STATS_SAMPLESDX:
			var = (sumxx - sumx * sumx / sizex) / (sizex - 1);
			result.setValue(Math.sqrt(var));
			break;
		case STATS_SAMPLESDY:
			var = (sumyy - sumy * sumy / sizey) / (sizey - 1);
			result.setValue(Math.sqrt(var));
			break;
		case STATS_SDX:
			var = (sumxx - sumx * sumx / sizex) / sizex;
			result.setValue(Math.sqrt(var));
			break;
		case STATS_SDY:
			var = (sumyy - sumy * sumy / sizey) / sizey;
			result.setValue(Math.sqrt(var));
			break;
		}
	}

}
