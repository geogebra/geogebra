package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.prover.AlgoEnvelope;
import org.geogebra.common.main.MyError;

/**
 * Envelope[&lt;Object&gt;, &lt;Mover point&gt;]
 */
public class CmdEnvelope extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEnvelope(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c, info);
		GeoPoint movingPoint;
		GeoElement linear;

		switch (n) {
		case 2:
			if ((ok[0] = arg[0].isPath())
					&& (ok[1] = arg[1].isGeoPoint())) {
				linear = arg[0];
				movingPoint = (GeoPoint) arg[1];
			} else {
				throw argErr(c, getBadArg(ok, arg));
			}
			break;

		default:
			throw argNumErr(c);
		}

		return new GeoElement[] {
				envelope(c.getLabel(), (Path) linear, movingPoint)
						.toGeoElement() };
	}

	/**
	 * locus equation for Q dependent on P.
	 * 
	 * @param label
	 *            output label
	 * @param linear
	 *            dependent path
	 * @param movingPoint
	 *            moving point
	 * @return implicit curve
	 */
	final public GeoImplicit envelope(String label, Path linear,
			GeoPoint movingPoint) {
		// TODO: add check here if linear is a correct input
		if (movingPoint.getPath() == null || !movingPoint.isParentOf(linear)) {
			return null;
		}
		AlgoEnvelope algo = new AlgoEnvelope(cons, linear, movingPoint);
		GeoImplicit poly = algo.getPoly();

		poly.setLabel(label);
		return poly;
	}

}