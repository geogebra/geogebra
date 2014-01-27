package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.EuclidianSettings;


/**
 *ZoomIn
 */
public class CmdZoomIn extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * @param kernel kernel
	 */
	public CmdZoomIn(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianViewInterfaceSlim ev =  app.getActiveEuclidianView();
				double px = ev.getWidth() / 2; // mouseLoc.x;
				double py = ev.getHeight() / 2; // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				
				return;

			} 
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint p = (GeoPoint) arg[1];

				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				
				return;

			} 
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 4:
			arg = resArgs(c);
			for(int i=0;i<3;i++){
				if(!(arg[i] instanceof NumberValue)){
					throw argErr(app, c.getName(),arg[i]);
				}
			}
			EuclidianSettings evs = app.getActiveEuclidianView().getSettings();
			evs.setXminObject((GeoNumeric)arg[0],false);
			evs.setXmaxObject((GeoNumeric)arg[2],false);
			evs.setYminObject((GeoNumeric)arg[1],false);
			evs.setYmaxObject((GeoNumeric)arg[3],true);
			
			return;
						
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
