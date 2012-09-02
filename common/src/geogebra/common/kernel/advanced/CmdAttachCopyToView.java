package geogebra.common.kernel.advanced;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
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
		case 6:
		case 2:
			arg = resArgs(c);

			if (arg[1].isNumberValue()) {
				GeoPointND corner1, corner3, screenCorner1,screenCorner3;
				int viewID = (int) ((NumberValue) arg[1]).getDouble();
				EuclidianView ev;
				if (viewID == 2)
					ev = app.getEuclidianView2();
				else
					ev = app.getEuclidianView1();
				if (n == 2) {

					corner1 = new GeoPoint(kernelA.getConstruction());
					corner3 = new GeoPoint(kernelA.getConstruction());
					screenCorner1 = new GeoPoint(kernelA.getConstruction());
					screenCorner3 = new GeoPoint(kernelA.getConstruction());
					if(ev!=null){
						corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
						corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
						screenCorner1.setCoords(0, ev.getHeight(), 1);
						screenCorner3.setCoords(ev.getWidth(), 0, 1);
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
						screenCorner1 = (GeoPointND) arg[4];
					else
						throw argErr(app, c.getName(), arg[4]);
					if (arg[5].isGeoPoint())
						screenCorner3 = (GeoPointND) arg[5];
					else
						throw argErr(app, c.getName(), arg[5]);
				}
				
				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoPolyLine() || arg[0].isGeoList()) {
					
					AlgoAttachCopyToView algo = new AlgoAttachCopyToView(cons,arg[0], (NumberValue)arg[1],
							corner1, corner3, screenCorner1,screenCorner3);

					ret = new GeoElement[] { algo.getResult() };
					if(n==2 && ev!=app.getActiveEuclidianView()){
						ret[0].addView(ev.getViewID());
						ret[0].removeView(app.getActiveEuclidianView().getViewID());
						app.getActiveEuclidianView().remove(ret[0]);
						ev.add(ret[0]);
					}
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
