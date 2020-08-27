package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCylinder;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCylinder;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoordSys1D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPointND;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
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
import org.geogebra.common.main.SelectionManager;

public abstract class CopyPaste {

	// labelPrefix has to contain something else than big letters,
	// otherwise the parsed label could be regarded as a spreadsheet label
	// see GeoElement.isSpreadsheetLabel
	// check if name is valid for geo
	public static final String labelPrefix = "CLIPBOARDmagicSTRING";

	public abstract void copyToXML(App app, List<GeoElement> selection);

	public abstract void pasteFromXML(App app);

	public abstract void duplicate(App app, List<GeoElement> geos);

	public abstract void clearClipboard();

	/**
	 * @param app
	 *            application
	 * @param cut
	 *            whether to cut (false = copy)
	 */
	public static void handleCutCopy(App app, boolean cut) {
		SelectionManager selection = app.getSelectionManager();
		app.setWaitCursor();
		ArrayList<GeoElement> tempSelection
				= new ArrayList<>(selection.getSelectedGeos());

		app.getActiveEuclidianView().getEuclidianController().splitSelectedStrokes(cut);
		app.getCopyPaste().copyToXML(app, selection.getSelectedGeos());
		if (cut) {
			app.deleteSelectedObjects(cut);
		} else {
			app.getActiveEuclidianView().getEuclidianController().removeSplitParts();
		}
		app.updateMenubar();

		selection.setSelectedGeos(tempSelection, false);
		app.setDefaultCursor();
	}

