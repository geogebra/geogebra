/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.euclidian;

import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicND.HitType;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasVolume;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * class for hitting objects with the mouse
 * 
 * @author Markus Hohenwarter
 */

//TODO change ArrayList to TreeSet 
public class Hits extends ArrayList<GeoElement> {
	
	private static final long serialVersionUID = 1L;
	
	private int listCount;
	private int polyCount;
	private int imageCount;
	private boolean hasXAxis, hasYAxis;

	/** init the hits */
	public void init(){
		clear();
		listCount = 0;
		polyCount = 0;
		imageCount = 0;
		hasXAxis = false;
		hasYAxis = false;
	}
	//Can't override and GWT don't support CLONE anyway.
	@SuppressWarnings("all")
	public Hits clone() {
		Hits ret = newHits();
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
		
		return ret;
	} 
	
	/**
	 * 
	 * @return new instance of the same class
	 */
	protected Hits newHits(){
		return new Hits();
	}
	
	/** adding specifics GeoElements */
	@Override
	public boolean add(GeoElement geo) {

		if (!geo.isSelectionAllowed())
			return false;

		if (geo.isGeoList()) {
			listCount++;
		} else if (geo.isGeoImage()) {
			imageCount++;
		} else if (geo.isGeoPolygon()) {
			polyCount++;

		} else if (geo instanceof GeoAxis) {
			if (((GeoAxis) geo).getType() == GeoAxisND.X_AXIS) {
				hasXAxis = true;
			} else {
				hasYAxis = true;
			}
		}
		return super.add(geo);
	}
	

	/**
	 * @return count of hit images
	 */
	public int getImageCount(){
		return imageCount;
	}
	
	/**
	 * @return count of hit lists
	 */
	public int getListCount(){
		return listCount;
	}	
	
	/**
	 * @return true if x axis is hit
	 */
	public boolean hasXAxis(){
		return hasXAxis;
	}
	/**
	 * @return true if y axis is hit
	 */
	public boolean hasYAxis(){
		return hasYAxis;
	}
	
	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	/*
	final public GeoElement getLabelHit(Point p) {
		if (!app.isLabelDragsEnabled()) return null;
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible())
					return geo;
			}
		}
		return null;
	}
	*/
	
	/** absorbs new elements in hits2
	 * Tam: 2011/5/21
	 * @param hits2 hits to be absorbed
	 * @return the repeated elements in hits2
	 */
	public Hits absorb(ArrayList<GeoElement> hits2){
		Hits ret = new Hits();
		for(int i=0; i<hits2.size(); i++){
			if (!contains(hits2.get(i)))
				add(hits2.get(i));
			else
				ret.add(hits2.get(i));
		}
		return ret;
	}

