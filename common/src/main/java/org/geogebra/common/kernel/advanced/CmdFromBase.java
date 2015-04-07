package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ToBase[&lt;base>, &lt;Number>]
 */
public class CmdFromBase extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFromBase(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			boolean oldMacroMode = cons.isSuppressLabelsActive();

			// following part is very similar to normal resArgs,
			// but allows autocorrection for eg FromBase[2,101010]
			cons.setSuppressLabelCreation(true);

			// resolve arguments to get GeoElements
			ExpressionNode[] argE = c.getArguments();
			GeoElement[] arg = new GeoElement[2];

			argE[1].resolveVariables();
			arg[1] = resArg(argE[1])[0];
			if (!(arg[1] instanceof GeoNumberValue))
				throw argErr(app, c.getName(), arg[1]);
			String str = argE[0].toString(StringTemplate.defaultTemplate);
			try {
				argE[0].resolveVariables();
				arg[0] = resArg(argE[0])[0];
			} catch (Throwable t) {
				// do nothing
			}
			if (!(arg[0] instanceof GeoText)) {
				arg[0] = new GeoText(kernelA.getConstruction(), str);
			}

			cons.setSuppressLabelCreation(oldMacroMode);

			AlgoFromBase fromBase = new AlgoFromBase(cons, c.getLabel(),
					(GeoText) arg[0], (GeoNumberValue) arg[1]);

			GeoElement[] ret = { fromBase.getResult() };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
