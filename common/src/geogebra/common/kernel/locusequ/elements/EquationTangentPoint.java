/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoTangentPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationAuxiliarSymbolicPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * EquationElement for {@link AlgoTangentPoint}
 */
public class EquationTangentPoint extends EquationGenericLine {


    /**
     * General constructor
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationTangentPoint(final GeoElement line, final EquationScope scope) {
        super(line, scope);
        
        AlgoTangentPoint algo = (AlgoTangentPoint) this.getResult().getParentAlgorithm();
        
        GeoPoint p = algo.getPoint();
        
        this.setPoint(p);
        EquationGenericConic conic = (EquationGenericConic) this.getScope().getElement(algo.getConic());
        
        EquationPolarLine polar = conic.getPolarLine(this.getEquationPoint());
        
        EquationAuxiliarSymbolicPoint pAux = polar.getNewIncidentPoint();
        pAux.addIncidence(conic);
        
        this.setVector(new SymbolicVector(pAux, this.getEquationPoint()));
    }
}
