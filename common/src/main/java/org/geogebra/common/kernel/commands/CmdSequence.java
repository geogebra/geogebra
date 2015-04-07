package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Sequence[ <expression>, <number-var>, <from>, <to> ] Sequence[ <expression>,
 * <number-var>, <from>, <to>, <step> ] Sequence[ <number-var>]
 */
public class CmdSequence extends CommandProcessor {
	/**
	 * Creates new sequence command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSequence(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		// avoid
		// "Command Sequence not known eg Sequence[If[Element[list1,i]=="b",0,1]]
		if (n != 4 && n != 5 && n != 1)
			throw argNumErr(app, c.getName(), n);

		boolean[] ok = new boolean[n];

		// create local variable at position 1 and resolve arguments
		GeoElement[] arg;
		if (n > 1)
			arg = resArgsLocalNumVar(c, 1, 2);
		else
			arg = resArgs(c);
		switch (n) {
		case 1:
			if (arg[0] instanceof GeoNumberValue) {

				AlgoSequence algo = new AlgoSequence(cons, c.getLabel(),
						(GeoNumberValue) arg[0]);
				return algo.getOutput();
			}
			throw argErr(app, c.getName(), arg[0]);
		case 4:

			// make sure Sequence[i,i,i,i] gives an error
			checkDependency(arg, c.getName(), 2, 1);
			checkDependency(arg, c.getName(), 3, 1);

			if ((ok[0] = arg[0].isGeoElement())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)) {

				AlgoSequence algo = new AlgoSequence(cons, c.getLabel(),
						arg[0], (GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], null);

				return algo.getOutput();
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 5:
			// make sure Sequence[i,i,i,i,i] gives an error
			checkDependency(arg, c.getName(), 2, 1);
			checkDependency(arg, c.getName(), 3, 1);
			checkDependency(arg, c.getName(), 4, 1);

			if ((ok[0] = arg[0].isGeoElement())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)) {

				AlgoSequence algo = new AlgoSequence(cons, c.getLabel(),
						arg[0], (GeoNumeric) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3], (GeoNumberValue) arg[4]);
				return algo.getOutput();

			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}