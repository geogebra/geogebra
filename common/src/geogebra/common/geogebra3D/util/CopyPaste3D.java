package geogebra.common.geogebra3D.util;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyLine3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygonRegular3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoVector3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.CopyPaste;

import java.util.ArrayList;

public class CopyPaste3D extends CopyPaste {

	public CopyPaste3D() {
		// dummy, for now
	}

	@Override
	protected void addSubGeos(ArrayList<ConstructionElement> geos) {
		// even in 3D, there may be a lot of 2D objects
		super.addSubGeos(geos);

		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if(geo.getParentAlgorithm()==null)
				continue;

			if (geo.isGeoElement3D()) {
				// TODO: implementation!

				if ((geo.isGeoLine() && geo.getParentAlgorithm() instanceof AlgoJoinPoints3D)
						|| (geo.isGeoVector() && geo.getParentAlgorithm() instanceof AlgoVector3D)) {

					if (!geos.contains(geo.getParentAlgorithm().getInput()[0])) {
						geos.add(geo.getParentAlgorithm().getInput()[0]);
					}
					if (!geos.contains(geo.getParentAlgorithm().getInput()[1])) {
						geos.add(geo.getParentAlgorithm().getInput()[1]);
					}
				} else if (geo instanceof GeoPolygon3D) {

					if (geo.getParentAlgorithm() instanceof AlgoPolygon3D) {
						GeoPointND[] points = ((AlgoPolygon3D) (geo
								.getParentAlgorithm())).getPoints();
						for (int j = 0; j < points.length; j++) {
							if (!geos.contains(points[j])) {
								geos.add((GeoElement) points[j]);
							}
						}
						GeoElement[] ogeos = ((AlgoPolygon3D) (geo
								.getParentAlgorithm())).getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j]) && ogeos[j].isGeoSegment()) {
								geos.add(ogeos[j]);
							}
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoPolygonRegular3D) {
						GeoElement[] pgeos = ((geo
								.getParentAlgorithm())).getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]) && pgeos[j].isGeoPoint() && j < 3) {
								geos.add(pgeos[j]);
							}
						}
						GeoElement[] ogeos = ((geo
								.getParentAlgorithm())).getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j])
									&& (ogeos[j].isGeoSegment() || ogeos[j]
											.isGeoPoint())) {
								geos.add(ogeos[j]);
							}
						}
					}
				} else if (geo instanceof GeoPolyLine3D) {
					if (geo.getParentAlgorithm() instanceof AlgoPolyLine3D) {
						GeoPointND[] pgeos = ((AlgoPolyLine3D) (geo
								.getParentAlgorithm())).getPointsND();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])) {
								geos.add((GeoElement) pgeos[j]);
							}
						}
					}
				} else if (geo.isGeoConic()) {
					// different, harder!
					/*if (geo.getParentAlgorithm() instanceof AlgoCircleTwoPoints) {
						GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
						if (!geos.contains(pgeos[1]))
							geos.add(pgeos[1]);
					} else if (geo.getParentAlgorithm() instanceof AlgoCircleThreePoints
							|| geo.getParentAlgorithm()instanceof AlgoEllipseHyperbolaFociPoint) {
						GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
						if (!geos.contains(pgeos[1]))
							geos.add(pgeos[1]);
						if (!geos.contains(pgeos[2]))
							geos.add(pgeos[2]);
					} else if (geo.getParentAlgorithm() instanceof AlgoConicFivePoints) {
						GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]))
								geos.add(pgeos[j]);
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
						GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
					}*/
				}
			}
		}
	}
}
