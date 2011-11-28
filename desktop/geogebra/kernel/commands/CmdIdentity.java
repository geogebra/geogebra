package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;

/**
 * Identity[<number>]
 */
class CmdIdentity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIdentity(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n != 1)
			throw argNumErr(app, c.getName(), n);
		if (!arg[0].isNumberValue())
			throw argErr(app, c.getName(), arg[0]);
		
		StringBuilder sb = new StringBuilder();
		int order = (int)Math.round(((NumberValue)arg[0]).getDouble());
		
		if (order < 1)
			throw argErr(app, c.getName(), arg[0]);
		String label = c.getLabel();
		if (label != null) {
			sb.append(label);
			sb.append('=');
		}
		sb.append('{');
		
		for (int i = 0 ; i < order ; i++) {
			sb.append('{');
			for (int j = 0 ; j < order ; j++) {
			sb.append(i == j ? '1' : '0');
			if (j < order - 1) sb.append(',');
			}
			sb.append(i == order - 1 ? "}" : "},");
		}
		sb.append('}');
		
		kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(sb.toString()
								, true);
		return new GeoElement[] {};

	}
}
