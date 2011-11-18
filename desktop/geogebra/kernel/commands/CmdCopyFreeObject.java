package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.main.MyError;

/**
 *CopyFreeObject
 */
class CmdCopyFreeObject extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCopyFreeObject(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			String label = c.getLabel();
			if (arg[0].isGeoFunctionConditional()|| arg[0].isGeoFunctionNVar() || arg[0].isGeoFunction()) {
				String command = label == null ? "" : label + "="; 

				kernel.setTemporaryPrintFigures(15); 
				command += arg[0].toOutputValueString(); 
				kernel.restorePrintAccuracy(); 

				try { 

					GeoElement[] ret = kernel.getAlgebraProcessor() 
							.processAlgebraCommandNoExceptions(command, true); 

					ret[0].setVisualStyle(arg[0]); 

					return ret; 

				} catch (Exception e) { 
					e.printStackTrace(); 
					throw argErr(app, c.getName(), arg[0]); 
				} 
			} else {
				GeoElement geo = arg[0].copy();
				geo.setLabel(label);
				GeoElement[] ret = { geo };
				return ret;
			}


			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
