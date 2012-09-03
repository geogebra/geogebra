package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CmdScripting;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *SelectObjects
 */
public class CmdSelectObjects extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectObjects(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		

		app.clearSelectedGeos(false);

		if (n > 0) {
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if ((arg[i].isGeoElement())) {
					GeoElement geo = arg[i];
					app.addSelectedGeo(geo,false,false);
				}
			}
			
			kernelA.notifyRepaint();

		}else{
			kernelA.notifyRepaint();
			app.updateSelection(false);
		}
			
		
		
		
		return;

	}
}
