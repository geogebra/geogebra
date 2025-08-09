package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Vector[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Vector[ &lt;GeoPoint&gt; ]
 */
public class CmdVector extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { (GeoElement) getAlgoDispatcher()
						.vector(c.getLabel(), (GeoPointND) arg[0]) };
				return ret;
			}

			/*
			 * wrap a vector as a vector. needed for vectors that are defined
			 * through points: e.g. v = B - A used in AlgoLinPointVector
			 * 
			 * @see AlgoLinePointVector.getCmdXML()
			 */
			else if (arg[0].isGeoVector()) {
				// maybe we have to set a label here
				if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {
					arg[0].setLabel(c.getLabel());

					// make sure that arg[0] is in construction list
					if (arg[0].isIndependent()) {
						cons.addToConstructionList(arg[0], true);
					} else {
						cons.addToConstructionList(arg[0].getParentAlgorithm(),
								true);
					}
				}
				GeoElement[] ret = { arg[0] };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = { vector(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1]) };
				return ret;
			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * 
	 * @param label
	 *            vector name
	 * @param p0
	 *            first point
	 * @param p1
	 *            second point
	 * @return vector (p0,p1)
	 */
	protected GeoElement vector(String label, GeoPointND p0, GeoPointND p1) {
		return getAlgoDispatcher().vectorND(label, p0, p1);
	}
}