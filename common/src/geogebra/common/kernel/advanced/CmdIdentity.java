package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Identity[<number>]
 */
public class CmdIdentity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIdentity(Kernel kernel) {
		super(kernel);
	}

	@Override
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
		
		kernelA.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(sb.toString()
								, true);
		return new GeoElement[] {};

	}
}
