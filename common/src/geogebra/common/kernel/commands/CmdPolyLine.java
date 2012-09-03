package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoPolyLine;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;


/**
 * Polyline[ <GeoPoint>, ..., <GeoPoint> ]
 */
public class CmdPolyLine extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPolyLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);
		switch (n) {
		case 0 :
			throw argNumErr(app, c.getName(), n);
		case 1:
			if (arg[0].isGeoList())
				return PolyLine(c.getLabels(), (GeoList) arg[0]);
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoBoolean()) {
				throw argErr(app, c.getName(), arg[1]);
			}

			return PolyLine(c.getLabels(), (GeoList) arg[0]);


		default:
			
			int size = n;
			boolean penStroke = false;

			if (arg[arg.length - 1].isGeoBoolean()) {
				// pen stroke
				// last argument is boolean (normally true)
				size = size - 1;
				penStroke = ((GeoBoolean)arg[arg.length - 1]).getBoolean();
			} 

				// polygon for given points
				GeoPoint[] points = new GeoPoint[size];
				// check arguments
				for (int i = 0; i < size; i++) {
					if (!(arg[i].isGeoPoint()))
						throw argErr(app, c.getName(), arg[i]);
					points[i] = (GeoPoint) arg[i];
				}
				// everything ok
				return kernelA.PolyLine(c.getLabels(), points, penStroke);
			
		}
	}
	

	final private GeoElement[] PolyLine(String[] labels, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, labels, pointList);
		return algo.getOutput();
	}
}
