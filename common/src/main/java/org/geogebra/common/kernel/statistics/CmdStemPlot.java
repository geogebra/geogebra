package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * StemPlot
 * 
 * @author Michael Borcherds
 */
public class CmdStemPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStemPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (((arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				GeoElement[] ret = { StemPlot(c.getLabel(), list, null) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoNumeric()) {
				throw argErr(app, c.getName(), arg[1]);
			}

			GeoElement[] ret = { StemPlot(c.getLabel(), (GeoList) arg[0],
					(GeoNumeric) arg[1]) };
			return ret;

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:

			GeoList list = wrapInList(kernelA, arg, arg.length,
					GeoClass.DEFAULT);
			if (list != null) {
				GeoElement[] ret2 = { StemPlot(c.getLabel(), list, null) };
				return ret2;
			}

			throw argErr(app, c.getName(), arg[0]);
		}
	}

	/**
	 * StemPlot[list, number]
	 * 
	 * @param label
	 *            output label
	 * @param list
	 *            list
	 * @param num
	 *            scale adjustment
	 */
	private GeoText StemPlot(String label, GeoList list, GeoNumeric num) {
		AlgoStemPlot algo = new AlgoStemPlot(cons, label, list, num);
		GeoText text = algo.getResult();
		return text;
	}
}
