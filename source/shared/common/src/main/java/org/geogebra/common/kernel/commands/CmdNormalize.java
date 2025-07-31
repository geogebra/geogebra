package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoNormalize;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Normalize[ &lt;List&gt; ]
 * 
 * @author Oana Niculaescu
 */
public class CmdNormalize extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNormalize(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoList()) {

				GeoElement[] ret = {
						normalize(c.getLabel(), (GeoList) arg[0]) };
				return ret;

			} else if (!(arg[0] instanceof VectorValue)) {
				throw argErr(c, arg[0]);
			}

		default:

			// try to create list of points (eg FitExp[])
			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { normalize(c.getLabel(), list) };
				return ret;
			}

			throw argNumErr(c);
		}
	}

	/**
	 * Normalize[list]
	 */
	final private GeoList normalize(String label, GeoList list) {
		AlgoNormalize algo = new AlgoNormalize(cons, list);
		GeoList list2 = algo.getResult();
		list2.setLabel(label);
		return list2;
	}

}
