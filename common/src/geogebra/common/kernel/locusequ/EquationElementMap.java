/**
 * 
 */
package geogebra.common.kernel.locusequ;


import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.geos.GeoElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sergio
 * Scope for elementss. Maps elements to symbolic representation.
 */
public class EquationElementMap {

    
    protected Map<GeoElement, EquationElement> container;
    private EquationScope scope;
    //private EquationParser parser;
    
    /**
     * Create a new map associated to scope.
     * @param scope {@link EquationScope} associated to this map.
     */
    public EquationElementMap(EquationScope scope) {
        this.container = new HashMap<GeoElement, EquationElement>();
        this.scope = scope;
        //this.parser = new EquationParser(this.scope);
    }
    
    /**
     * Retrieves the symbolic representation, if any, of an element.
     * @param key element.
     * @return symbolic representation for key, null if it is not found.
     */
    public EquationElement get(final GeoElement key) {
        return this.container.get(key);
    }
    
    /**
     * Adds a {@link GeoElement}�and its symbolic representation.
     * @param key the {@link GeoElement}
     * @param value its symbolic representation.
     * @return value if it was added.
     */
    public EquationElement put(final GeoElement key, final EquationElement value){
        return this.container.put(key, value);
    }
    
    /**
     * Like get, but it creates the object if it does no exists.
     * @param key the {@link GeoElement}
     * @return a symbolic representation of it.
     */
    public EquationElement getOrCreate(final GeoElement key) {
        EquationElement res = this.get(key);
        if(res == null) {
            res = (EquationElement) createEquationElement(key);
            this.put(key, res);
        }
        return res;
    }

    /**
     * Creates a new symbolic representation for key.
     * @param key the {@link GeoElement}
     * @return a new symbolic representation.
     */
    private EquationElementInterface createEquationElement(final GeoElement key) {
    	AlgoElement algo = key.getParentAlgorithm();
    	
    	if(algo == null) {
    		return EquationRestriction.getEmptyRestriction();
    	}
    	return algo.buildEquationElementForGeo(key, this.scope);
    }
    
    /**
     * Identifies two different elements.
     * @param orig the element that will get the symbolic representation from target.
     * @param target produces the symbolic representation.
     */
    public void identify(GeoElement orig, GeoElement target) {
    	if(orig.getGeoClassType() == target.getGeoClassType()) {
    		this.container.put(orig, this.getOrCreate(target));
    	}
    }
}
