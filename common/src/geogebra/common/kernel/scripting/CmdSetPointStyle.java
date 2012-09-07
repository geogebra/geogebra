package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.main.MyError;

/**
 *SetPointStyle
 */
public class CmdSetPointStyle extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointStyle(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if ((ok = arg[0] instanceof PointProperties) && arg[1].isNumberValue()) {

				PointProperties point = (PointProperties) arg[0];

				int style = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointStyle(style);
				point.updateRepaint();

				
				return;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
