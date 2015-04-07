package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Sequence[ <expression>, <number-var>, <from>, <to> ] Sequence[ <expression>,
 * <number-var>, <from>, <to>, <step> ] Sequence[ <number-var>]
 */
public class CmdZip extends CommandProcessor {
	/**
	 * Creates new zip command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZip(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		// avoid
		// "Command Sequence not known eg Sequence[If[Element[list1,i]=="b",0,1]]
		if (n < 3)
			throw argNumErr(app, c.getName(), n);

		// create local variable at position 1 and resolve arguments
		GeoElement arg = null;
		GeoElement[] vars = new GeoElement[n / 2];
		GeoList[] over = new GeoList[(n - 1) / 2];
		boolean oldval = cons.isSuppressLabelsActive();
		try {
			cons.setSuppressLabelCreation(true);
			arg = resArgsForZip(c, vars, over);
		} finally {
			for (GeoElement localVar : vars) {
				if (localVar != null)
					cons.removeLocalVariable(localVar
							.getLabel(StringTemplate.defaultTemplate));
			}
			cons.setSuppressLabelCreation(oldval);
		}

		AlgoZip algo = new AlgoZip(cons, c.getLabel(), arg, vars, over);
		return algo.getOutput();

	}

}
