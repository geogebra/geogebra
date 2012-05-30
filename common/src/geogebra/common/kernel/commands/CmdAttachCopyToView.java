package geogebra.common.kernel.commands;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * AttachCopyToView
 */
public class CmdAttachCopyToView extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAttachCopyToView(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 5:
		case 2:
			arg = resArgs(c);

			if (arg[1].isNumberValue()) {
				GeoPointND corner1, corner3, corner5;
				int viewID = (int) ((NumberValue) arg[1]).getDouble();
				AbstractEuclidianView ev;
				if (viewID == 2)
					ev = app.getEuclidianView2();
				else
					ev = app.getEuclidianView1();
				if (n == 2) {

					corner1 = new GeoPoint2(kernelA.getConstruction());
					corner3 = new GeoPoint2(kernelA.getConstruction());
					corner5 = new GeoPoint2(kernelA.getConstruction());
					if(ev!=null){
						corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
						corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
						corner5.setCoords(ev.getWidth(), ev.getHeight(), 1);
					}
				} 
				else {
					if (arg[2].isGeoPoint())
						corner1 = (GeoPointND) arg[2];
					else
						throw argErr(app, c.getName(), arg[2]);
					if (arg[3].isGeoPoint())
						corner3 = (GeoPointND) arg[3];
					else
						throw argErr(app, c.getName(), arg[3]);
					if (arg[4].isGeoPoint())
						corner5 = (GeoPointND) arg[4];
					else
						throw argErr(app, c.getName(), arg[4]);
				}
				
				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoPolyLine() || arg[0].isGeoList()) {
					ret = new GeoElement[] {kernelA.AttachCopyToView(label, arg[0], (NumberValue)arg[1],
							corner1, corner3, corner5)};
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
