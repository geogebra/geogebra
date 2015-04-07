/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;

import org.geogebra.common.kernel.algos.AlgoDiameterLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
/**
 * @author sergio
 * EquationElement for {@link AlgoDiameterLine}
 */
public class EquationDiameterLine extends EquationGenericLine {

    /**
     * General constructor.
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationDiameterLine(final GeoElement line, final EquationScope scope) {
        super(line, scope);
        
        AlgoDiameterLine algo  = (AlgoDiameterLine) this.getResult().getParentAlgorithm();
        
        EquationGenericLine  l = (EquationGenericLine)  this.getScope().getElement((GeoElement) algo.getLine());
        EquationGenericConic c = (EquationGenericConic) this.getScope().getElement(algo.getConic());
        
        EquationExpression[] m = c.getMatrix();
        
        this.setA(sum(times(m[0], l.getA()), times(m[3], l.getB())));
        this.setB(sum(times(m[3], l.getA()), times(m[1], l.getB())));
        this.setC(sum(times(m[4], l.getA()), times(m[5], l.getB())));
    }
}
