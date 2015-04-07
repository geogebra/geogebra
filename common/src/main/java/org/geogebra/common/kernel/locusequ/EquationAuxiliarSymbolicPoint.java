/**
 * 
 */
package org.geogebra.common.kernel.locusequ;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.arith.EquationAuxiliarSymbolicValue;

/**
 * @author sergio
 * Represents an auxiliary symbolic point. In case it is needed for a computation.
 */
public class EquationAuxiliarSymbolicPoint extends EquationSymbolicPoint {
    /**
     * Set of EquationElements to which this point is incident.
     */
    protected List<EquationElement> incidences;
    
    /**
     * @param v id.
     */
    public EquationAuxiliarSymbolicPoint(int v) {
        super();
        this.x = new EquationAuxiliarSymbolicValue(v+0);
        this.y = new EquationAuxiliarSymbolicValue(v+1);
        this.z = new EquationAuxiliarSymbolicValue(v+2);
        this.incidences = new ArrayList<EquationElement>();
    }
    
    /**
     * Adds an incidence.
     * @param element for incidence.
     * @return whether the element was added or not.
     */
    public boolean addIncidence(final EquationElement element) {
        return this.incidences.add(element);
    }
    
    /**
     * Getter for the restriction.
     * @return a copy of the collection of the restrictions.
     */
    public EquationList getRestrictions() {
        EquationList el = new EquationList();
        
        for(EquationElement element : this.incidences) {
            el.addAll(element.forPoint(this));
        }
        
        return el;
    }
    
    @Override
    protected String getId() {
        return AUXILIAR_SYMBOLIC_ID;
    }

    @Override
    public GeoPoint getPoint() {
        return null;
    }
}
