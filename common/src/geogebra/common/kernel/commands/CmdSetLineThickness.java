package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 *SetLineThickness
 */
public class CmdSetLineThickness extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLineThickness(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1].isNumberValue()) {

				int thickness = (int) ((NumberValue) arg[1]).getDouble();

				arg[0].setLineThickness(thickness);
				arg[0].updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
