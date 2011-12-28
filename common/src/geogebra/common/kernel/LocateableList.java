package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


import java.util.ArrayList;



/**
 * List of Locateables linked to a point
 * 
 * @author ggb3D
 *
 */
@SuppressWarnings("serial")
public class LocateableList extends ArrayList<Locateable> {
	
	private GeoPointND point;

	
	/** constructor
	 * @param point
	 */
	public LocateableList(GeoPointND point){
		super();
		this.point = point;
	}
	
	
	/**
	 * Tells this point that the given Locateable has this point
	 * as start point.
	 * @param l 
	 */
	public void registerLocateable(Locateable l) {	
		if (contains(l)) return;
		
		// add only locateables that are not already
		// part of the updateSet of this point
		AlgoElementInterface parentAlgo = l.toGeoElement().getParentAlgorithm();
		if (parentAlgo == null ||
			!(((GeoElement) point).algoUpdateSetContains(parentAlgo))) {
			// add the locatable
			add(l);			
		}
	}
	
	
	/**
	 * Tells this point that the given Locatable no longer has this point
	 * as start point.
	 * @param l 
	 */
	public void unregisterLocateable(Locateable l) {
		remove(l);
	}
	
	
	
	
	/**
	 * Tells Locateables that their start point is removed
	 * and calls super.remove()
	 */
	public void doRemove(){
		
		// copy locateableList into array
		Object [] locs = toArray();	
		clear();
		
		// tell all locateables 
		for (int i=0; i < locs.length; i++) {		
			Locateable loc = (Locateable) locs[i];
			loc.removeStartPoint(point);				
			loc.toGeoElement().updateCascade();			
		}		
		
	}
	
	
	
	
	
	

}
