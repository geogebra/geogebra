package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * Union
 */
public class CmdUnion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnion(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList()) {

				AlgoUnion algo = new AlgoUnion(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoPolygon() && arg[1].isGeoPolygon()) {
				if (arg[0] instanceof GeoPolygon3D
						&& arg[1] instanceof GeoPolygon3D) {
					return union3D(c.getLabels(), (GeoPolygon3D) arg[0],
							(GeoPolygon3D) arg[1]);
				}
				return union(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1]);
			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * returns the output polygon after polygon union operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @return resulting polygons
	 */
	protected GeoElement[] union(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2) {
		return getAlgoDispatcher().Union(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon union operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @return resulting polygons
	 */
	protected GeoElement[] union3D(String[] labels, GeoPolygon3D poly1,
			GeoPolygon3D poly2) {
		return kernelA.getManager3D().UnionPolygons(labels, poly1, poly2);
	}
}