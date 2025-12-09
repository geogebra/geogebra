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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * AttachCopyToView
 */
public class CmdAttachCopyToView extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAttachCopyToView(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 6:
		case 2:
			arg = resArgs(c, info);

			if (arg[1] instanceof GeoNumberValue) {
				GeoPointND corner1, corner3, screenCorner1, screenCorner3;
				int viewID = (int) arg[1].evaluateDouble();
				EuclidianView ev = null;
				if (viewID == 2) {
					// #5014
					if (app.hasEuclidianView2(1)) {
						ev = app.getEuclidianView2(1);
					}
				} else {
					ev = app.getEuclidianView1();
				}
				if (n == 2) {

					corner1 = new GeoPoint(kernel.getConstruction());
					corner3 = new GeoPoint(kernel.getConstruction());
					screenCorner1 = new GeoPoint(kernel.getConstruction());
					screenCorner3 = new GeoPoint(kernel.getConstruction());
					if (ev != null) {
						corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
						corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
						screenCorner1.setCoords(0, ev.getHeight(), 1);
						screenCorner3.setCoords(ev.getWidth(), 0, 1);
					}
				} else {
					if (arg[2].isGeoPoint()) {
						corner1 = (GeoPointND) arg[2];
					} else {
						throw argErr(c, arg[2]);
					}
					if (arg[3].isGeoPoint()) {
						corner3 = (GeoPointND) arg[3];
					} else {
						throw argErr(c, arg[3]);
					}
					if (arg[4].isGeoPoint()) {
						screenCorner1 = (GeoPointND) arg[4];
					} else {
						throw argErr(c, arg[4]);
					}
					if (arg[5].isGeoPoint()) {
						screenCorner3 = (GeoPointND) arg[5];
					} else {
						throw argErr(c, arg[5]);
					}
				}

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoPolyLine()
						|| arg[0].isGeoList()
						|| arg[0] instanceof GeoLocusStroke) {

					AlgoAttachCopyToView algo = new AlgoAttachCopyToView(cons,
							c.getLabel(), arg[0], (GeoNumberValue) arg[1],
							corner1, corner3, screenCorner1, screenCorner3);

					ret = new GeoElement[] { algo.getResult() };
					if (n == 2 && ev != app.getActiveEuclidianView()) {
						ret[0].addView(ev.getViewID());
						ret[0].removeView(
								app.getActiveEuclidianView().getViewID());
						app.getActiveEuclidianView().remove(ret[0]);
						ev.add(ret[0]);
					}
					return ret;
				}
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
