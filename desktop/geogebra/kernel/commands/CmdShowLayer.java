package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

import java.util.Iterator;

/**
 *ShowLayer
 */
class CmdShowLayer extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowLayer(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(true);
						geo.updateRepaint();
					}
				}

				
				return;

			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
