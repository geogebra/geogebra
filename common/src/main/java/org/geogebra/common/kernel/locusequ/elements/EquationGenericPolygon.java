/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.locusequ.EquationElement;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Base class for polygons.
 */
public abstract class EquationGenericPolygon extends EquationElement {


    private GeoSegment[] segments;

    /**
     * Empty constructor in case a subclass needs it.
     */
    protected EquationGenericPolygon() {}
    
    /**
     * General Constructor
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericPolygon(final GeoElement element, final EquationScope scope) {
        super(element, scope);
    }
    
    @Override
    protected EquationList forPointImpl(EquationPoint p) {
        EquationList el = new EquationList(this.getSegments().length);
        
        for(GeoSegment s : this.getSegments()) {
        	// Polygon internally uses AlgoJoinPointsSegment for creating
        	// each segment.
            el.addAll(this.getScope().getElement(s).forPoint(p));
        }
        
        return el;
    }
    
    /**
     * @return a copy of segments array.
     */
    protected GeoSegment[] getSegments() {
    	GeoSegment[] res = new GeoSegment[this.segments.length];
    	System.arraycopy(this.segments, 0, res, 0, this.segments.length);
        return res;
    }
    
    /**
     * @param sis array of segments.
     */
    protected void setSegments(final GeoSegmentND[] sis) {
        this.segments = new GeoSegment[sis.length];
        
        for(int i = 0; i < sis.length; i++) {
            this.segments[i] = (GeoSegment) sis[i];
        }
    }

    @Override
    public boolean isAlgebraic() {
        return false;
    }
}
