package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
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
	public static boolean moveObjects(List<GeoElement> geosToMove,
			final Coords rwTransVec, final Coords endPosition,
			final Coords viewDirection, EuclidianView view) {
		if (moveObjectsUpdateList == null) {
			moveObjectsUpdateList = new ArrayList<>();
		}
		List<GeoElement> geos = geosToMove;
		final ArrayList<GeoElement> geos2 = new ArrayList<>();

		// remove duplicates, eg drag Circle[A,A]
		for (int i = 0; i < geos.size(); i++) {
			if (!geos2.contains(geos.get(i))) {
				geos2.add(geos.get(i));
			}
		}

		geos = geos2;

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
			/*
			 * Michael Borcherds check for isGeoPoint() as it makes the mouse
			 * jump to the position of the point when dragging eg Image with one
			 * corner, Rigid Polygon and stops grid-lock working properly but is
			 * needed for eg dragging (a + x(A), b + x(B))
			 */
			// AbstractApplication.debug((geo.getParentAlgorithm() == null) + "
			// "
			// + size + " " + geo.getClassName()+"
			// "+geo.getLabel(StringTemplate.defaultTemplate));
			final Coords position = (size == 1)
					&& (geo.getParentAlgorithm() != null) ? endPosition : null;
			moved = moveObject(geo, rwTransVec, position, viewDirection,
					moveObjectsUpdateList, view, geos.size() < 2) || moved;

		}

		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause
		// multiple updates of the same algorithm)
		GeoElement.updateCascade(moveObjectsUpdateList, GeoElement.getTempSet(),
				false);

		return moved;
	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 * 
	 * @return whether actual moving occurred
	 */
	private static boolean moveObject(GeoElement geo1, final Coords rwTransVec,
			final Coords endPosition, final Coords viewDirection,
			final ArrayList<GeoElement> updateGeos, EuclidianView view,
			boolean moveParentPoints) {
		boolean movedGeo = false;
		GeoElement geo = geo1;
		// moveable geo
		if (geo1.isMoveable()) {
			// point
			if (geo1.isGeoPoint()) {

				if (geo1.getParentAlgorithm() instanceof AlgoDynamicCoordinatesInterface) {
					final GeoPointND p = ((AlgoDynamicCoordinatesInterface) geo1
							.getParentAlgorithm()).getParentPoint();
					movedGeo = p.movePoint(rwTransVec, endPosition);
					geo = (GeoElement) p;
				} else {
					movedGeo = geo1.movePoint(rwTransVec, endPosition);
				}
			}

			// vector
			else if (geo1.isGeoVector()) {
				movedGeo = geo1.moveVector(rwTransVec, endPosition);
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
					if (!((GeoNumeric) geo).isSliderFixed()) {
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
		}

		// non-moveable geo

		else if (geo1.isTranslateable()
				&& geo1.getParentAlgorithm() instanceof AlgoTranslate) {

			AlgoElement algo = geo1.getParentAlgorithm();
			GeoElement[] input = algo.getInput();
			GeoElement in = input[1];
			if (in.isGeoVector()) {
				ArrayList<GeoElement> tempMoveObjectList = geo1.kernel
						.getApplication().getSelectionManager()
						.getTempMoveGeoList();

				if (in.isIndependent()) {
					movedGeo = in.moveVector(rwTransVec, endPosition);
					GeoElement.addParentToUpdateList(in, updateGeos,
							tempMoveObjectList);
				} else if (in.getParentAlgorithm() instanceof AlgoVectorPoint) {
					AlgoVectorPoint algoVector = (AlgoVectorPoint) in
							.getParentAlgorithm();
					GeoElement p = (GeoElement) algoVector.getP();
					if (p.isIndependent()) {
						movedGeo = p.movePoint(rwTransVec, endPosition);
						GeoElement.addParentToUpdateList(p, updateGeos,
								tempMoveObjectList);
					}
				}
			}

		}

		else {
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
			if (!movedGeo) {
				ArrayList<GeoPointND> freeInputPoints = geo1
						.getFreeInputPoints(view);

				// eg macro like Hexagon[G_8], if the hexagon is dragged then
				// G_8
				// needs updating
				// handled for mouse drag in
				// EuclidianController.addMovedGeoElementFreeInputPointsToTranslateableGeos
				// needed here for moving with arrow keys
				if (moveParentPoints && freeInputPoints != null
						&& freeInputPoints.size() > 0) {
					for (int i = 0; i < freeInputPoints.size(); i++) {
						moveObject(((GeoElement) freeInputPoints.get(i)),
								rwTransVec, endPosition, viewDirection,
								updateGeos, view, false);
					}
					movedGeo = true;
				}
			}
		}

		return movedGeo;
	}
}
