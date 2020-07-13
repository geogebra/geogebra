/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DPartInterface;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasFaces;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.util.GPredicate;

/**
 * 
 * class for hitting objects with the mouse
 * 
 * @author Markus Hohenwarter
 */

// TODO change ArrayList to TreeSet
public class Hits extends ArrayList<GeoElement> {

	private static final long serialVersionUID = 1L;

	private int listCount;
	private int polyCount;
	private int imageCount;
	/** number of coord sys 2D */
	private int cs2DCount;

	private boolean hasXAxis;
	private boolean hasYAxis;
	private boolean hasZAxis;

	/** init the hits */
	public void init() {
		clear();
		listCount = 0;
		polyCount = 0;
		imageCount = 0;
		cs2DCount = 0;
		hasXAxis = false;
		hasYAxis = false;
		hasZAxis = false;
	}

	// Can't override and GWT don't support CLONE anyway.
	/**
	 * @return clone of the hits
	 */
	public Hits cloneHits() {
		Hits ret = createNewHits();
		if (this.size() > 0) {
			for (int i = 0; i < this.size(); i++) {
				ret.add(this.get(i));
			}
		}
		ret.listCount = this.listCount;
		ret.polyCount = this.polyCount;
		ret.imageCount = this.imageCount;
		ret.hasXAxis = this.hasXAxis;
		ret.hasYAxis = this.hasYAxis;
		ret.hasZAxis = this.hasZAxis;
		ret.cs2DCount = this.cs2DCount;

		return ret;
	}

	/** adding specifics GeoElements */
	@Override
	public boolean add(GeoElement geo) {
		// geo is not added if countGeo() returns false
		return countGeo(geo) && super.add(geo);
	}

	private boolean countGeo(GeoElement geo) {
		if (geo == null) {
			return false;
		}

		if (!geo.isSelectionAllowed(null)) {
			// #3771
			if (!(geo instanceof GeoList && ((GeoList) geo).drawAsComboBox())
					&& !geo.isGeoInputBox()) {
				return false;
			}
		}

		if (geo instanceof GeoCoordSys2D) {
			cs2DCount++;
		}

		if (geo.isGeoList()) {
			listCount++;
		} else if (geo.isGeoImage()) {
			imageCount++;
		} else if (isPolygon(geo)) {
			polyCount++;

		} else if (geo instanceof GeoAxisND) {
			switch (((GeoAxisND) geo).getType()) {
				default:
				case GeoAxisND.X_AXIS:
					hasXAxis = true;
					break;
				case GeoAxisND.Y_AXIS:
					hasYAxis = true;
					break;
				case GeoAxisND.Z_AXIS:
					hasZAxis = true;
					break;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends GeoElement> hits) {
		for (GeoElement geo: hits) {
			countGeo(geo);
		}
		return super.addAll(hits);
	}

	/**
	 * @return count of hit images
	 */
	public int getImageCount() {
		return imageCount;
	}

	/**
	 * @return count of hit lists
	 */
	public int getListCount() {
		return listCount;
	}

	/**
	 * @return true if x axis is hit
	 */
	public boolean hasXAxis() {
		return hasXAxis;
	}

	/**
	 * @return true if y axis is hit
	 */
	public boolean hasYAxis() {
		return hasYAxis;
	}

	/**
	 * @return true if z axis is hit
	 */
	public boolean hasZAxis() {
		return hasZAxis;
	}

	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	/*
	 * final public GeoElement getLabelHit(Point p) { if
	 * (!app.isLabelDragsEnabled()) return null; DrawableIterator it =
	 * allDrawableList.getIterator(); while (it.hasNext()) { Drawable d =
	 * it.next(); if (d.hitLabel(p.x, p.y)) { GeoElement geo =
	 * d.getGeoElement(); if (geo.isEuclidianVisible()) return geo; } } return
	 * null; }
	 */

	/**
	 * absorbs new elements in hits2 Tam: 2011/5/21
	 * 
	 * @param hits2
	 *            hits to be absorbed
	 * @return the repeated elements in hits2
	 */
	public Hits absorb(ArrayList<GeoElement> hits2) {
		Hits ret = new Hits();
		for (int i = 0; i < hits2.size(); i++) {
			if (!contains(hits2.get(i))) {
				add(hits2.get(i));
			} else {
				ret.add(hits2.get(i));
			}
		}
		return ret;
	}

	/**
	 * remove all the points Tam, 5/22/2011
	 */
	final public void removeAllPoints() {
		for (int i = size() - 1; i >= 0; i--) {
			GeoElement geo = get(i);
			if (geo == null || geo.isGeoPoint()) {
				remove(i);
			}
		}
	}

	/**
	 * Removes all transparent geos. Transparency criteria same as in
	 * EuclidianController3D::decideHideIntersection
	 */
	final public void removeAllDimElements() {
		for (int i = size() - 1; i >= 0; i--) {
			GeoElement geo = get(i);
			if (geo == null
					|| geo.isRegion() && (geo.getAlphaValue() < 0.1f
							|| geo.getLineThickness() < 0.5f)
					|| geo.isPath() && geo.getLineThickness() < 0.5f) {
				remove(i);
			}
		}
	}

	/**
	 * A polygon is only kept if none of its sides is also in hits.
	 */
	final public void removeHasSegmentsIfSidePresent() {
		removeHasSegmentsDependingSidePresent(false);
	}

	/**
	 * Removes polygons that are in hits but none of their sides is hit
	 */
	final public void removeHasSegmentsIfSideNotPresent() {
		removeHasSegmentsDependingSidePresent(true);
	}

	/**
	 * Returns hits that are suitable for new point mode. A polygon is only kept
	 * if one of its sides is also in hits.
	 */
	final public void keepOnlyHitsForNewPointMode() {
		removeHasSegmentsDependingSidePresent(true);
	}

	/**
	 * remove all conics hitted on the filling, not on the boundary
	 */
	final public void removeConicsHittedOnFilling() {
		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoConic()) {
				if (((GeoConicND) geo).getLastHitType() == HitType.ON_FILLING) {
					it.remove();
				}
			}
		}
	}

