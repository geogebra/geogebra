/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoLinePointLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoLinePointLine}
 */
public class EquationLinePointLine extends EquationGenericLine {
	
	private GeoLine l;
    private EquationGenericLine lequ;

    /**
     * General constructor
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationLinePointLine(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        AlgoLinePointLine algo = (AlgoLinePointLine) this.getResult().getParentAlgorithm();

        this.l = algo.getl();
        this.lequ = (EquationGenericLine) this.getScope().getElement(this.l);
        
        this.setVector(this.lequ.getVector());
        this.setPoint(algo.getP());
    }
}
