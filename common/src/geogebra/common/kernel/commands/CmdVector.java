package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;


/**
 * Vector[ <GeoPoint>, <GeoPoint> ] Vector[ <GeoPoint> ]
 */
public class CmdVector extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1 :
			arg = resArgs(c);
			if (arg[0] .isGeoPoint()) {
				GeoElement[] ret =
				{ getAlgoDispatcher().Vector(c.getLabel(), (GeoPointND) arg[0])};
				return ret;
			}

			/*
			 * wrap a vector as a vector. needed for vectors that are defined
			 * through points: e.g. v = B - A used in AlgoLinPointVector
			 * 
			 * @see AlgoLinePointVector.getCmdXML()
			 */
			else if (arg[0] .isGeoVector()) {
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
				throw argErr(app, c.getName(), arg[0]);

		case 2 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoPoint()))) {
				GeoElement[] ret =
				{
						getAlgoDispatcher().Vector(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoPoint) arg[1])};
				return ret;
			}
			
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}