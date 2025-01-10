package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * LineStyle
 */
public class CmdSetLineStyle extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLineStyle(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int style = (int) arg[1].evaluateDouble();
				// Integer[] types = EuclidianView.getLineTypes();

				// For invalid number we assume it's 0
				// We do this also for SetPointStyle

				if (style < 0 || style >= EuclidianView.getLineTypeLength()) {
					style = 0;
				}

				arg[0].setLineType(EuclidianView.getLineType(style));
				arg[0].updateVisualStyleRepaint(GProperty.LINE_STYLE);

				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