	/** remove all the points
	 * Tam, 5/22/2011
	 */
	final public void removeAllPoints(){
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = get(i);
			if (geo==null || geo.isGeoPoint())
				remove(i);
		}
	}
	
	
	/**
	 * Removes all transparent geos.
	 * Transparency criteria same as in EuclidianController3D::decideHideIntersection
	 */
	final public void removeAllDimElements(){
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = get(i);
			if (geo==null || 
					geo.isRegion() && (geo.getAlphaValue() < 0.1f || geo.getLineThickness() <0.5f) ||
					geo.isPath() && geo.getLineThickness() < 0.5f)
				remove(i);
		}
	}
	
	/**
	 * A polygon is only kept if none of its sides is also in
	 * hits.
	 */
	final public void removePolygonsIfSidePresent(){
		removePolygonsDependingSidePresent(false);
	}
	
	/**
	 * Removes polygons that are in hits but none of their
	 *  sides is hit
	 */
	final public void removePolygonsIfSideNotPresent(){
		removePolygonsDependingSidePresent(true);
	}
	
	/**
	 * Returns hits that are suitable for new point mode.
	 * A polygon is only kept if one of its sides is also in
	 * hits.
	 */
	final public void keepOnlyHitsForNewPointMode() {	
		removePolygonsDependingSidePresent(true);
	}

	/**
	 * remove all conics hitted on the filling, not on the boundary
	 */
	final public void removeConicsHittedOnFilling(){
		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoConic()){
				if (((GeoConicND) geo).getLastHitType()==HitType.ON_FILLING){
					it.remove();
				}
			}
		}
	}
	
	final private void removePolygonsDependingSidePresent(boolean sidePresentWanted){
	
		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPolygon()) {
				boolean sidePresent = false;
				GeoSegmentND [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) {
					if (this.contains(sides[k])) {
						sidePresent = true;
						break;
					}
				}
				
				if (sidePresent!=sidePresentWanted){
					it.remove();					
				}
			}				
		}						
	}
	
	/**
	 * remove segments from all present polygons
	 */
	final public void removeSegmentsFromPolygons(){
		
		ArrayList<GeoSegmentND> toRemove = new ArrayList<GeoSegmentND>();
		
		Iterator<GeoElement> it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPolygon()) {
				GeoSegmentND [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) {
					toRemove.add(sides[k]);
				}
				
			}				
		}
		
		for (GeoSegmentND d : toRemove) {
			this.remove(d);
		}
				
	}
	
	/**
	 * @return vectors and points in this hits; NOT numerics
	 */
	final public Hits getPointVectorNumericHits(){

		Hits ret = new Hits();
		for (int i = 0; i < size(); ++i) {
			GeoElement geo = get(i);
			if (
					//geo.isGeoNumeric() ||
					 geo.isGeoVector()
					|| geo.isGeoPoint())
				ret.add(geo);
		}
		
		return ret;
	}
	
	//replaces EuclidianView . final public ArrayList getHits(Point p, boolean includePolygons) {
	/**
	 * removes all polygons
	 */
	final public void removePolygons(){
		
		
		if (size() - polyCount > 0) {
			
			for (int i = size() - 1 ; i >= 0 ; i-- ) {
				GeoElement geo = get(i);
				if (geo.isGeoPolygon())
					remove(i);
			}
		}
	}

	/**
	 * Removes all polygons
	 */
	final public void removeAllPolygons(){
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = get(i);
			if (geo.isGeoPolygon())
				remove(i);
		}
	}	
	
	/**
	 * remove all polygons but one
	 */
	public void removeAllPolygonsButOne(){
		int toRemove = polyCount-1;
		for (int i = size() - 1 ; i >= 0 && toRemove>0; i-- ) {
			GeoElement geo = get(i);
			if (geo.isGeoPolygon()){
				remove(i);
				toRemove--;
			}
		}
	}
	

	/**
	 * Removes all polygonsand quadrics but one; for 3D 
	 */
	public void removeAllPolygonsAndQuadricsButOne(){
		//for 3D
	}
	
	/**
	 * Keeps only images; for 3D
	 */
	final public void removeAllButImages(){
		//for 3D
	}
	
	/**
	 * Removes images
	 */
	public void removeImages() {
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = get(i);
			if (geo.isGeoImage())
				remove(i);
		}
	}

	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	final public ArrayList getMoveableHits(Point p) {
		return getMoveableHits(getHits(p));
	}
	*/

	/**
	 * @param view view
	 * @return array of changeable GeoElements out of hits
	 */
	final public Hits getMoveableHits(EuclidianViewInterfaceSlim view) {
		return getMoveables(view, Test.MOVEABLE, null);
	}

	/**
	 * PointRotateable
	 * @param view view
	 * @param rotCenter rotation center 
	 * @return array of changeable GeoElements out of hits that implement 
	 */
	final public Hits getPointRotateableHits(EuclidianViewInterfaceSlim view, GeoPointND rotCenter) {
		return getMoveables(view, Test.ROTATEMOVEABLE, rotCenter);
	}
	
	/**
	 * @return hits that have selection allowed
	 */
	final public Hits getSelectableHits(){
		GeoElement geo;
		Hits selectableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = get(i);
			if (geo.isSelectionAllowed())
				selectableList.add(geo);
		}
		
		return selectableList;
	}

	/**
	 * @param view view
	 * @param test either ROTATEMOVEABLE or MOVEABLE
	 * @param rotCenter rotation center
	 * @return (rotate)moveable geos 
	 */
	protected Hits getMoveables(EuclidianViewInterfaceSlim view, Test test, GeoPointND rotCenter) {

		GeoElement geo;
		Hits moveableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = get(i);
			switch (test) {
			case MOVEABLE:
				// moveable object
				if (geo.isMoveable(view)) {
					moveableList.add(geo);
					//Application.debug("moveable GeoElement = "+geo);
				}
				// point with changeable parent coords
				else if (geo.isGeoPoint()) {
					GeoPointND point = (GeoPointND) geo;
					if (point.hasChangeableCoordParentNumbers())
						moveableList.add((GeoElement)point);
				}
				// not a point, but has moveable input points
				else if (geo.hasMoveableInputPoints(view)) {
					moveableList.add(geo);
				}
				break;

			case ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf(rotCenter))
						moveableList.add(geo);
					
				}else if (geo.hasMoveableInputPoints(view)) {
					moveableList.add(geo);
				}

				break;
			}
		}
		
		/*
		if (moveableList.size() == 0)
			return null;
		else
			return moveableList;
			*/
		return moveableList;
	}
	
	
	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	final public ArrayList getHits(Point p, Class geoclass, ArrayList result) {
		return getHits(getHits(p), geoclass, false, result);
	}
	*/

	/**
	 * @param geoclass test for type that
	 * @param result hits object for result
	 * @return array of GeoElements NOT passing test out of hits 
	 */
	final public Hits getOtherHits(Test geoclass,
			Hits result) {
		return getHits(geoclass, true, result);
	}

	/**
	 * @param geoclass test for type
	 * @param result hits object for result
	 * @return array of GeoElements passing test out of hits 
	 */
	final public Hits getHits(Test geoclass,
			Hits result) {
		return getHits(geoclass, false, result);
	}

	/**
	 * Returns array of polygons with n points out of hits.
	 * 
	 * @return
	 *
	final public ArrayList getPolygons(ArrayList hits, int n, ArrayList polygons) {
		// search for polygons in hits that exactly have the needed number of
		// points
		polygons.clear();
		getHits(hits, GeoPolygon.class, polygons);
		for (int k = polygons.size() - 1; k > 0; k--) {
			GeoPolygon poly = (GeoPolygon) polygons.get(k);
			// remove poly with wrong number of points
			if (n != poly.getPoints().length)
				polygons.remove(k);
		}
		return polygons;
	}*/

	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * @param geoclass test
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT passing test out of
	 *            hits.
	 * @param result Hits in which the result should be stored
	 * @return result
	 */
	
	final protected Hits getHits(Test geoclass,
			boolean other, Hits result) {


		result.clear();
		for (int i = 0; i < size(); ++i) {
			boolean success = geoclass.check(get(i));
			if (other)
				success = !success;
			if (success)
				result.add(get(i));
		}
		//return result.size() == 0 ? null : result;
		
		return result;
	}
	
	/**
	 * @param result hits to store result
	 * @return result
	 */
	public final Hits getRegionHits(
			Hits result) {
		result.clear();
		for (int i = 0; i < size(); ++i) {
			if (get(i).isRegion())
				result.add(get(i));
		}
		//return result.size() == 0 ? null : result;
		
		return result;
	}

	/**
	 * Stores all GeoElements of any of type geoclasses to result list.
	 * @param geoclasses test
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT passing any test out of
	 *            hits.
	 * @param result Hits in which the result should be stored
	 * @return result
	 */
	final public Hits getHits(Test[] geoclasses,
			boolean other, Hits result) {

		result.clear();
		for (int i = 0; i < size(); ++i) {
			for (int j = 0; j<geoclasses.length; ++j) {
				boolean success = geoclasses[j].check(get(i));
				if (other)
					success = !success;
				if (success)
					result.add(get(i));
			}
		}
		
		return result;
	}
	
	/**
	 * return first hit of given class
	 * @param geoclass test
	 * @return first hit of given class
	 */
	final public GeoElement getFirstHit(Test geoclass) {

		for (int i = 0; i < size(); ++i) {
			if(geoclass.check(get(i)))
				return get(i);
		}

		return null;
	}
	
	/**
	 * Stores all GeoElements of type GeoPoint, GeoVector, GeoNumeric to result list.
	 * 
	 */
	/*
	final protected ArrayList getRecordableHits(ArrayList hits, ArrayList result) {
		if (hits == null)
			return null;

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement hit = (GeoElement)hits.get(i);
			boolean success = (hit.isGeoPoint() || hit.isGeoVector() || hit.isGeoNumeric());
			if (success)
				result.add(hits.get(i));
		}
		return result.size() == 0 ? null : result;
	}
	*/

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	/*
	final public ArrayList getTopHits(Point p) {
		return getTopHits(getHits(p));
	}
	*/

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * @return list of hit points
	 * 
	 * @see EuclidianController#wrapMousePressed(AbstractEvent)
	 * @see EuclidianController#wrapMouseMoved(AbstractEvent)
	 */
	public Hits getTopHits() {
		
		if (isEmpty())
			return clone();
		
		// point in there?
		Hits topHitsList = new Hits();
		if (containsGeoPoint(topHitsList)) {
			//Hits topHitsList = new Hits();
			getHits(Test.GEOPOINTND, false, topHitsList);
			return topHitsList;
		} 
		return clone();
	}
	
	/** return hits at the top, limited to a number of nb
	 * @param nb number of top hits to return
	 * @return hits at the top, limited to a number of nb
	 */
	public Hits getTopHits(int nb){
		Hits topHits = getTopHits();
		
		/*
		//remove all last elements, since topHits.size()<=nb
		for(;topHits.size()>nb;)
			topHits.remove(topHits.size()-1);
			*/
		
		Hits ret = new Hits();
		for(int i=0;i<nb && i<topHits.size(); i++)
			ret.add(topHits.get(i));
		
		return ret;
	}

	/**
	 * @param nb maximal number of hits
	 * @return first at most nb hits
	 */
	public Hits getHits(int nb){
		Hits ret = createNewHits();
		for(int i=0;i<nb && i<size(); i++)
			ret.add(get(i));
		
		return ret;
	}
	
	/**
	 * @return creates new instance of this class 
	 */
	protected Hits createNewHits() {
		return new Hits();
	}

	/**
	 * @param depth for 3D 
	 * @param geoN maximal number of returned geos
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
			if (get(i).isGeoPoint())
				return true;
		}
		return false;
	}
	/**
	 * @param ret if the point is found, it is added into ret
	 * @return true if contains GeoPointND
	 */
	final public boolean containsGeoPoint(Hits ret) {

		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			if (geo.isGeoPoint()){
				ret.add(geo);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		String s = "hits: "+size();
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = get(i);
			s+="\n hits("+i+") = "+geo.getLabel(StringTemplate.defaultTemplate);
		}
		return s;
	}
	
	
	/**
	 * 
	 * @param list geo list
	 * @return true if contains at least one of the elements of the list
	 */
	public boolean intersect(ArrayList<GeoElement> list){
		for (GeoElement geo : list)
			if (contains(geo))
				return true;
		return false;
	}

	
	
	/**
	 * 
	 * @return hits that has finite volume
	 */
	public Hits getFiniteVolumeIncludingMetaHits(){
		Hits result = new Hits();
		
		for (GeoElement geo : this){
			//first check if is segment/polygon/quadric side from a geo that has finite volume
			if (geo.getMetasLength() > 0){
				for (GeoElement meta : ((FromMeta) geo).getMetas())
					addFiniteVolume(result, meta);
			//check if the geo has finite volume
			}else{
				addFiniteVolume(result, geo);
			}
		}
		
		
		return result;
	}
	
	private static void addFiniteVolume(Hits result, GeoElement geo){
		if (geo instanceof HasVolume){
			if (((HasVolume) geo).hasFiniteVolume()){
				result.add(geo);
			}
		}
	}
	
}
