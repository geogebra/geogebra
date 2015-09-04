package org.geogebra.common.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * Difference[<polygon>,<polygon>]
 * 
 * @author thilina
 *
 */
public class CmdDifference extends CommandProcessor {

	/**
	 * 
	 * @param kernel
	 */
	public CmdDifference(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int argumentNo = c.getArgumentNumber();
		boolean[] ok = { false, false, false };
		GeoElement[] arg = resArgs(c);

		switch (argumentNo) {
		case 2:
			if ((ok[0] = arg[0] instanceof GeoPolygon)
					&& (ok[1] = arg[1] instanceof GeoPolygon)) {
				if (arg[0] instanceof GeoPolygon3D
						&& arg[1] instanceof GeoPolygon3D) {
					return difference3D(c.getLabels(), (GeoPolygon3D) arg[0],
							(GeoPolygon3D) arg[1]);
				}
				return difference(c.getLabels(), (GeoPolygon) arg[0],
								(GeoPolygon) arg[1]);
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 3:
			if ((ok[0] = arg[0] instanceof GeoPolygon)
					&& (ok[1] = arg[1] instanceof GeoPolygon)
					&& (ok[2] = arg[2] instanceof GeoBoolean)) {
				if (arg[0] instanceof GeoPolygon3D
						&& arg[1] instanceof GeoPolygon3D) {
					return difference3D(c.getLabels(), (GeoPolygon3D) arg[0],
							(GeoPolygon3D) arg[1], (GeoBoolean) arg[2]);
				}
				return difference(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1], (GeoBoolean) arg[2]);
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), argumentNo);
		}

	}

	/**
	 * returns the output polygon after polygon difference operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @return resulting polygons
	 */
	protected GeoElement[] difference(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2) {
		return getAlgoDispatcher().Difference(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon difference operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @return resulting polygons
	 */
	protected GeoElement[] difference3D(String[] labels, GeoPolygon3D poly1,
			GeoPolygon3D poly2) {
		return kernelA.getManager3D().DifferencePolygons(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon exclusive OR/difference (XOR)
	 * operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @param exclusive
	 *            input GeoBoolean indicating XOR or not
	 * @return resulting polygons
	 */
	protected GeoElement[] difference(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2, GeoBoolean exclusive) {
		return getAlgoDispatcher().Difference(labels, poly1, poly2, exclusive);
	}

	/**
	 * returns the output polygon after polygon exclusive OR/difference (XOR)
	 * operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @param exclusive
	 *            input GeoBoolean indicating exclusive difference or not
	 * @return resulting polygons
	 */
	protected GeoElement[] difference3D(String[] labels, GeoPolygon3D poly1,
			GeoPolygon3D poly2, GeoBoolean exclusive) {
		return kernelA.getManager3D().DifferencePolygons(labels, poly1, poly2,
				exclusive);
	}

}