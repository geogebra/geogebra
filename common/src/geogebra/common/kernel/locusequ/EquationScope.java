/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sergio
 * Represents the scope for a locus. It is basically a map
 * from points an elements to their symbolic representation.
 */
public class EquationScope {
	
	protected EquationPointMap pointMap;
    protected EquationElementMap elementsMap;
    protected Set<EquationAuxiliarSymbolicPoint> auxiliarPoints;
    protected int auxPointIndex;
    
    public EquationScope() {
        this.pointMap = new EquationPointMap(this);
        this.elementsMap = new EquationElementMap(this);
        this.initAuxiliarPoints();
    }
    
    public EquationScope(GeoPoint locusPoint, GeoPoint movingPoint) {
        this.pointMap = new EquationPointMap(locusPoint, movingPoint, this);
        this.elementsMap = new EquationElementMap(this);
        this.initAuxiliarPoints();
    }
    
    private void initAuxiliarPoints() {
        this.auxiliarPoints = new HashSet<EquationAuxiliarSymbolicPoint>();
        this.auxPointIndex = 1;
    }
    
    public Collection<EquationPoint> getAllPoints() {
        return this.pointMap.getAllPoints();
    }
    
    public Collection<? extends EquationSymbolicValue> getAllVariables() {
        Set<EquationSymbolicValue> set = new HashSet<EquationSymbolicValue>();
        
        for(EquationPoint p : getAllPoints()) {
            set.addAll(p.getVariables());
        }
        
        for(EquationPoint p : this.auxiliarPoints) {
            set.addAll(p.getVariables());
        }
        
        return set;
    }
    
    public EquationPoint getPoint(GeoPoint p) {
        return this.pointMap.getOrCreate(p);
    }
    
    public EquationElement getElement(GeoElement key) {
        return this.elementsMap.getOrCreate(key);
    }
    
    public EquationList getRestrictionsFromAlgo(AlgoElement algo) {
    	
    	if(algo instanceof RestrictionAlgoForLocusEquation) {
    		return ((EquationRestriction) algo.buildEquationElementForGeo(null, this)).getEquationList();
    	} 
    	return EquationList.getEmptyList();
    }
    
    /**
     * @param orig The point that will be replaced
     * @param target The point that will replace orig.
     */
    public void identifyPoints(GeoPoint orig, GeoPoint target) {
        this.pointMap.identify(orig, target);
    }
    
    /**
     * @param orig The element that will be replaced
     * @param target The element that will replace orig.
     */
    public void identifyElements(GeoElement orig, GeoElement target) {
        this.elementsMap.identify(orig, target);
    }

    /**
     * Adds a point and its symbolic representation.
     * @param point to be added
     * @param symbolic representation
     */
    public void addPoint(GeoPoint point, EquationPoint symbolic) {
        this.pointMap.put(point, symbolic);
    }

    /**
     * Check if p is the moving point.
     * @param p {@link GeoPoint}
     * @return true iff they are the exact same point.
     */
    public boolean isMovingPoint(GeoPoint p) {
        return this.pointMap.isMovingPoint(p);
    }

    /**
     * Check if p is the locus point.
     * @param p {@link GeoPoint}
     * @return true iff they are the exact same point.
     */
    public boolean isLocusPoint(GeoPoint p) {
        return this.pointMap.isLocusPoint(p);
    }

    /**
     * Generates a new auxiliary point
     * @return a new auxiliary
     */
    public EquationAuxiliarSymbolicPoint getNewAuxiliarPoint() {
        EquationAuxiliarSymbolicPoint p = new EquationAuxiliarSymbolicPoint(this.auxPointIndex);
        
        this.auxPointIndex += 2;
        this.registerAuxiliarPoint(p);
        
        return p;
    }

    /**
     * Register a new auxiliary point.
     * @param p the point to register.
     */
    public void registerAuxiliarPoint(EquationAuxiliarSymbolicPoint p) {
        this.auxiliarPoints.add(p);
    }
    
    /**
     * Retrieves a copy of the set of auxiliary points.
     * @return a copy of the collection.
     */
    public Set<EquationAuxiliarSymbolicPoint> getAuxiliarSymbolicPoints() {
        return new HashSet<EquationAuxiliarSymbolicPoint>(this.auxiliarPoints);
    }
}
