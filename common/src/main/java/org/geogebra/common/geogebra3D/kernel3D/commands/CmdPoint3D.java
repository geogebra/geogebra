package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPointVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.algos.AlgoPointsFromList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/*
 * Point[ <Path (3D)> ] or Point[ <Region (3D)> ] or CmdPoint
 */
public class CmdPoint3D extends CmdPoint {

	public CmdPoint3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			GeoElement geo0 = arg[0];

			if (geo0.isGeoElement3D()
					|| (geo0.isGeoList() && ((GeoList) geo0)
							.containsGeoElement3D())) {
				if (geo0.isPath()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.Point3D(c.getLabel(), (Path) geo0, false) };
					return ret;
				}
				// if arg[0] isn't a Path, try to process it as a region (e.g.
				// GeoPlane3D)
				if (geo0.isRegion()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.Point3DIn(c.getLabel(), (Region) arg[0], false) };
					return ret;
				}

				throw argErr(app, c.getName(), geo0);
			} else if (arg[0].isGeoList()
					&& ((GeoList) arg[0]).getGeoElementForPropertiesDialog()
							.isGeoNumeric()) {
				if ((((GeoList) arg[0]).get(0).isGeoNumeric() && ((GeoList) arg[0])
						.size() == 3)
						|| (((GeoList) arg[0]).get(0).isGeoList() && ((GeoList) ((GeoList) arg[0])
								.get(0)).size() == 3)) {

					AlgoPointsFromList algo = new AlgoPointsFromList(cons,
							c.getLabels(), !cons.isSuppressLabelsActive(),
							(GeoList) arg[0]);

					GeoElement[] ret = algo.getPoints3D();

					return ret;
				}
			}
		}

		return super.process(c);

	}

	@Override
	protected GeoElement point(String label, Path path, GeoNumberValue value) {

		if (path.isGeoElement3D()
				|| (((GeoElement) path).isGeoList() && ((GeoList) path)
						.containsGeoElement3D())) {
			return (GeoElement) kernelA.getManager3D().Point3D(label, path,
					value);
		}

		return super.point(label, path, value);
	}

	@Override
	protected GeoPointND point(String label, GeoPointND point,
			GeoVectorND vector) {

		if (point.isGeoElement3D() || vector.isGeoElement3D()) {
			AlgoPointVector3D algo = new AlgoPointVector3D(cons, label, point,
					vector);

			return algo.getQ();
		}

		return super.point(label, point, vector);
	}

}
