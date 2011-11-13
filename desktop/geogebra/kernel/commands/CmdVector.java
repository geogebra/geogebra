package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/*
 * Vector[ <GeoPoint>, <GeoPoint> ] Vector[ <GeoPoint> ]
 */
public class CmdVector extends CommandProcessor {

	public CmdVector(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1 :
			arg = resArgs(c);
			if (ok[0] = (arg[0] .isGeoPoint())) {
				GeoElement[] ret =
				{ kernel.Vector(c.getLabel(), (GeoPoint) arg[0])};
				return ret;
			}

			/*
			 * wrap a vector as a vector. needed for vectors that are defined
			 * through points: e.g. v = B - A used in AlgoLinPointVector
			 * 
			 * @see AlgoLinePointVector.getCmdXML()
			 */
			else if (ok[0] = (arg[0] .isGeoVector())) {
				// maybe we have to set a label here
				if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {            	           
					arg[0].setLabel(c.getLabel());

					// make sure that arg[0] is in construction list
					if (arg[0].isIndependent())
						cons.addToConstructionList(arg[0], true);
					else 
						cons.addToConstructionList(arg[0].getParentAlgorithm(), true);
				}       
				GeoElement[] ret = { arg[0] };
				return ret;
			} else
				throw argErr(app, "Vector", arg[0]);

		case 2 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoPoint()))) {
				GeoElement[] ret =
				{
						kernel.Vector(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoPoint) arg[1])};
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Vector", arg[0]);
				else
					throw argErr(app, "Vector", arg[1]);
			}

		default :
			throw argNumErr(app, "Vector", n);
		}
	}
}