package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoToBase;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * ToBase[&lt;base>, &lt;Number>]
 */
public class CmdToBase extends CommandProcessor{
	

		/**
		 * Create new command processor
		 * 
		 * @param kernel
		 *            kernel
		 */
		public CmdToBase(Kernel kernel) {
			super(kernel);
		}

		@Override
		final public GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean[] ok = new boolean[n];
			GeoElement[] arg;

			switch (n) {
			case 2:
				arg = resArgs(c);
				if ((ok[0] = (arg[0].isNumberValue()))
						&& (ok[1] = (arg[1].isNumberValue()))) {
					
					AlgoToBase toBase = new AlgoToBase(cons,c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]);

					GeoElement[] ret = { toBase.getResult() };
					return ret;
				}
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	

}
