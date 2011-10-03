package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * 
 * class for hitting objects with the mouse
 * 
 * @author Markus Hohenwarter
 * @version
 */

//TODO change ArrayList to TreeSet 

public class Hits extends ArrayList<GeoElement> {
	
	
	private int listCount;
	protected int polyCount;
	private int imageCount;
	
	
	protected final int TEST_MOVEABLE = 1;
	protected final int TEST_ROTATEMOVEABLE = 2;

	
	
	

	/** init the hits */
	public void init(){
		clear();
		listCount = 0;
		polyCount = 0;
		imageCount = 0;
		
	}
	
	
	
	public Hits clone() {

		Hits ret = (Hits) super.clone();
		ret.listCount = this.listCount;
		ret.polyCount = this.polyCount;
		ret.imageCount = this.imageCount;

		return ret;
	} 
	
	/** adding specifics GeoElements */
	public boolean add(GeoElement geo){
		
		if (!geo.isSelectionAllowed()) return false;
		
		if (geo.isGeoList()) {
			listCount++;
		} else if (geo.isGeoImage()) {
			imageCount++;
		} else if (geo.isGeoPolygon()) {
			polyCount++;
		} 
		return super.add(geo);		
	}
	

	public int getImageCount(){
		return imageCount;
	}
	
	public int getListCount(){
		return listCount;
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
	 * @param hits2 
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
			GeoElement geo = (GeoElement) get(i);
			if (geo==null || geo.isGeoPoint())
				remove(i);
		}
	}
	
	
	final public void removeAllDimElements(){
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = (GeoElement) get(i);
			//transparency criteria same as in EuclidianController3D::decideHideIntersection
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
				if (((GeoConicND) geo).getLastHitType()==GeoConicND.HIT_TYPE_ON_FILLING){
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
		
		for (GeoSegmentND d : toRemove)
			this.remove(d);
				
	}
	
	
	
	
	/*
	 * remove sides of polygons present
	 *
	final public void removeSidesOfPolygons(){
		
		Iterator it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (geo.isGeoPolygon()) {
				GeoSegmentND [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) 
					this.remove(sides[k]);
			}				
		}				
				
	}
*/

	// replaces final public ArrayList getPointVectorNumericHits(Point p) {
	final public Hits getPointVectorNumericHits(){

		Hits ret = new Hits();
		for (int i = 0; i < size(); ++i) {
			GeoElement geo = (GeoElement) get(i);
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
				GeoElement geo = (GeoElement) get(i);
				if (geo.isGeoPolygon())
					remove(i);
			}
		}
	}

	final public void removeAllPolygons(){
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = (GeoElement) get(i);
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
			GeoElement geo = (GeoElement) get(i);
			if (geo.isGeoPolygon()){
				remove(i);
				toRemove--;
			}
		}
	}
	
	//for 3D only
	public void removeAllPolygonsAndQuadricsButOne(){

	}
	
	final public void removeAllButImages(){

	}
	
	public void removeImages() {
		for (int i = size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = (GeoElement) get(i);
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
	 * @param view 
	 * @return array of changeable GeoElements out of hits
	 */
	final public Hits getMoveableHits(EuclidianViewInterface view) {
		return getMoveables(view, TEST_MOVEABLE, null);
	}

	/**
	 * PointRotateable
	 * @param view 
	 * @param rotCenter 
	 * @return array of changeable GeoElements out of hits that implement 
	 */
	final public Hits getPointRotateableHits(EuclidianViewInterface view, GeoPointND rotCenter) {
		return getMoveables(view, TEST_ROTATEMOVEABLE, rotCenter);
	}


	protected Hits getMoveables(EuclidianViewInterface view, int test, GeoPointND rotCenter) {


		GeoElement geo;
		Hits moveableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = (GeoElement) get(i);
			switch (test) {
			case TEST_MOVEABLE:
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

			case TEST_ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf((GeoElement) rotCenter))
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
	 * @param geoclass 
	 * @param result 
	 * @return array of GeoElements NOT of type geoclass out of hits 
	 */
	final public Hits getOtherHits(Class geoclass,
			Hits result) {
		return getHits(geoclass, true, result);
	}

	final public Hits getHits(Class geoclass,
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
	 * @param geoclass 
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of type geoclass out of
	 *            hits.
	 * @param result Hits in which the result should be stored
	 * @return result
	 */
	final protected Hits getHits(Class geoclass,
			boolean other, Hits result) {


		result.clear();
		for (int i = 0; i < size(); ++i) {
			boolean success = geoclass.isInstance(get(i));
			if (other)
				success = !success;
			if (success)
				result.add(get(i));
		}
		//return result.size() == 0 ? null : result;
		
		return result;
	}
	
	final protected Hits getRegionHits(
			Hits result) {
		result.clear();
		for (int i = 0; i < size(); ++i) {
			if (((GeoElement)get(i)).isRegion())
				result.add(get(i));
		}
		//return result.size() == 0 ? null : result;
		
		return result;
	}
	
	

	/**
	 * Stores all GeoElements of any of type geoclasses to result list.
	 * @param geoclasses 
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of any of type geoclasses out of
	 *            hits.
	 * @param result Hits in which the result should be stored
	 * @return result
	 */
	final public Hits getHits(Class[] geoclasses,
			boolean other, Hits result) {

		result.clear();
		for (int i = 0; i < size(); ++i) {
			for (int j = 0; j<geoclasses.length; ++j) {
				boolean success = geoclasses[j].isInstance(get(i));
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
	 * @param geoclass
	 * @return first hit of given class
	 */
	final public GeoElement getFirstHit(Class geoclass) {

		for (int i = 0; i < size(); ++i) {
			if(geoclass.isInstance(get(i)))
				return (GeoElement) get(i);
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
	 * @see EuclidianController#mousePressed(MouseEvent)
	 * @see EuclidianController#mouseMoved(MouseEvent)
	 */
	public Hits getTopHits() {
		
		if (isEmpty())
			return clone();
		
		// point in there?
		Hits topHitsList = new Hits();
		if (containsGeoPoint(topHitsList)) {
			//Hits topHitsList = new Hits();
			getHits(GeoPointND.class, false, topHitsList);
			return topHitsList;
		} else
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

	public Hits getHits(int nb){
		Hits ret = createNewHits();
		for(int i=0;i<nb && i<size(); i++)
			ret.add(get(i));
		
		return ret;
	}
	
	protected Hits createNewHits() {
		return new Hits();
	}



	//for 3D only
	public Hits getTopHits(int depth, int geoN) {
		return getTopHits(geoN);
	}
	final public boolean containsGeoPoint() {

		for (int i = 0; i < size(); i++) {
			if (((GeoElement) get(i)).isGeoPoint())
				return true;
		}
		return false;
	}

	
	final public boolean containsGeoPoint(Hits ret) {

		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = (GeoElement) get(i);
			if (geo.isGeoPoint()){
				ret.add(geo);
				return true;
			}
		}
		return false;
	}
	
	
	
	public String toString(){
		String s = "hits: "+size();
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = (GeoElement) get(i);
			s+="\n hits("+i+") = "+geo.getLabel();
		}
		return s;
	}




}
