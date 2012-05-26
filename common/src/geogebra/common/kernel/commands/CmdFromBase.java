package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * ToBase[&lt;base>, &lt;Number>]
 */
public class CmdFromBase extends CommandProcessor{
	

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
			boolean[] ok = new boolean[n];
			

			switch (n) {
			case 2:
				boolean oldMacroMode = cons.isSuppressLabelsActive();
				
				//following part is very similar to normal resArgs,
				//but allows autocorrection for eg FromBase[2,101010]
				cons.setSuppressLabelCreation(true);

				// resolve arguments to get GeoElements
				ExpressionNode[] argE = c.getArguments();
				GeoElement[] arg = new GeoElement[2];
				
				argE[0].resolveVariables();
				arg[0] = resArg(argE[0])[0];
				if(!arg[0].isNumberValue())
					throw argErr(app, c.getName(), arg[0]);
				String str = argE[1].toString(StringTemplate.defaultTemplate);
				try{
					argE[1].resolveVariables();
					arg[1] = resArg(argE[0])[1];
				}
				catch(Throwable t){
					//do nothing
				}
				if(!(arg[1] instanceof GeoText)){
					arg[1] = new GeoText(kernelA.getConstruction(),str);
				}
				
				cons.setSuppressLabelCreation(oldMacroMode);
				
				
				if ((ok[0] = (arg[0].isNumberValue()))
						&& (ok[1] = (arg[1].isGeoText()))) {
					GeoElement[] ret = { kernelA.FromBase(c.getLabel(),
							(NumberValue) arg[0], (GeoText) arg[1]) };
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
