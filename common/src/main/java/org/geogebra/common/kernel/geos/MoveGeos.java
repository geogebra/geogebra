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
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;

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

		// make sure list is not moved twice
		for (GeoElement geo : geosToMove) {
			if (geo.isGeoList()) {
				for (int i = 0; i < ((GeoList) geo).size(); i++) {
					geosToMove.remove(((GeoList) geo).get(i));
				}
			}
		}

		final ArrayList<GeoElement> geos = new ArrayList<>();
		for (GeoElement geo: geosToMove) {
			if (!geo.isLocked()) { // Non fixed elements only
				if (!geo.isGeoList() || shouldAddListAsWhole((GeoList) geo, view)) {
					addWithSiblingsAndChildNodes(geo, geos, view);
				} else {
					((GeoList) geo).elements().forEach(
							element -> addWithSiblingsAndChildNodes(element, geos, view));
				}
			}
		}

		boolean moved = false;
		final int size = geos.size();
		moveObjectsUpdateList.clear();
		moveObjectsUpdateList.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geos.get(i);
			final Coords position = (size == 1)
					&& (geo.getParentAlgorithm() != null) ? endPosition : null;
			moved = moveObject(geo, rwTransVec, position, viewDirection,
					view) || moved;
		}

		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause
		// multiple updates of the same algorithm)
		GeoElement.updateCascade(moveObjectsUpdateList, GeoElement.getTempSet(),
				false);

		//geoLists do not trigger the update of the cascade in the function call above
		for (GeoElement geo : geosToMove) {
			if (geo.isGeoList()) {
				if (geo.isIndependent()) {
					geo.resetDefinition();
				} else {
					((GeoList) geo).resetDefinitionDependentList();
				}
			}
		}
		return moved;
	}

	/**
	 * @param list GeoList
	 * @param view EuclidianView
	 * @return True if list contains of only movable objects and the list can therefore be added
	 * as a whole to the GeoElements that need to be moved, false else
	 */
	public static boolean shouldAddListAsWhole(GeoList list, EuclidianView view) {
		return list.elements().allMatch(geo -> !geo.isLocked() && geo.isMoveable(view)
				&& (containsFreeInputPoints(geo, view) || geo.isGeoPoint()))
				|| list.getElementType() == GeoClass.NUMERIC
				|| list.getCorrespondingCasCell() != null;
	}

	/* visible for tests */
	static void addWithSiblingsAndChildNodes(GeoElement geo, ArrayList<GeoElement> geos,
			EuclidianView view) {
		if (!geos.contains(geo)) {

			if (shouldTryAddingFreeInputPoints(geo) && canAddFreeInputPoints(geo, view)) {
				ArrayList<GeoElementND> freeInputs = geo.getFreeInputPoints(view);
				for (GeoElementND point : freeInputs) {
					addWithSiblingsAndChildNodes(point.toGeoElement(), geos, view);
				}
				return;
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
	 * @param geo GeoElement
	 * @return True if we should try to add the individual, free input points of a GeoElement -
	 * false else
	 */
	private static boolean shouldTryAddingFreeInputPoints(GeoElement geo) {
		return !geo.isMoveable() && !isOutputOfTranslate(geo) && !geo.isGeoList();
	}

	/**
	 * @param geo GeoElement
	 * @param view EuclidianView
	 * @return True if we can actually add free input points to the list of movable GeoElements,
	 * false else
	 */
	private static boolean canAddFreeInputPoints(GeoElement geo, EuclidianView view) {
		ArrayList<GeoElementND> freeInputs = geo.getFreeInputPoints(view);
		return freeInputs != null && !freeInputs.isEmpty()
				&& !freeInputsContainLockedElement(freeInputs)
				&& containsFreeInputPoints(geo, view);
	}

	/**
	 * @param freeInputs Free input points
	 * @return True if the list of free inputs contains at least one locked object, false else
	 */
	private static boolean freeInputsContainLockedElement(ArrayList<GeoElementND> freeInputs) {
		return freeInputs.stream().anyMatch(GeoElementND::isLocked);
	}

	/**
	 * @param geo GeoElement
	 * @param view EuclidianView
	 * @return True if <li>There is no parent algorithm OR</li>
	 * <li>The parent algorithm contains only free input points OR</li>
	 * <li>The GeoElement has movable input points</li>
	 */
	private static boolean containsFreeInputPoints(GeoElement geo, EuclidianView view) {
		AlgoElement parentAlgorithm = geo.getParentAlgorithm();
		return parentAlgorithm == null
				|| parentAlgorithm.hasOnlyFreeInputPoints(view)
				|| geo.hasMoveableInputPoints(view);
	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 *
	 * @return whether actual moving occurred
	 */
	private static boolean moveObject(GeoElement geo1, final Coords rwTransVec,
			final Coords endPosition, final Coords viewDirection,
			EuclidianView view) {
		boolean movedGeo;

		if (geo1.isMoveable()) {
			movedGeo = moveMoveableGeo(geo1, rwTransVec, endPosition,
					view);
		} else if (geo1.isGeoList() && !geo1.isLocked() && !geo1.isRandomGeo()
				&& geo1.getCorrespondingCasCell() == null) {
			((GeoList) geo1).elements().forEach(el -> moveMoveableGeo(el, rwTransVec, null,
					view));
			moveObjectsUpdateList.add(geo1);
			movedGeo = true;
		} else if (isOutputOfTranslate(geo1)) {
			movedGeo = moveTranslateOutput(geo1, rwTransVec, null, moveObjectsUpdateList);
		} else {
			ArrayList<GeoElement> tempMoveObjectList = geo1.kernel
					.getApplication().getSelectionManager()
					.getTempMoveGeoList();

			if (geo1.hasChangeableParent3D()) {
				movedGeo = geo1.getChangeableParent3D().move(rwTransVec,
						endPosition, viewDirection, moveObjectsUpdateList,
						tempMoveObjectList, view);
			} else {
				movedGeo = geo1.moveFromChangeableCoordParentNumbers(rwTransVec,
						endPosition, moveObjectsUpdateList, tempMoveObjectList);
			}
		}

		return movedGeo;
	}

	private static boolean moveTranslateOutput(GeoElement geo1, Coords rwTransVec,
			Coords endPosition, ArrayList<GeoElement> updateGeos) {
		AlgoElement algo = geo1.getParentAlgorithm();
		GeoElement in = algo.getInput(1).toGeoElement();
		boolean movedGeo = false;
		if (in.isGeoVector()) {
			ArrayList<GeoElement> tempMoveObjectList = geo1.kernel
					.getApplication().getSelectionManager()
					.getTempMoveGeoList();

			if (in.isIndependent()) {
				movedGeo = ((GeoVectorND) in).moveVector(rwTransVec, endPosition);
				GeoElement.addParentToUpdateList(in, updateGeos,
						tempMoveObjectList);
			} else {
				GeoPointND p = getMovablePointForVector(in);
				if (p != null) {
					movedGeo = p.movePoint(rwTransVec, endPosition);
					GeoElement.addParentToUpdateList(p.toGeoElement(), updateGeos,
							tempMoveObjectList);
				}
			}
		}
		return movedGeo;
	}

	/**
	 * Unwraps Vector(pt) to pt, checks that pt is independent.
	 * @param vec vector value, should be a vector or a point
	 * @return movable parent point or null
	 */
	public static GeoPointND getMovablePointForVector(GeoElementND vec) {
		if (vec.getParentAlgorithm() instanceof AlgoVectorPoint) {
			AlgoVectorPoint algoVector = (AlgoVectorPoint) vec
					.getParentAlgorithm();
			GeoPointND p = algoVector.getP();
			if (p.isIndependent()) {
				return p;
			}
		}
		if (vec instanceof GeoPointND && vec.isIndependent()) {
			return (GeoPointND) vec;
		}
		return null;
	}

	private static boolean moveMoveableGeo(GeoElement geo1, final Coords rwTransVec,
			final Coords endPosition, EuclidianView view) {
		if (geo1.isLockedPosition()) {
			return false;
		}
		boolean movedGeo = false;
		boolean changedPosition = false;
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

		// absolute position on screen
		else if (geo1.isAbsoluteScreenLocateable()
				&& ((AbsoluteScreenLocateable) geo1).isAbsoluteScreenLocActive()
				&& ((AbsoluteScreenLocateable) geo1).hasStaticLocation()) {
			final AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) geo1;
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
				changedPosition = true;
				movedGeo = screenLoc.needsUpdatedBoundingBox();
			}
		}
		// translateable
		else if (geo1.isTranslateable()) {
			final Translateable trans = (Translateable) geo1;
			trans.translate(rwTransVec);
			if (geo1.isGeoImage()) {
				changedPosition = true;
			}
			movedGeo = true;
		}

		// slider with RW position
		else if (geo1.isGeoNumeric()) {
			if (!geo.isLockedPosition()) {
				// real world screen position - GeoNumeric
				((GeoNumeric) geo).setRealWorldLoc(
						((GeoNumeric) geo).getRealWorldLocX()
								+ rwTransVec.getX(),
						((GeoNumeric) geo).getRealWorldLocY()
								+ rwTransVec.getY());
				changedPosition = true;
			}
		} else if (geo1.isGeoText()) {
			// check for GeoText with unlabeled start point
			final GeoText movedGeoText = (GeoText) geo1;
			if (movedGeoText.hasStaticLocation()) {
				// absolute location: change location
				final GeoPointND locPoint = movedGeoText
						.getStartPoint();
				if (locPoint != null) {
					locPoint.translate(rwTransVec);
					changedPosition = true;
					movedGeo = movedGeoText.needsUpdatedBoundingBox();
				}
			}
		}

		if (movedGeo) {
			addWithFreePointsToUpdateList(view, geo);
		}

		if (changedPosition) {
			geo.updateVisualStyleRepaint(GProperty.POSITION);
		}
		return movedGeo || changedPosition;
	}

	private static void addWithFreePointsToUpdateList(EuclidianView view, GeoElement geo) {
		moveObjectsUpdateList.add(geo);
		if (!ignoreFreePoints(geo)) {
			addFreePointsToUpdateList(geo.getFreeInputPoints(view));
		}
	}

	private static boolean ignoreFreePoints(GeoElement geo) {
		return geo.isGeoConic();
	}

	private static void addFreePointsToUpdateList(List<GeoElementND> freeInputPoints) {
		if (freeInputPoints == null) {
			return;
		}

		for (GeoElementND point: freeInputPoints) {
			moveObjectsUpdateList.add((GeoElement) point);
		}
	}

	private static boolean isOutputOfTranslate(GeoElement geo1) {
		return geo1.isTranslateable()
				&& geo1.getParentAlgorithm() instanceof AlgoTranslate;
	}

	/**
	 * Check if geos ar about to update.
	 * For testing only.
	 * @param geos to check.
	 * @return if the update list includes all the parameters.
	 */
	static boolean updateListHave(GeoElement... geos) {
		for (GeoElement geo: geos) {
			if (!moveObjectsUpdateList.contains(geo)) {
				return false;
			}
		}
		return true;
	}
}
