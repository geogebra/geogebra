package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 *StartAnimation
 */
class CmdStartAnimation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStartAnimation(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy
		

		switch (n) {
		case 0:

			app.getKernel().getAnimatonManager().startAnimation();
			return;

		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoNumeric() && ((GeoNumeric) arg[0]).isIndependent()) ||
					arg[0].isPointOnPath()) {				
				arg[0].setAnimating(true);
				app.getKernel().getAnimatonManager().startAnimation();
				return;
			}			
			else if (arg[0].isGeoBoolean()) {

				GeoBoolean geo = (GeoBoolean) arg[0];

				if (geo.getBoolean()) {
					app.getKernel().getAnimatonManager().startAnimation();

				} else {
					app.getKernel().getAnimatonManager().stopAnimation();
				}
				return;
			} else
				throw argErr(app, c.getName(), arg[0]);
		default:
			arg = resArgs(c);
			boolean start = true;
			int sliderCount = n;
			if (arg[n-1].isGeoBoolean()){
				start = ((GeoBoolean) arg[n-1]).getBoolean();
				sliderCount = n-1;
			}
			for(int i = 0; i < sliderCount; i++)
				if(!arg[i].isGeoNumeric() && !arg[i].isPointOnPath())
					throw argErr(app,c.getName(),arg[i]);
			
			for(int i = 0; i < sliderCount; i++){
				if(arg[i].isGeoNumeric())
					((GeoNumeric) arg[0]).setAnimating(start);
				else
					((GeoPoint2) arg[0]).setAnimating(start);
				if(start)
					app.getKernel().getAnimatonManager().startAnimation();
			} 
			
			return;		
		}
	}
}
