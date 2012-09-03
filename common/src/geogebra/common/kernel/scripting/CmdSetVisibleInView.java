package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.CmdScripting;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *SetVisibleInView
 */
public class CmdSetVisibleInView extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetVisibleInView(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 3:
			arg = resArgs(c);
			if (!arg[1].isNumberValue())
				throw argErr(app, c.getName(), arg[1]);


			if (arg[2].isGeoBoolean()) {

				GeoElement geo = arg[0];
				

				int viewNo = (int)((NumberValue)arg[1]).getDouble();

				EuclidianViewInterfaceSlim ev = null;

				switch (viewNo) {
				case 1:
					ev = app.getEuclidianView1();
					break;
				case 2:
					if (!app.hasEuclidianView2()) break;
					ev = app.getEuclidianView2();
					break;
				default:
					// do nothing
				}

				if (ev != null) {
					boolean show = ((GeoBoolean)arg[2]).getBoolean();

					if (show) {
						geo.setEuclidianVisible(true);
						geo.addView(ev.getViewID());
						ev.add(geo);
					} else {
						geo.removeView(ev.getViewID());
						ev.remove(geo);
					}
					
					geo.updateRepaint();
				}

				return;
			}
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

