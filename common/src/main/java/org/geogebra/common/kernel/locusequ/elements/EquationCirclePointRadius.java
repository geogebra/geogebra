/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.dist2;
import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.sqr;

import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
import org.geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 * EquationElement for AlgoCirclePointRadius
 */
public class EquationCirclePointRadius extends EquationGenericConic {
    
    /**
     * General constructor.
     * @param el {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationCirclePointRadius(final GeoElement el, final EquationScope scope) {
        super(el, scope);
        this.computeMatrix();
    }

    @Override
    protected void computeMatrix() {
        AlgoCirclePointRadius algo = (AlgoCirclePointRadius) this.getResult().getParentAlgorithm();
        
        EquationPoint center = this.getScope().getPoint((GeoPoint) algo.getCenter());
        
        // Work out radius.
        EquationExpression r2 = null;
        
        GeoElement rGeo = algo.getRadiusGeo();
        
        if(rGeo.isGeoNumeric()) {
            EquationExpression r = EquationNumericValue.from(((GeoNumeric) rGeo).getValue());
            r2 = sqr(r);
        } else if (rGeo.isGeoSegment()) {
            GeoSegment s = (GeoSegment) rGeo;
            r2 = dist2(this.getScope().getPoint(s.getStartPoint()),
                       this.getScope().getPoint(s.getEndPoint()));
        }
                
        this.setMatrix(matrixForCircle(center, r2));
    }
}
