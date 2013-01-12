/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoLineBisector;
import geogebra.common.kernel.algos.AlgoLineBisectorSegment;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationMidpoint;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;


/**
 * @author sergio
 * Common base class for bisectors.
 */
public abstract class EquationGenericBisector extends EquationGenericLine {

    /**
     * Empty constructor in case a subclass needs it.
     */
    protected EquationGenericBisector() {}
    
    /**
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericBisector(final GeoElement line, final EquationScope scope) {
        super(line, scope);
    }
    
    protected void setExtremePoints(final GeoPoint a, final GeoPoint b) {
        this.setExtremePoints(this.getScope().getPoint(a),
                              this.getScope().getPoint(b));
    }
    
    protected void setExtremePoints(final GeoPoint a, final EquationPoint b) {
        this.setExtremePoints(this.getScope().getPoint(a), b);
    }
    
    protected void setExtremePoints(final EquationPoint a, final GeoPoint b) {
        this.setExtremePoints(a, this.getScope().getPoint(b));
    }
    
    protected void setExtremePoints(final EquationPoint a, final EquationPoint b) {
        EquationPoint m = new EquationMidpoint(a,b);
        GeoPoint midpoint = null;
        
        AlgoElement algo = this.getResult().getParentAlgorithm();

        if(algo instanceof AlgoLineBisector) {
            midpoint = ((AlgoLineBisector) algo).getMidPoint();
        } else if(algo instanceof AlgoLineBisectorSegment) {
            midpoint = ((AlgoLineBisectorSegment) algo).getMidPoint();
        }
        
        this.getScope().addPoint(midpoint, m);
        
        this.setPoint(midpoint); this.setPoint(m);
        this.setVector(new SymbolicVector(a,b).normal());
    }
}
