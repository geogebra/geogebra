package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.kernel.AbstractKernel;

/**
 *LineStyle
 */
public class CmdLineStyle extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineStyle(AbstractKernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int style = (int) ((NumberValue) arg[1]).getDouble();
				Integer[] types = EuclidianViewInterface2D.getLineTypes();

				//For invalid number we assume it's 0
				//We do this also for SetPointStyle
				 
				if (style < 0 || style >= types.length)
					style = 0;
				
				arg[0].setLineType(types[style].intValue());
				arg[0].updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
