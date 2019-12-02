package org.geogebra.common.util;

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
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public abstract class CopyPaste {

	// labelPrefix has to contain something else than big letters,
	// otherwise the parsed label could be regarded as a spreadsheet label
	// see GeoElement.isSpreadsheetLabel
	// check if name is valid for geo
	public static final String labelPrefix = "CLIPBOARDmagicSTRING";

	public abstract void copyToXML(App app, List<GeoElement> selection);

	public abstract void pasteFromXML(App app);

	public abstract void duplicate(App app, List<GeoElement> selection);

	/**
	 * copyToXML - Step 2 Add subgeos of geos like points of a segment or line
	 * or polygon These are copied anyway but this way they won't be hidden
	 *
	 * @param geos input and output
	 */
	protected static void addSubGeos(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if (geo.getParentAlgorithm() == null) {
				continue;
			}

			if ((geo.isGeoLine()
					&& geo.getParentAlgorithm() instanceof AlgoJoinPoints)
					|| (geo.isGeoSegment()
					&& geo.getParentAlgorithm() instanceof AlgoJoinPointsSegment)
					|| (geo.isGeoRay()
					&& geo.getParentAlgorithm() instanceof AlgoJoinPointsRay)
					|| (geo.isGeoVector() && geo
					.getParentAlgorithm() instanceof AlgoVector)) {

				if (!geos
						.contains(geo.getParentAlgorithm().getInput()[0])) {
					geos.add(geo.getParentAlgorithm().getInput()[0]);
				}
				if (!geos
						.contains(geo.getParentAlgorithm().getInput()[1])) {
					geos.add(geo.getParentAlgorithm().getInput()[1]);
				}
			} else if (geo.isGeoPolygon()) {
				if (geo.getParentAlgorithm() instanceof AlgoPolygon) {
					GeoPointND[] points = ((AlgoPolygon) (geo
							.getParentAlgorithm())).getPoints();
					for (int j = 0; j < points.length; j++) {
						if (!geos.contains(points[j])) {
							geos.add((GeoElement) points[j]);
						}
					}
					GeoElement[] ogeos = ((AlgoPolygon) (geo
							.getParentAlgorithm())).getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j])
								&& ogeos[j].isGeoSegment()) {
							geos.add(ogeos[j]);
						}
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoPolygonRegularND) {
					GeoElement[] pgeos = ((geo.getParentAlgorithm()))
							.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])
								&& pgeos[j].isGeoPoint()) {
							geos.add(pgeos[j]);
						}
					}
					GeoElement[] ogeos = ((geo.getParentAlgorithm()))
							.getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j])
								&& (ogeos[j].isGeoSegment()
								|| ogeos[j].isGeoPoint())) {
							geos.add(ogeos[j]);
						}
					}
				}
			} else if (geo instanceof GeoPolyLine) {
				if (geo.getParentAlgorithm() instanceof AlgoPolyLine) {
					GeoPointND[] pgeos = ((AlgoPolyLine) (geo
							.getParentAlgorithm())).getPoints();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add((GeoElement) pgeos[j]);
						}
					}
				}
			} else if (geo.isGeoConic()) {
				if (geo.getParentAlgorithm() instanceof AlgoCircleTwoPoints) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					if (!geos.contains(pgeos[0])) {
						geos.add(pgeos[0]);
					}
					if (!geos.contains(pgeos[1])) {
						geos.add(pgeos[1]);
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoCircleThreePoints
						|| geo.getParentAlgorithm() instanceof AlgoEllipseHyperbolaFociPoint) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					if (!geos.contains(pgeos[0])) {
						geos.add(pgeos[0]);
					}
					if (!geos.contains(pgeos[1])) {
						geos.add(pgeos[1]);
					}
					if (!geos.contains(pgeos[2])) {
						geos.add(pgeos[2]);
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoConicFivePoints) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add(pgeos[j]);
						}
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					if (!geos.contains(pgeos[0])) {
						geos.add(pgeos[0]);
					}
				}
			} else if (geo.isGeoList()) {
				// TODO: note that there are a whole lot of other list algos
				// that might need to be supported! 3D cases come here too,
				// because GeoList is 2D object! Or we should make a
				// separate
				// method just for GeoList! It would also be good, for
				// nested
				// lists in lists and GeoElements with subGeos in lists!
				// (new ticket)
				if (Algos.isUsedFor(Commands.Sequence, geo)) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					if (pgeos.length > 1) {
						if (!geos.contains(pgeos[0])) {
							geos.add(pgeos[0]);
						}
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoDependentList) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add(pgeos[j]);
						}
					}
				}
			}

			if (geo.isGeoPolyhedron()) {
				TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
						geo);

				// there are many kinds of algorithm to create a
				// GeoPolyhedron,
				// but the essence is that its faces, edges and points
				// should
				// be shown in any case when they have common parent algo
				// inputs
				Iterator<GeoPolygon3D> polysit = ((GeoPolyhedron) geo)
						.getPolygons().iterator();
				GeoPolygon3D psnext;
				while (polysit.hasNext()) {
					psnext = polysit.next();
					if (!geos.contains(psnext)
							&& predecessorsCovered(psnext, ancestors)) {
						geos.add(psnext);
					}
				}
				Iterator<GeoPolygon> ps2 = ((GeoPolyhedron) geo)
						.getPolygonsLinked().iterator();
				GeoPolygon ps2n;
				while (ps2.hasNext()) {
					ps2n = ps2.next();
					if (!geos.contains(ps2n)
							&& predecessorsCovered(ps2n, ancestors)) {
						geos.add(ps2n);
					}
				}
				GeoSegment3D[] segm = ((GeoPolyhedron) geo).getSegments3D();
				for (int j = 0; j < segm.length; j++) {
					if (!geos.contains(segm[j])
							&& predecessorsCovered(segm[j], ancestors)) {
						geos.add(segm[j]);
						GeoPointND[] pspoints2 = {segm[j].getStartPoint(),
								segm[j].getEndPoint()};
						for (int k = 0; k < pspoints2.length; k++) {
							if (!geos.contains(pspoints2[k])
									&& predecessorsCovered(pspoints2[k],
									ancestors)) {
								geos.add((GeoElement) (pspoints2[k]));
							}
						}
					}
				}
			} else if (geo instanceof GeoPolyhedronNet) {
				TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
						geo);

				Iterator<GeoPolygon3D> polysit = ((GeoPolyhedronNet) geo)
						.getPolygons().iterator();
				GeoPolygon3D psnext;
				while (polysit.hasNext()) {
					psnext = polysit.next();
					if (!geos.contains(psnext)
							&& predecessorsCovered(psnext, ancestors)) {
						geos.add(psnext);
					}
				}
				Iterator<GeoPolygon> ps2 = ((GeoPolyhedronNet) geo)
						.getPolygonsLinked().iterator();
				GeoPolygon ps2n;
				while (ps2.hasNext()) {
					ps2n = ps2.next();
					if (!geos.contains(ps2n)
							&& predecessorsCovered(ps2n, ancestors)) {
						geos.add(ps2n);
					}
				}
				GeoSegment3D[] segm = ((GeoPolyhedronNet) geo)
						.getSegments3D();
				for (int j = 0; j < segm.length; j++) {
					if (!geos.contains(segm[j])
							&& predecessorsCovered(segm[j], ancestors)) {
						geos.add(segm[j]);
						GeoPointND[] pspoints2 = {segm[j].getStartPoint(),
								segm[j].getEndPoint()};
						for (int k = 0; k < pspoints2.length; k++) {
							if (!geos.contains(pspoints2[k])
									&& predecessorsCovered(pspoints2[k],
									ancestors)) {
								geos.add((GeoElement) (pspoints2[k]));
							}
						}
					}
				}
			} else if (geo instanceof GeoQuadric3DLimited) {
				AlgoElement algo = geo.getParentAlgorithm();
				if (algo instanceof AlgoQuadricLimitedPointPointRadiusCone
						|| algo instanceof AlgoQuadricLimitedPointPointRadiusCylinder
						|| algo instanceof AlgoQuadricLimitedConicHeightCone
						|| algo instanceof AlgoQuadricLimitedConicHeightCylinder) {
					TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
							geo);

					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j]) && predecessorsCovered(
								pgeos[j], ancestors)) {
							geos.add(pgeos[j]);
						}
					}
					pgeos = geo.getParentAlgorithm().getOutput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j]) && predecessorsCovered(
								pgeos[j], ancestors)) {
							geos.add(pgeos[j]);
						}
					}
				}

			} else if ((geo.isGeoLine()
					&& geo.getParentAlgorithm() instanceof AlgoJoinPoints3D)
					|| (geo.isGeoVector() && geo
					.getParentAlgorithm() instanceof AlgoVector3D)) {

				if (!geos
						.contains(geo.getParentAlgorithm().getInput()[0])) {
					geos.add(geo.getParentAlgorithm().getInput()[0]);
				}
				if (!geos
						.contains(geo.getParentAlgorithm().getInput()[1])) {
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
				} else if (geo
						.getParentAlgorithm() instanceof AlgoPolygonRegular3D) {
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
								&& (ogeos[j].isGeoSegment()
								|| ogeos[j].isGeoPoint())) {
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
				 */
				AlgoElement algo = geo.getParentAlgorithm();
				if (algo instanceof AlgoCircle3DThreePoints
						|| algo instanceof AlgoEllipseHyperbolaFociPoint3D) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					if (!geos.contains(pgeos[0])) {
						geos.add(pgeos[0]);
					}
					if (!geos.contains(pgeos[1])) {
						geos.add(pgeos[1]);
					}
					if (!geos.contains(pgeos[2])) {
						geos.add(pgeos[2]);
					}
				} else if (geo
						.getParentAlgorithm() instanceof AlgoConicFivePoints3D) {
					GeoElement[] pgeos = geo.getParentAlgorithm()
							.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add(pgeos[j]);
						}
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

	private static TreeSet<GeoElement> getAllIndependentPredecessors(
			GeoElement geo) {
		TreeSet<GeoElement> ancestors = new TreeSet<>();
		geo.addPredecessorsToSet(ancestors, true);
		return ancestors;
	}

	private static boolean predecessorsCovered(GeoElementND ps2n,
			TreeSet<GeoElement> ancestors) {
		return ancestors.containsAll(
				getAllIndependentPredecessors(ps2n.toGeoElement()));
	}

	/**
	 * copyToXML - Step 3 Add geos which might be intermediates between our
	 * existent geos And also add all predecessors of our geos except GeoAxis
	 * objects (GeoAxis objects should be dealt with later - we suppose they are
	 * always on)
	 *
	 * @param geos input and output
	 * @return just the predecessor and intermediate geos for future handling
	 */
	protected static ArrayList<ConstructionElement> addPredecessorGeos(
			ArrayList<ConstructionElement> geos) {

		ArrayList<ConstructionElement> ret = new ArrayList<>();

		GeoElement geo, geo2;
		TreeSet<GeoElement> ts;
		Iterator<GeoElement> it;
		for (int i = 0; i < geos.size(); i++) {
			geo = (GeoElement) geos.get(i);

			ts = geo.getAllPredecessors();
			it = ts.iterator();
			while (it.hasNext()) {
				geo2 = it.next();
				if (!ret.contains(geo2) && !geos.contains(geo2)
						&& geo2.getConstruction()
						.isConstantElement(geo2) == Construction.Constants.NOT) {
					ret.add(geo2);
				}
			}
		}
		geos.addAll(ret);
		return ret;
	}

	/**
	 * Convenience method to set new labels instead of labels
	 *
	 * @param app
	 *            application
	 * @param labels
	 *            new labels
	 */
	protected static ArrayList<GeoElement> handleLabels(App app, ArrayList<String> labels,
			boolean putdown) {
		ArrayList<GeoElement> ret = new ArrayList<>();

		Kernel kernel = app.getKernel();
		GeoElement geo;
		String oldLabel;
		for (int i = 0; i < labels.size(); i++) {
			String ll = labels.get(i);
			geo = kernel.lookupLabel(ll);
			if (geo != null) {
				if (app.getActiveEuclidianView() == app.getEuclidianView1()) {
					app.addToEuclidianView(geo);
					if (app.hasEuclidianView2(1)) {
						geo.removeView(App.VIEW_EUCLIDIAN2);
						app.getEuclidianView2(1).remove(geo);
					}
					if (app.isEuclidianView3Dinited()) {
						app.removeFromViews3D(geo);
					}
				} else if (app.getActiveEuclidianView()
						.getViewID() == App.VIEW_EUCLIDIAN3D) {
					app.removeFromEuclidianView(geo);
					if (app.isEuclidianView3Dinited()) {
						app.addToViews3D(geo);
					}
					if (app.hasEuclidianView2(1)) {
						geo.removeView(App.VIEW_EUCLIDIAN2);
						app.getEuclidianView2(1).remove(geo);
					}
				} else {
					app.removeFromEuclidianView(geo);
					geo.addView(App.VIEW_EUCLIDIAN2);
					app.getEuclidianView2(1).add(geo);
					if (app.isEuclidianView3Dinited()) {
						app.removeFromViews3D(geo);
					}
				}

				oldLabel = geo.getLabelSimple();
				geo.setLabel(geo.getIndexLabel(
						geo.getLabelSimple().substring(labelPrefix.length())));
				// geo.getLabelSimple() is now not the oldLabel, ideally
				if (putdown) {
					geo.getKernel().renameLabelInScripts(oldLabel,
							geo.getLabelSimple());
				}

				ret.add(geo);

				if (Algos.isUsedFor(Commands.Sequence, geo)) {
					// variable of AlgoSequence is not returned in
					// lookupLabel!
					// the old name of the variable may remain, as it is not
					// part of the construction anyway
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					if (pgeos.length > 1 && pgeos[1].getLabelSimple()
							.length() > labelPrefix.length()) {
						if (pgeos[1].getLabelSimple()
								.substring(0, labelPrefix.length())
								.equals(labelPrefix)) {
							pgeos[1].setLabelSimple(pgeos[1].getLabelSimple()
									.substring(labelPrefix.length()));
						}
					}
				}
			}
		}

		return ret;
	}
}
