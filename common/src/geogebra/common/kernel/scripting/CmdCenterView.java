package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;


/**
 *ZoomIn
 */
public class CmdCenterView extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * @param kernel kernel
	 */
	public CmdCenterView(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoPoint()) {
				GeoPoint p = (GeoPoint) arg[0];

				EuclidianViewInterfaceSlim ev =  app.getActiveEuclidianView();
				double px = (ev.toRealWorldCoordX(ev.getWidth()) - ev.toRealWorldCoordX(0)) / 2; 
				double py = (-ev.toRealWorldCoordY(ev.getHeight()) + ev.toRealWorldCoordY(0)) / 2; 

				ev.setRealWorldCoordSystem(p.inhomX - px, p.inhomX + px, p.inhomY - py, p.inhomY + py);
				
				return;

			} 
				throw argErr(app, c.getName(), arg[0]);
						
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
