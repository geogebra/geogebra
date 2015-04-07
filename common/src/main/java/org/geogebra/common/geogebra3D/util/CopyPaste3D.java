package org.geogebra.common.geogebra3D.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCircle3DThreePoints;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicFivePoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoEllipseHyperbolaFociPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygonRegular3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCylinder;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCylinder;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.CopyPaste;

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
			if (geo.getParentAlgorithm() == null)
				continue;

			if (geo.isGeoElement3D()) {
				// TODO: implementation!

				if (geo.isGeoPolyhedron()) {
					// there are many kinds of algorithm to create a
					// GeoPolyhedron,
					// but the essence is that its faces, edges and points
					// should
					// be shown in any case when they have common parent algo
					// inputs
					Iterator<GeoPolygon3D> polysit = ((GeoPolyhedron) geo)
							.getPolygons().iterator();
					GeoPolygon3D psnext;
					GeoPointND[] pspoints;
					while (polysit.hasNext()) {
						psnext = polysit.next();
						if (!geos.contains(psnext)
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												psnext.getAllIndependentPredecessors())) {
							geos.add(psnext);
						}
					}
					Iterator<GeoPolygon> ps2 = ((GeoPolyhedron) geo)
							.getPolygonsLinked().iterator();
					GeoPolygon ps2n;
					while (ps2.hasNext()) {
						ps2n = ps2.next();
						if (!geos.contains(ps2n)
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												ps2n.getAllIndependentPredecessors())) {
							geos.add(ps2n);
						}
					}
					GeoSegment3D[] segm = ((GeoPolyhedron) geo).getSegments3D();
					for (int j = 0; j < segm.length; j++) {
						if (!geos.contains(segm[j])
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												segm[j].getAllIndependentPredecessors())) {
							geos.add(segm[j]);
							GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
									segm[j].getEndPoint() };
							for (int k = 0; k < pspoints2.length; k++) {
								if (!geos.contains(pspoints2[k])
										&& geo.getAllIndependentPredecessors()
												.containsAll(
														((GeoElement) (pspoints2[k]))
																.getAllIndependentPredecessors())) {
									geos.add((GeoElement) (pspoints2[k]));
								}
							}
						}
					}
				} else if (geo instanceof GeoPolyhedronNet) {
					Iterator<GeoPolygon3D> polysit = ((GeoPolyhedronNet) geo)
							.getPolygons().iterator();
					GeoPolygon3D psnext;
					GeoPointND[] pspoints;
					while (polysit.hasNext()) {
						psnext = polysit.next();
						if (!geos.contains(psnext)
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												psnext.getAllIndependentPredecessors())) {
							geos.add(psnext);
						}
					}
					Iterator<GeoPolygon> ps2 = ((GeoPolyhedronNet) geo)
							.getPolygonsLinked().iterator();
					GeoPolygon ps2n;
					while (ps2.hasNext()) {
						ps2n = ps2.next();
						if (!geos.contains(ps2n)
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												ps2n.getAllIndependentPredecessors())) {
							geos.add(ps2n);
						}
					}
					GeoSegment3D[] segm = ((GeoPolyhedronNet) geo)
							.getSegments3D();
					for (int j = 0; j < segm.length; j++) {
						if (!geos.contains(segm[j])
								&& geo.getAllIndependentPredecessors()
										.containsAll(
												segm[j].getAllIndependentPredecessors())) {
							geos.add(segm[j]);
							GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
									segm[j].getEndPoint() };
							for (int k = 0; k < pspoints2.length; k++) {
								if (!geos.contains(pspoints2[k])
										&& geo.getAllIndependentPredecessors()
												.containsAll(
														((GeoElement) (pspoints2[k]))
																.getAllIndependentPredecessors())) {
									geos.add((GeoElement) (pspoints2[k]));
								}
							}
						}
					}
				} else if (geo instanceof GeoQuadric3DLimited) {
					if (geo.getParentAlgorithm() instanceof AlgoQuadricLimitedPointPointRadiusCone
							|| geo.getParentAlgorithm() instanceof AlgoQuadricLimitedPointPointRadiusCylinder
							|| geo.getParentAlgorithm() instanceof AlgoQuadricLimitedConicHeightCone
							|| geo.getParentAlgorithm() instanceof AlgoQuadricLimitedConicHeightCylinder) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])
									&& geo.getAllIndependentPredecessors()
											.containsAll(
													pgeos[j].getAllIndependentPredecessors())) {
								geos.add(pgeos[j]);
							}
						}
						pgeos = geo.getParentAlgorithm().getOutput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])
									&& geo.getAllIndependentPredecessors()
											.containsAll(
													pgeos[j].getAllIndependentPredecessors())) {
								geos.add(pgeos[j]);
							}
						}
					}

				} else if ((geo.isGeoLine() && geo.getParentAlgorithm() instanceof AlgoJoinPoints3D)
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
							if (!geos.contains(ogeos[j])
									&& ogeos[j].isGeoSegment()) {
								geos.add(ogeos[j]);
							}
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoPolygonRegular3D) {
						GeoElement[] pgeos = ((geo.getParentAlgorithm()))
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])
									&& pgeos[j].isGeoPoint() && j < 3) {
								geos.add(pgeos[j]);
							}
						}
						GeoElement[] ogeos = ((geo.getParentAlgorithm()))
								.getOutput();
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
					/*
					 * if (geo.getParentAlgorithm() instanceof
					 * AlgoCircleTwoPoints) { GeoElement[] pgeos =
					 * geo.getParentAlgorithm().getInput(); if
					 * (!geos.contains(pgeos[0])) geos.add(pgeos[0]); if
					 * (!geos.contains(pgeos[1])) geos.add(pgeos[1]); } else
					 */if (geo.getParentAlgorithm() instanceof AlgoCircle3DThreePoints
							|| geo.getParentAlgorithm() instanceof AlgoEllipseHyperbolaFociPoint3D) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
						if (!geos.contains(pgeos[1]))
							geos.add(pgeos[1]);
						if (!geos.contains(pgeos[2]))
							geos.add(pgeos[2]);
					} else if (geo.getParentAlgorithm() instanceof AlgoConicFivePoints3D) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]))
								geos.add(pgeos[j]);
						}
					} /*
					 * else if (geo.getParentAlgorithm() instanceof
					 * AlgoCirclePointRadius) { GeoElement[] pgeos =
					 * geo.getParentAlgorithm().getInput(); if
					 * (!geos.contains(pgeos[0])) geos.add(pgeos[0]); }
					 */
				}
			}
		}
	}
}