	private void removeHasSegmentsDependingSidePresent(
			boolean sidePresentWanted) {

		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo instanceof HasSegments) {
				boolean sidePresent = false;
				GeoSegmentND[] sides = ((HasSegments) geo).getSegments();
				if (sides != null) {
					for (GeoSegmentND side : sides) {
						if (this.contains(side)) {
							sidePresent = true;
							break;
						}
					}
				}

				if (sidePresent != sidePresentWanted) {
					it.remove();
				}
			}
		}
	}

	/**
	 * remove HasFaces geos if a face is present in this
	 */
	final public void removeHasFacesIfFacePresent() {
		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo instanceof HasFaces) {
				HasFaces hasFaces = (HasFaces) geo;
				for (int k = 0; k < hasFaces.getFacesSize(); k++) {
					if (this.contains(hasFaces.getFace(k))) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	/**
	 * remove sliders from hits
	 */
	final void removeSliders() {

		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoNumeric() && ((GeoNumeric) geo).isSlider()) {
				it.remove();
			}
		}
	}

	/**
	 * remove all hits after geo
	 * 
	 * @param geo
	 *            last geo
	 */
	final public void removeGeosAfter(GeoElementND geo) {
		for (int i = size() - 1; i >= 0 && get(i) != geo; i--) {
			remove(i);
		}
	}

	/**
	 * remove segments from all present polygons
	 */
	final public void removeSegmentsFromPolygons() {
		ArrayList<GeoSegmentND> toRemove = new ArrayList<>();

		for (GeoElement geo : this) {
			if (isPolygon(geo)) {
				GeoSegmentND[] sides = ((GeoPolygon) geo).getSegments();
				toRemove.addAll(Arrays.asList(sides));
			}
		}

		for (GeoSegmentND d : toRemove) {
			this.remove(d);
		}

	}

	/**
	 * @return vectors and points in this hits; NOT numerics
	 */
	final public Hits getPointVectorNumericHits() {
		Hits ret = new Hits();
		for (int i = 0; i < size(); ++i) {
			GeoElement geo = get(i);
			if (
			// geo.isGeoNumeric() ||
			geo.isGeoVector() || geo.isGeoPoint()) {
				ret.add(geo);
			}
		}

		return ret;
	}

	/**
	 * removes all polygons if there are other types of geo
	 */
	final public void removePolygons() {
		if (size() - polyCount > 0) {

			for (int i = size() - 1; i >= 0; i--) {
				GeoElement geo = get(i);
				if (isPolygon(geo)) {
					remove(i);
				}
			}
		}
	}

	private boolean isPolygon(GeoElement geo) {
		return geo.isGeoPolygon() && !geo.isShape();
	}

	/**
	 * Removes all polygons
	 */
	final public void removeAllPolygons() {
		for (int i = size() - 1; i >= 0; i--) {
			GeoElement geo = get(i);
			if (isPolygon(geo)) {
				remove(i);
			}
		}
	}

	/**
	 * Removes all planes
	 */
	final public void removeAllPlanes() {
		for (int i = size() - 1; i >= 0; i--) {
			GeoElement geo = get(i);
			if (geo.isGeoPlane()) {
				remove(i);
			}
		}
	}

	/**
	 * remove all polygons but one
	 */
	public void removeAllPolygonsButOne() {
		int toRemove = polyCount - 1;
		for (int i = size() - 1; i >= 0 && toRemove > 0; i--) {
			GeoElement geo = get(i);
			if (isPolygon(geo)) {
				remove(i);
				toRemove--;
			}
		}
	}

	/**
	 * 
	 * @return poly count in this
	 */
	public int getPolyCount() {
		return polyCount;
	}

	/**
	 * Find the first set of geo corresponding to one of the tests. Found geos
	 * are supposed to be in the same intervall.
	 * 
	 * @param tests
	 *            class tests
	 * @return correct hits (if exist)
	 */
	final public Hits keepFirsts(TestGeo... tests) {
		Hits ret = new Hits();
		TestGeo testFound = null;
		boolean goFurther = true;

		for (int i = 0; i < size() && goFurther; i++) {
			GeoElement geo = get(i);
			if (testFound == null) {
				for (int j = 0; j < tests.length && testFound == null; j++) {
					if (tests[j].check(geo)) {
						testFound = tests[j];
						ret.add(geo);
					}
				}
			} else {
				if (testFound.check(geo)) {
					ret.add(geo);
				} else {
					goFurther = false;
				}
			}
		}

		return ret;
	}

	/**
	 * Removes all polygonsand quadrics but one; for 3D
	 */
	public void removeAllPolygonsAndQuadricsButOne() {
		// for 3D
	}

	/**
	 * Removes images
	 */
	public void removeImages() {
		for (int i = size() - 1; i >= 0; i--) {
			GeoElement geo = get(i);
			if (geo.isGeoImage()) {
				remove(i);
			}
		}
	}

	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	 * final public ArrayList getMoveableHits(Point p) { return
	 * getMoveableHits(getHits(p)); }
	 */

	/**
	 * @param view
	 *            view
	 * @return array of changeable GeoElements out of hits
	 */
	final public Hits getMoveableHits(EuclidianViewInterfaceSlim view) {
		return getMoveables(view, TestGeo.MOVEABLE, null);
	}

	/**
	 * PointRotateable
	 * 
	 * @param view
	 *            view
	 * @param rotCenter
	 *            rotation center
	 * @return array of changeable GeoElements out of hits that implement
	 */
	final public Hits getPointRotateableHits(EuclidianViewInterfaceSlim view,
			GeoPointND rotCenter) {
		return getMoveables(view, TestGeo.ROTATEMOVEABLE, rotCenter);
	}

	/**
	 * @return hits that have selection allowed
	 */
	final public Hits getSelectableHits() {
		GeoElement geo;
		Hits selectableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = get(i);
			if (geo.isSelectionAllowed(null)) {
				selectableList.add(geo);
			}
		}

		return selectableList;
	}

	/**
	 * @param view
	 *            view
	 * @param test
	 *            either ROTATEMOVEABLE or MOVEABLE
	 * @param rotCenter
	 *            rotation center
	 * @return (rotate)moveable geos
	 */
	protected Hits getMoveables(EuclidianViewInterfaceSlim view, TestGeo test,
			GeoPointND rotCenter) {

		GeoElement geo;
		Hits moveableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = get(i);
			switch (test) {
			case MOVEABLE:
				// moveable object
				if (geo.isMoveable(view)) {
					moveableList.add(geo);
					// Application.debug("moveable GeoElement = "+geo);
				}
				// point with changeable parent coords
				else if (geo.isGeoPoint()) {
					GeoPointND point = (GeoPointND) geo;
					if (point.hasChangeableCoordParentNumbers()) {
						moveableList.add((GeoElement) point);
					}
				}
				// not a point, but has moveable input points
				else if (geo.hasMoveableInputPoints(view)) {
					moveableList.add(geo);
				}
				break;

			case ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf(rotCenter)) {
						moveableList.add(geo);
					}
				} else if (geo.hasMoveableInputPoints(view)) {
					moveableList.add(geo);
				}
				break;

			default:
				break;
			}
		}

		/*
		 * if (moveableList.size() == 0) return null; else return moveableList;
		 */
		return moveableList;
	}

	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	 * final public ArrayList getHits(Point p, Class geoclass, ArrayList result)
	 * { return getHits(getHits(p), geoclass, false, result); }
	 */

	/**
	 * @param geoclass
	 *            test for type that
	 * @param result
	 *            hits object for result
	 * @return array of GeoElements NOT passing test out of hits
	 */
	final public Hits getOtherHits(TestGeo geoclass, Hits result) {
		return getHits(geoclass, true, result);
	}

	/**
	 * @param geoclass
	 *            test for type
	 * @param result
	 *            hits object for result
	 * @return array of GeoElements passing test out of hits
	 */
	final public Hits getHits(TestGeo geoclass, Hits result) {
		return getHits(geoclass, false, result);
	}

	/**
	 * Returns array of polygons with n points out of hits.
	 * 
	 * @return
	 *
	 * 		final public ArrayList getPolygons(ArrayList hits, int n,
	 *         ArrayList polygons) { // search for polygons in hits that exactly
	 *         have the needed number of // points polygons.clear();
	 *         getHits(hits, GeoPolygon.class, polygons); for (int k =
	 *         polygons.size() - 1; k > 0; k--) { GeoPolygon poly = (GeoPolygon)
	 *         polygons.get(k); // remove poly with wrong number of points if (n
	 *         != poly.getPoints().length) polygons.remove(k); } return
	 *         polygons; }
	 */

	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * 
	 * @param geoclass
	 *            test
	 * 
	 * @param other
	 *            == true: returns array of GeoElements NOT passing test out of
	 *            hits.
	 * @param result
	 *            Hits in which the result should be stored
	 * @return result
	 */

	final protected Hits getHits(TestGeo geoclass, boolean other, Hits result) {
		result.clear();
		for (int i = 0; i < size(); ++i) {
			boolean success = geoclass.check(get(i));
			if (other) {
				success = !success;
			}
			if (success) {
				result.add(get(i));
			}
		}
		// return result.size() == 0 ? null : result;

		return result;
	}

	/**
	 * @param result
	 *            hits to store result
	 * @return result
	 */
	public final Hits getRegionHits(Hits result) {
		result.clear();
		for (int i = 0; i < size(); ++i) {
			if (get(i).isRegion()) {
				result.add(get(i));
			}
		}
		// return result.size() == 0 ? null : result;

		return result;
	}

	/**
	 * Stores all GeoElements of any of type geoclasses to result list.
	 * 
	 * @param geoclasses
	 *            test
	 * 
	 * @param other
	 *            == true: returns array of GeoElements NOT passing any test out
	 *            of hits.
	 * @param result
	 *            Hits in which the result should be stored
	 * @return result
	 */
	final public Hits getHits(TestGeo[] geoclasses, boolean other, Hits result) {
		result.clear();
		for (int i = 0; i < size(); ++i) {
			for (int j = 0; j < geoclasses.length; ++j) {
				boolean success = geoclasses[j].check(get(i));
				if (other) {
					success = !success;
				}
				if (success) {
					result.add(get(i));
				}
			}
		}

		return result;
	}

	/**
	 * return first hit of given class
	 * 
	 * @param geoclass
	 *            test
	 * @return first hit of given class
	 */
	final public GeoElement getFirstHit(TestGeo geoclass) {
		for (int i = 0; i < size(); ++i) {
			if (geoclass.check(get(i))) {
				return get(i);
			}
		}

		return null;
	}

	/**
	 * Stores all GeoElements of type GeoPoint, GeoVector, GeoNumeric to result
	 * list.
	 * 
	 */
	/*
	 * final protected ArrayList getRecordableHits(ArrayList hits, ArrayList
	 * result) { if (hits == null) return null;
	 * 
	 * result.clear(); for (int i = 0; i < hits.size(); ++i) { GeoElement hit =
	 * (GeoElement)hits.get(i); boolean success = (hit.isGeoPoint() ||
	 * hit.isGeoVector() || hit.isGeoNumeric()); if (success)
	 * result.add(hits.get(i)); } return result.size() == 0 ? null : result; }
	 */

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	/*
	 * final public ArrayList getTopHits(Point p) { return
	 * getTopHits(getHits(p)); }
	 */

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * 
	 * @return list of hit points
	 * 
	 * @see EuclidianController#wrapMousePressed(AbstractEvent)
	 * @see EuclidianController#wrapMouseMoved(AbstractEvent)
	 */
	public Hits getTopHits() {
		if (isEmpty()) {
			return cloneHits();
		}

		// point in there?
		Hits topHitsList = new Hits();
		if (containsComboBox(topHitsList)) {
			getHits(TestGeo.GEOLIST_AS_COMBO, false, topHitsList);
			return topHitsList;
		}
		if (containsGeoPoint(topHitsList)) {
			// Hits topHitsList = new Hits();
			getHits(TestGeo.GEOPOINTND, false, topHitsList);
			return topHitsList;
		}
		if (containsGeoTextfield(topHitsList)) {
			getHits(TestGeo.GEOTEXTFIELD, false, topHitsList);
			return topHitsList;
		}
		// text in there?
		if (containsGeoText(topHitsList)) {
			getHits(TestGeo.GEOTEXT, false, topHitsList);
			return topHitsList;
		}

		if (containsGeoNumeric()) {
			getHits(TestGeo.GEONUMERIC, false, topHitsList);
			return topHitsList;
		}
		return cloneHits();
	}

	/**
	 * return hits at the top, limited to a number of nb
	 * 
	 * @param nb
	 *            number of top hits to return
	 * @return hits at the top, limited to a number of nb
	 */
	public Hits getTopHits(int nb) {
		Hits topHits = getTopHits();

		/*
		 * //remove all last elements, since topHits.size()<=nb
		 * for(;topHits.size()>nb;) topHits.remove(topHits.size()-1);
		 */

		Hits ret = new Hits();
		for (int i = 0; i < nb && i < topHits.size(); i++) {
			ret.add(topHits.get(i));
		}

		return ret;
	}

	/**
	 * @param nb
	 *            maximal number of hits
	 * @return first at most nb hits
	 */
	public Hits getHits(int nb) {
		Hits ret = createNewHits();
		for (int i = 0; i < nb && i < size(); i++) {
			ret.add(get(i));
		}

		return ret;
	}

	/**
	 * @return creates new instance of this class
	 */
	protected Hits createNewHits() {
		return new Hits();
	}

	/**
	 * @param depth
	 *            for 3D
	 * @param geoN
	 *            maximal number of returned geos
	 * @return top hits
	 */
	public Hits getTopHits(int depth, int geoN) {
		return getTopHits(geoN);
	}

	/**
	 * @return true if contains GeoPointND
	 */
	final public boolean containsGeoPoint() {
		for (int i = 0; i < size(); i++) {
			if (get(i).isGeoPoint()) {
				return true;
			}
		}
		return false;
	}

	final private boolean containsGeoNumeric() {
		for (int i = 0; i < size(); i++) {
			if (get(i).isGeoNumeric()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ret
	 *            if the point is found, it is added into ret
	 * @return true if contains GeoPointND
	 */
	final public boolean containsGeoPoint(Hits ret) {
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			if (geo.isGeoPoint()) {
				ret.add(geo);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ret
	 *            if the point is found, it is added into ret
	 * @return true if contains GeoPointND
	 */
	final public boolean containsGeoText(Hits ret) {
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			if (geo.isGeoText()) {
				ret.add(geo);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ret
	 *            hits
	 * @return whether gits contain input box
	 */
	final public boolean containsGeoTextfield(Hits ret) {
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			if (geo.isGeoInputBox()) {
				ret.add(geo);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ret
	 *            hits
	 * @return whether hits contain combobox
	 */
	final public boolean containsComboBox(Hits ret) {
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			if (geo.isGeoList() && ((GeoList) geo).drawAsComboBox()) {
				ret.add(geo);
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("hits: ");
		s.append(size());
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			s.append("\n hits(");
			s.append(i);
			s.append(") = ");
			s.append(geo.getLabel(StringTemplate.defaultTemplate));
		}
		return s.toString();
	}

	/**
	 * 
	 * @param list
	 *            geo list
	 * @return true if contains at least one of the elements of the list
	 */
	public boolean intersect(ArrayList<GeoElement> list) {
		for (GeoElement geo : list) {
			if (contains(geo)) {
				return true;
			}
		}
		return false;
	}

	private Hits getWithMetaHits(GPredicate<GeoElement> filter) {
		Hits result = new Hits();

		for (GeoElement geo : this) {
			if (geo.getMetasLength() > 0) {
				for (GeoElement meta : ((FromMeta) geo).getMetas()) {
					if (filter.test(meta) && !result.contains(meta)) {
						result.add(meta);
					}
				}
			}

			if (filter.test(geo)) {
				result.add(geo);
			}
		}

		return result;
	}

	/**
	 * 
	 * @return hits that has finite volume
	 */
	public Hits getFiniteVolumeIncludingMetaHits() {
		return getWithMetaHits(new GPredicate<GeoElement>() {
			@Override
			public boolean test(GeoElement geo) {
				return geo instanceof HasVolume && ((HasVolume) geo).hasFiniteVolume();
			}
		});
	}

	/**
	 * 
	 * @return hits that has finite volume
	 */
	public Hits getPolyhedronsIncludingMetaHits() {
		return getWithMetaHits(new GPredicate<GeoElement>() {
			@Override
			public boolean test(GeoElement geo) {
				return geo.isGeoPolyhedron();
			}
		});
	}

	/**
	 * WARNING : only GeoCoordSys2D, GeoQuadric3DInterface and
	 * GeoPolyhedronInterface implemented yet
	 * 
	 * @param ignoredGeos
	 *            geos that are ignored
	 * @return hits containing first surface (not included in ignoredGeos)
	 */
	final public Hits getFirstSurfaceBefore(ArrayList<GeoElement> ignoredGeos) {
		Hits ret = new Hits();
		for (int i = 0; i < size(); i++) {
			GeoElement geo = get(i);
			if (geo instanceof GeoCoordSys2D
					|| geo instanceof GeoQuadric3DInterface
					|| geo instanceof GeoQuadric3DLimitedInterface
					|| geo instanceof GeoPolyhedronInterface
					|| geo instanceof GeoFunctionNVar
					|| geo instanceof GeoImplicitSurfaceND) {
				if (!ignoredGeos.contains(geo)) {
					if (geo instanceof GeoQuadric3DPartInterface) { // temporary
																	// fix (TODO
																	// implement
																	// intersection
																	// GeoQuadric3DPart
																	// / plane)
						GeoElement meta = ((FromMeta) geo).getMetas()[0];
						if (!ignoredGeos.contains(meta)) {
							ret.add(meta);
							return ret;
						}
					}
					ret.add(geo);
					return ret;
				}
			}
		}

		return ret;
	}

	/**
	 * remove all polygons, if hits are not all instance of GeoCoordSys2D
	 */
	public void removePolygonsIfNotOnlyCS2D() {
		// String s = "cs2DCount="+cs2DCount+"/"+(size());

		if (size() - cs2DCount > 0) {
			removePolygons();
		}
	}

	/**
	 * 
	 * @return first 6 degrees of freedom moveable geo
	 */
	public GeoElement getFirstGeo6dofMoveable() {
		for (GeoElement geo : this) {
			if (geo.is6dofMoveable()) {
				return geo;
			}
		}

		return null;
	}

	/**
	 * Get hits only that are groupped together or have no group
	 *
	 *  @return the groupped geos.
	 */
	public Hits getHitsGroupped() {
		Hits ret = new Hits();
		for (int i = 0; i < size(); i++) {
			GeoElement geo = get(i);
			Group group = geo.getParentGroup();
			if (group == null || containsGroup(group)) {
				ret.add(geo);
			}
		}
		return ret;
	}

	private boolean containsGroup(Group group) {
		for (GeoElement geo: group.getGroupedGeos()) {
			if (!contains(geo)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}