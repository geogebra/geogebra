/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.equation;
import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;

import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;

/**
 * @author sergio
 * Base class for Conic Parts
 */
public abstract class EquationGenericConicPart extends EquationGenericConic {

    /**
     * General Constructor
     * @param conic {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericConicPart(final GeoElement conic, final EquationScope scope) {
        super(conic, scope);
    }

    @Override
    public boolean isAlgebraic() { return false; }

    /**
     * @return true iff GeoConicPart type is CONIC_PART_ARC.
     */
    public boolean isArc() {
        return ((GeoConicPart) this.getResult()).getConicPartType() == GeoConicPart.CONIC_PART_ARC;
    }

    /**
     * @return true iff GeoConicPart type is CONIC_PART_SECTOR.
     */
    public boolean isSector() {
        return ((GeoConicPart) this.getResult()).getConicPartType() == GeoConicPart.CONIC_PART_SECTOR;
    }
    
    
    /**
     * If current part is a sector, it needs to add two lines to the conic
     * and the point needs to be in at least one of them, not in all.
     * This method joins all expression in one unique product.
     * @param originalList containing all expressions.
     * @return a new EquationList containing a product of all the expressions
     * in originalList.
     */
    protected EquationList orAllExpressions(final EquationList originalList) {

    	EquationList result = new EquationList(1);
    	
    	EquationExpression[] eqs = new EquationExpression[originalList.size()];

        for(int i = 0; i < originalList.size(); i++) {
        	eqs[i] = originalList.get(i).getExpression();
        }
        
        result.add(equation(times(eqs)));
        
        return result;
    }
}
