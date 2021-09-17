package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Library class for moving geos by drag
 */
public class MoveGeos {
	private static volatile ArrayList<GeoElement> moveObjectsUpdateList;

	/**
	 * Translates all GeoElement objects in geos by a vector in real world
	 * coordinates or by (xPixel, yPixel) in screen coordinates.
	 * 
	 * @param geosToMove
	 *            geos to be moved
	 * @param rwTransVec
	 *            translation vector
	 * @param endPosition
	 *            end position; may be null
	 * @param viewDirection
	 *            direction of view
	 * @param view
	 *            euclidian view
	 * @return true if something was moved
	 */
	public static boolean moveObjects(List<? extends GeoElement> geosToMove,
			final Coords rwTransVec, final Coords endPosition,
			final Coords viewDirection, EuclidianView view) {
		if (moveObjectsUpdateList == null) {
			moveObjectsUpdateList = new ArrayList<>();
		}
		final ArrayList<GeoElement> geos = new ArrayList<>();

		for (GeoElement geo: geosToMove) {
			addWithSiblingsAndChildNodes(geo, geos, view); // also removes duplicates
		}
		boolean moved = false;
		final int size = geos.size();
		moveObjectsUpdateList.clear();
		moveObjectsUpdateList.ensureCapacity(size);

		for (int i = 0; i < size; i++) {
			final GeoElement geo = geos.get(i);
			if (geo.isGeoList()) {
				moveObjectsUpdateList.add(geo);
				continue;
			}
			final Coords position = (size == 1)
					&& (geo.getParentAlgorithm() != null) ? endPosition : null;
			moved = moveObject(geo, rwTransVec, position, viewDirection,
					moveObjectsUpdateList, view) || moved;
		}

		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause
		// multiple updates of the same algorithm)
		GeoElement.updateCascade(moveObjectsUpdateList, GeoElement.getTempSet(),
				false);
		return moved;
	}

