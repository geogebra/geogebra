package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * StartAnimation
 */
public class CmdStartAnimation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStartAnimation(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		// dummy

		switch (n) {
		case 0:

			app.getKernel().getAnimationManager().startAnimation();
			return new GeoElement[0];

		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isAnimatable()) {
				arg[0].setAnimating(true);
				app.getKernel().getAnimationManager().startAnimation();
				return arg;
			} else if (arg[0].isGeoBoolean()) {

				GeoBoolean geo = (GeoBoolean) arg[0];

				if (geo.getBoolean()) {
					app.getKernel().getAnimationManager().startAnimation();

				} else {
					app.getKernel().getAnimationManager().stopAnimation();
				}
				return arg;
			} else {
				throw argErr(c, arg[0]);
			}
		default:
			arg = resArgs(c);
			boolean start = true;
			int sliderCount = n;
			if (arg[n - 1].isGeoBoolean()) {
				start = ((GeoBoolean) arg[n - 1]).getBoolean();
				sliderCount = n - 1;
			}
			for (int i = 0; i < sliderCount; i++) {
				if (!arg[i].isAnimatable()) {
					throw argErr(c, arg[i]);
				}
			}

			for (int i = 0; i < sliderCount; i++) {
				if (arg[i].isAnimatable()) {
					arg[i].setAnimating(start);
				}
				if (start) {
					app.getKernel().getAnimationManager().startAnimation();
				}
			}

			return arg;
		}
	}
}
