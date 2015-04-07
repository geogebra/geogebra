package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDrawingPadCorner;
import org.geogebra.common.kernel.algos.AlgoImageCorner;
import org.geogebra.common.kernel.algos.AlgoTextCorner;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.algos.AlgoVertexIneq;
import org.geogebra.common.kernel.algos.AlgoVertexPolygon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Vertex[ <GeoConic> ]
 */
public class CmdVertex extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVertex(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// Vertex[ <GeoConic> ]
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoConic()) {

				AlgoVertexConic algo = newAlgoVertexConic(cons, c.getLabels(),
						(GeoConicND) arg[0]);

				return (GeoElement[]) algo.getVertex();
			}
			if (arg[0] instanceof GeoPoly) {

				AlgoVertexPolygon algo = kernelA.getAlgoDispatcher()
						.newAlgoVertexPolygon(cons, c.getLabels(),
								(GeoPoly) arg[0]);

				return algo.getVertex();
			}
			if (arg[0] instanceof GeoFunctionNVar) {

				AlgoVertexIneq algo = new AlgoVertexIneq(cons, c.getLabels(),
						(GeoFunctionNVar) arg[0]);

				return algo.getVertex();
			} else if (arg[0] instanceof GeoNumberValue) {
				GeoElement[] ret = { (GeoElement) cornerOfDrawingPad(
						c.getLabel(), (GeoNumberValue) arg[0], null) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			// Corner[ <Image>, <number> ]
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoPoly))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {

				AlgoVertexPolygon algo = newAlgoVertexPolygon(cons,
						c.getLabel(), (GeoPoly) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { (GeoElement) algo.getOneVertex() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoImage()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {

				AlgoImageCorner algo = new AlgoImageCorner(cons, c.getLabel(),
						(GeoImage) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getCorner() };
				return ret;
			}
			// Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
			// Corner[ <Text>, <number> ]
			else if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {

				AlgoTextCorner algo = new AlgoTextCorner(cons, c.getLabel(),
						(GeoText) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getCorner() };
				return ret;
				// Michael Borcherds 2007-11-26 END
			} else if ((ok[0] = (arg[0] instanceof GeoNumberValue))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				GeoElement[] ret = { (GeoElement) cornerOfDrawingPad(
						c.getLabel(), (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[0]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	protected GeoPointND cornerOfDrawingPad(String label, NumberValue number,
			NumberValue ev) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner(cons, label,
				number, ev);
		return algo.getCorner();
	}

	/**
	 * @param cons
	 * @param label
	 * @param p
	 * @param v
	 * @return algo for one of the corners of a polygon/polyline
	 */
	protected AlgoVertexPolygon newAlgoVertexPolygon(Construction cons,
			String label, GeoPoly p, GeoNumberValue v) {
		return new AlgoVertexPolygon(cons, label, p, v);
	}

	/**
	 * @param cons
	 * @param labels
	 * @param conic
	 * @return algo for "corners" of a conic
	 */
	protected AlgoVertexConic newAlgoVertexConic(Construction cons,
			String[] labels, GeoConicND conic) {
		return new AlgoVertexConic(cons, labels, conic);
	}

}