	/* visible for tests */
	static void addWithSiblingsAndChildNodes(GeoElement geo, ArrayList<GeoElement> geos,
			EuclidianView view) {
		if (!geos.contains(geo)) {
			if (!geo.isMoveable() && !isOutputOfTranslate(geo)) {
				ArrayList<GeoPointND> freeInputs = geo.getFreeInputPoints(view);
				if (freeInputs != null && !freeInputs.isEmpty()) {
					for (GeoPointND point: freeInputs) {
						addWithSiblingsAndChildNodes(point.toGeoElement(), geos, view);
					}
					return;
				}
			}
			geos.add(geo);
			Group group = geo.getParentGroup();
			if (group != null) {
				for (GeoElement sibling : group.getGroupedGeos()) {
					addWithSiblingsAndChildNodes(sibling, geos, view);
				}
			}
			if (geo instanceof GeoMindMapNode) {
				for (GeoElement child : ((GeoMindMapNode) geo).getChildren()) {
					addWithSiblingsAndChildNodes(child, geos, view);
				}
			}

		}
	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 * 
	 * @return whether actual moving occurred
	 */
	private static boolean moveObject(GeoElement geo1, final Coords rwTransVec,
			final Coords endPosition, final Coords viewDirection,
			final ArrayList<GeoElement> updateGeos, EuclidianView view) {
		boolean movedGeo = false;

		if (geo1.isMoveable()) {
			movedGeo = moveMoveableGeo(geo1, rwTransVec, endPosition,
					updateGeos, view);
		} else if (isOutputOfTranslate(geo1)) {
			movedGeo = moveTranslateOutput(geo1, rwTransVec, endPosition, updateGeos);
		} else {
			ArrayList<GeoElement> tempMoveObjectList = geo1.kernel
					.getApplication().getSelectionManager()
					.getTempMoveGeoList();

			if (geo1.hasChangeableParent3D()) {
				movedGeo = geo1.getChangeableParent3D().move(rwTransVec,
						endPosition, viewDirection, updateGeos,
						tempMoveObjectList, view);
			} else {
				movedGeo = geo1.moveFromChangeableCoordParentNumbers(rwTransVec,
						endPosition, updateGeos, tempMoveObjectList);
			}
		}

		return movedGeo;
	}

	private static boolean moveTranslateOutput(GeoElement geo1, Coords rwTransVec,
			Coords endPosition, ArrayList<GeoElement> updateGeos) {
		AlgoElement algo = geo1.getParentAlgorithm();
		GeoElement[] input = algo.getInput();
		GeoElement in = input[1];
		boolean movedGeo = false;
		if (in.isGeoVector()) {
			ArrayList<GeoElement> tempMoveObjectList = geo1.kernel
					.getApplication().getSelectionManager()
					.getTempMoveGeoList();

			if (in.isIndependent()) {
				movedGeo = ((GeoVectorND) in).moveVector(rwTransVec, endPosition);
				GeoElement.addParentToUpdateList(in, updateGeos,
						tempMoveObjectList);
			} else if (in.getParentAlgorithm() instanceof AlgoVectorPoint) {
				AlgoVectorPoint algoVector = (AlgoVectorPoint) in
						.getParentAlgorithm();
				GeoElement p = (GeoElement) algoVector.getP();
				if (p.isIndependent()) {
					movedGeo = ((GeoPointND) p).movePoint(rwTransVec, endPosition);
					GeoElement.addParentToUpdateList(p, updateGeos,
							tempMoveObjectList);
				}
			}
		}
		return movedGeo;
	}

	private static boolean moveMoveableGeo(GeoElement geo1, final Coords rwTransVec,
			final Coords endPosition,
			final ArrayList<GeoElement> updateGeos, EuclidianView view) {
		if (geo1.isLockedPosition()) {
			return false;
		}
		boolean movedGeo = false;
		GeoElement geo = geo1;
		// point
		if (geo1.isGeoPoint()) {

			if (geo1.getParentAlgorithm() instanceof AlgoDynamicCoordinatesInterface) {
				final GeoPointND p = ((AlgoDynamicCoordinatesInterface) geo1
						.getParentAlgorithm()).getParentPoint();
				movedGeo = p.movePoint(rwTransVec, endPosition);
				geo = (GeoElement) p;
			} else {
				movedGeo = ((GeoPointND) geo1).movePoint(rwTransVec, endPosition);
			}
		}

		// vector
		else if (geo1.isGeoVector()) {
			movedGeo = ((GeoVectorND) geo1).moveVector(rwTransVec, endPosition);
		}

		// translateable
		else if (geo1.isTranslateable()) {
			final Translateable trans = (Translateable) geo1;
			trans.translate(rwTransVec);
			movedGeo = true;
		}

		// absolute position on screen
		else if (geo1.isAbsoluteScreenLocateable()) {
			final AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) geo1;
			if (screenLoc.isAbsoluteScreenLocActive()) {
				final int vxPixel = (int) Math
						.round(geo1.kernel.getXscale() * rwTransVec.getX());
				final int vyPixel = -(int) Math
						.round(geo1.kernel.getYscale() * rwTransVec.getY());
				final int x = screenLoc.getAbsoluteScreenLocX() + vxPixel;
				final int y = screenLoc.getAbsoluteScreenLocY() + vyPixel;
				DrawableND drawable = view.getDrawableFor(geo);
				// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=org.geogebra.common.kernel.geos.GeoElement&tm=moveObject&nid&an&c&s=new_status_desc
				if (drawable != null) {
					screenLoc.setAbsoluteScreenLoc(x, y);
					movedGeo = true;
				}
			} else if (geo1.isGeoNumeric()) {
				if (!geo.isLockedPosition()) {
					// real world screen position - GeoNumeric
					((GeoNumeric) geo).setRealWorldLoc(
							((GeoNumeric) geo).getRealWorldLocX()
									+ rwTransVec.getX(),
							((GeoNumeric) geo).getRealWorldLocY()
									+ rwTransVec.getY());
					movedGeo = true;
				}
			} else if (geo1.isGeoText()) {
				// check for GeoText with unlabeled start point
				final GeoText movedGeoText = (GeoText) geo1;
				if (movedGeoText.hasAbsoluteLocation()) {
					// absolute location: change location
					final GeoPointND locPoint = movedGeoText
							.getStartPoint();
					if (locPoint != null) {
						locPoint.translate(rwTransVec);
						movedGeo = true;
					}
				}
			}
		}
		if (movedGeo) {
			if (updateGeos != null) {
				updateGeos.add(geo);
			} else {
				geo.updateCascade();
			}
		}
		return movedGeo;
	}

	private static boolean isOutputOfTranslate(GeoElement geo1) {
		return geo1.isTranslateable()
				&& geo1.getParentAlgorithm() instanceof AlgoTranslate;
	}
}