	/**
	 * copyToXML - Step 2 Add subgeos of geos like points of a segment or line
	 * or polygon These are copied anyway but this way they won't be hidden
	 *
	 * @param geos
	 *            input and output
	 */
	protected static void addSubGeos(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			AlgoElement parentAlgorithm = geo.getParentAlgorithm();
			if (parentAlgorithm == null) {
				continue;
			}

			if ((geo.isGeoLine() && parentAlgorithm instanceof AlgoJoinPoints)
					|| (geo.isGeoLine()
							&& parentAlgorithm instanceof AlgoJoinPointsSegmentInterface)
					|| (geo.isGeoRay()
							&& parentAlgorithm instanceof AlgoJoinPointsRay)
					|| (geo.isGeoVector()
							&& parentAlgorithm instanceof AlgoVector)) {

				if (!geos.contains(parentAlgorithm.getInput()[0])) {
					geos.add(parentAlgorithm.getInput()[0]);
				}
				if (!geos.contains(parentAlgorithm.getInput()[1])) {
					geos.add(parentAlgorithm.getInput()[1]);
				}
			} else if (geo.isGeoPolygon()) {
				if (parentAlgorithm instanceof AlgoPolygon) {
					GeoPointND[] points = ((AlgoPolygon) parentAlgorithm)
							.getPoints();
					for (int j = 0; j < points.length; j++) {
						if (!geos.contains(points[j])) {
							geos.add((GeoElement) points[j]);
						}
					}
					GeoElement[] ogeos = ((AlgoPolygon) parentAlgorithm)
							.getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j])
								&& ogeos[j].isGeoSegment()) {
							geos.add(ogeos[j]);
						}
					}
				} else if (parentAlgorithm instanceof AlgoPolygonRegularND) {
					GeoElement[] pgeos = parentAlgorithm.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j]) && pgeos[j].isGeoPoint()) {
							geos.add(pgeos[j]);
						}
					}
					GeoElement[] ogeos = parentAlgorithm.getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j]) && (ogeos[j].isGeoSegment()
								|| ogeos[j].isGeoPoint())) {
							geos.add(ogeos[j]);
						}
					}
				}
			} else if (geo instanceof GeoPolyLine) {
				if (parentAlgorithm instanceof AlgoPolyLine) {
					GeoPointND[] pgeos = ((AlgoPolyLine) parentAlgorithm)
							.getPoints();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add((GeoElement) pgeos[j]);
						}
					}
				}
			} else if (geo.isGeoConic()) {
				if (parentAlgorithm instanceof AlgoCircleTwoPoints
						|| parentAlgorithm instanceof AlgoCircleThreePoints
						|| parentAlgorithm instanceof AlgoEllipseHyperbolaFociPointND
						|| parentAlgorithm instanceof AlgoConicFivePoints) {
					GeoElement[] pgeos = parentAlgorithm.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add(pgeos[j]);
						}
					}
				} else if (parentAlgorithm instanceof AlgoCirclePointRadius) {
					GeoElement[] pgeos = parentAlgorithm.getInput();
					if (!geos.contains(pgeos[0])) {
						geos.add(pgeos[0]);
					}
				}
			} else if (geo.isGeoList()) {
				// TODO: note that there are a whole lot of other list algos
				if (Algos.isUsedFor(Commands.Sequence, geo)
						|| parentAlgorithm instanceof AlgoDependentList) {
					GeoElement[] pgeos = parentAlgorithm.getInput();
					if (pgeos.length > 1) {
						if (!geos.contains(pgeos[0])) {
							geos.add(pgeos[0]);
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
				for (GeoPolygon psnext : ((GeoPolyhedron) geo)
						.getPolygons()) {
					if (!geos.contains(psnext)
							&& predecessorsCovered(psnext, ancestors)) {
						geos.add(psnext);
					}
				}
	
				for (GeoPolygon ps2n : ((GeoPolyhedron) geo)
						.getPolygonsLinked()) {
					if (!geos.contains(ps2n)
							&& predecessorsCovered(ps2n, ancestors)) {
						geos.add(ps2n);
					}
				}
				GeoCoordSys1D[] segm = ((GeoPolyhedron) geo).getSegments3D();
				for (int j = 0; j < segm.length; j++) {
					if (!geos.contains(segm[j])
							&& predecessorsCovered(segm[j], ancestors)) {
						geos.add(segm[j]);
						GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
								segm[j].getEndPoint() };
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

				for (GeoPolygon psnext : ((GeoPolyhedronNet) geo)
						.getPolygons()) {
					if (!geos.contains(psnext)
							&& predecessorsCovered(psnext, ancestors)) {
						geos.add(psnext);
					}
				}
				for (GeoPolygon ps2n : ((GeoPolyhedronNet) geo)
						.getPolygonsLinked()) {
					if (!geos.contains(ps2n)
							&& predecessorsCovered(ps2n, ancestors)) {
						geos.add(ps2n);
					}
				}
				GeoCoordSys1D[] segm = ((GeoPolyhedronNet) geo).getSegments3D();
				for (int j = 0; j < segm.length; j++) {
					if (!geos.contains(segm[j])
							&& predecessorsCovered(segm[j], ancestors)) {
						geos.add(segm[j]);
						GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
								segm[j].getEndPoint() };
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
				if (parentAlgorithm instanceof AlgoQuadricLimitedPointPointRadiusCone
						|| parentAlgorithm instanceof AlgoQuadricLimitedPointPointRadiusCylinder
						|| parentAlgorithm instanceof AlgoQuadricLimitedConicHeightCone
						|| parentAlgorithm instanceof AlgoQuadricLimitedConicHeightCylinder) {
					TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
							geo);

					GeoElement[] pgeos = parentAlgorithm.getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])
								&& predecessorsCovered(pgeos[j], ancestors)) {
							geos.add(pgeos[j]);
						}
					}
					pgeos = parentAlgorithm.getOutput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])
								&& predecessorsCovered(pgeos[j], ancestors)) {
							geos.add(pgeos[j]);
						}
					}
				}

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
	 * @param geos
	 *            input and output
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
						&& geo2.getConstruction().isConstantElement(
								geo2) == Construction.Constants.NOT) {
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
	 * @param renameInScripts
	 *            whether to update references in scripts after rename
	 * @return list of elements
	 */
	protected static ArrayList<GeoElement> handleLabels(App app,
			ArrayList<String> labels, boolean renameInScripts) {
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
				if (renameInScripts) {
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

	public void paste(App app, AsyncOperation<String> stringAsyncOperation) {
		pasteFromXML(app);
	}
}
