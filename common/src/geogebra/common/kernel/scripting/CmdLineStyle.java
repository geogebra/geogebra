package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.main.MyError;

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
	public CmdLineStyle(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int style = (int) ((NumberValue) arg[1]).getDouble();
				Integer[] types = EuclidianView.getLineTypes();

				//For invalid number we assume it's 0
				//We do this also for SetPointStyle
				 
				if (style < 0 || style >= types.length)
					style = 0;
				
				arg[0].setLineType(types[style].intValue());
				arg[0].updateRepaint();

				
				return;
			} 
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
