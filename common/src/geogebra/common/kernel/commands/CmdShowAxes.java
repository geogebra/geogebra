package geogebra.common.kernel.commands;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *SetVisibleInView
 */
public class CmdShowAxes extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowAxes(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		EuclidianViewInterfaceCommon ev = null;

		arg = resArgs(c);
		switch (n) {
		case 0:
			ev = app.getActiveEuclidianView();
			ev.setShowAxis(true);
			ev.repaintView();
			break;
		case 1:
			if (!arg[0].isBooleanValue())
				throw argErr(app, c.getName(), arg[0]);
			
			boolean show = ((BooleanValue)arg[0]).getBoolean();
			ev = app.getActiveEuclidianView();
			ev.setShowAxis(show);
			ev.repaintView();
			
			break;
		case 2:
			if (!arg[0].isNumberValue())
				throw argErr(app, c.getName(), arg[0]);
			if (!arg[1].isBooleanValue())
				throw argErr(app, c.getName(), arg[1]);
			
			int view = (int) ((NumberValue)arg[0]).getDouble();
			show = ((BooleanValue)arg[1]).getBoolean();

			
			switch ((int)(((NumberValue)arg[0]).getDouble())) {
			case 2: 
				if (app.hasEuclidianView2()) {
					ev = app.getEuclidianView2();
				}
				break;
			case 3:
				if (app.hasEuclidianView3D()) {
					ev = app.getEuclidianView3D();
				}
				break;
			default: 
				ev = app.getEuclidianView1();
			}
			
			ev.setShowAxis(show);
			ev.repaintView();
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

