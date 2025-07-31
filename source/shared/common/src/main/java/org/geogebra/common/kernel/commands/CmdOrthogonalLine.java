package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoOrthoLinePointConic;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.main.MyError;

/**
 * Orthogonal[ &lt;GeoPoint&gt;, &lt;GeoVector&gt; ]
 * 
 * Orthogonal[ &lt;GeoPoint&gt;, &lt;GeoLine&gt; ]
 */
public class CmdOrthogonalLine extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOrthogonalLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {

		case 3:
			return process3(c, info);
		case 2:
			return process2(c, resArgs(c, info));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param c
	 *            command
	 * @param arg
	 *            resolved arguments
	 * @return process for 2 arguments
	 */
	protected GeoElement[] process2(Command c, GeoElement[] arg) {
		boolean[] ok = new boolean[2];
		// line through point orthogonal to vector
		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1].isGeoVector()))) {
			GeoElement[] ret = { getAlgoDispatcher().orthogonalLine(
					c.getLabel(), (GeoPoint) arg[0], (GeoVector) arg[1]) };
			return ret;
		}

		// line through point orthogonal to another line
		else if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1] instanceof Lineable2D))) {
			GeoElement[] ret = { getAlgoDispatcher().orthogonalLine(
					c.getLabel(), (GeoPoint) arg[0], (Lineable2D) arg[1]) };
			return ret;
		} else if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1].isGeoConic()))) {

			AlgoOrthoLinePointConic algo = new AlgoOrthoLinePointConic(cons,
					c.getLabel(), (GeoPoint) arg[0], (GeoConic) arg[1]);

			return algo.getOutput();
		}

		// syntax error
		throw argErr(c, getBadArg(ok, arg));
	}

	/**
	 * @param c
	 *            command
	 * @return process for 3 arguments
	 */
	protected GeoElement[] process3(Command c, EvalInfo info) {
		ExpressionValue arg2 = c.getArgument(2).unwrap();

		// check if arg2 = xOyPlane
		String name = arg2.toString(StringTemplate.defaultTemplate);
		if (!(arg2 instanceof Variable) || !planeOrSpace(name)) {
			throw argNumErr(c);
		}
		c.setArgument(2, new GeoDummyVariable(cons, name).wrap());
		return process2(c, resArgs(c, info));
	}

	private boolean planeOrSpace(String name) {
		return "xOyPlane".equals(name) || loc.getMenu("xOyPlane").equals(name)
				|| "space".equals(name) || loc.getMenu("space").equals(name);
	}
}
