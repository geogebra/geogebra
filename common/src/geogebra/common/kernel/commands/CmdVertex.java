package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoDrawingPadCorner;
import geogebra.common.kernel.advanced.AlgoImageCorner;
import geogebra.common.kernel.advanced.AlgoTextCorner;
import geogebra.common.kernel.advanced.AlgoVertex;
import geogebra.common.kernel.advanced.AlgoVertexIneq;
import geogebra.common.kernel.advanced.AlgoVertexPolygon;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// Vertex[ <GeoConic> ]
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoConic()) {
				
				AlgoVertex algo = new AlgoVertex(cons, c.getLabels(), (GeoConic) arg[0]);

				return algo.getVertex();
			}
			if (arg[0] instanceof GeoPoly) {
				
				AlgoVertexPolygon algo = new AlgoVertexPolygon(cons, c.getLabels(), (GeoPoly) arg[0]);

				return algo.getVertex();
			}
			if (arg[0] instanceof GeoFunctionNVar) {
				
				AlgoVertexIneq algo = new AlgoVertexIneq(cons,c.getLabels(), (GeoFunctionNVar) arg[0]);

				return algo.getVertex();
			}
			else if (arg[0].isNumberValue()) {
				GeoElement[] ret = { CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[0], null) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			// Corner[ <Image>, <number> ]
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoPoly))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				
				AlgoVertexPolygon algo = new AlgoVertexPolygon(cons, c.getLabel(),
						(GeoPoly) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getOneVertex() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoImage()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				
				AlgoImageCorner algo = new AlgoImageCorner(cons, c.getLabel(),
						(GeoImage) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getCorner() };
				return ret;
			}
			// Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
			// Corner[ <Text>, <number> ]
			else if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				
				AlgoTextCorner algo = new AlgoTextCorner(cons, c.getLabel(),
						(GeoText) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getCorner() };
				return ret;
				// Michael Borcherds 2007-11-26 END
			} else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[1], (NumberValue) arg[0]) };
				return ret;
				
			} else {
				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}


	/**
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	final private GeoPoint CornerOfDrawingPad(String label, NumberValue number,
			NumberValue ev) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner(cons, label,
				number, ev);
		return algo.getCorner();
	}
}
