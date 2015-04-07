/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
import org.geogebra.common.kernel.locusequ.arith.EquationNumericValue;
import org.geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;

/**
 * @author sergio
 * EquationSymbolicPoint is the superclass of both {@link EquationDependentPoint}
 * and {@link EquationSpecialSymbolicPoint}.
 * Both of them share the same implementation except for the identifier.
 */
public abstract class EquationSymbolicPoint extends EquationPoint {

    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        this.x = newPoint.getX();
        this.y = newPoint.getY();
        this.z = newPoint.getZ();
    }

    /**
     * Identifier for {@link EquationDependentPoint}
     */
    protected final String DEPENDENT_POINT_ID = "x";
    
    /**
     * Identifier for {@link EquationSpecialSymbolicPoint}
     */
    protected final String SPECIAL_SYMBOLIC_ID = "u";
    
    /**
     * Identifier for {@link EquationAuxiliarSymbolicPoint}
     */
    protected final String AUXILIAR_SYMBOLIC_ID = "a";
    
    /**
     * x coordinate.
     */
    protected EquationExpression x;
    
    /**
     * y coordinate.
     */
    protected EquationExpression y;
    
    
    /**
     * z coordinate (only because GeoPoint already has three coordinates).
     */
    protected EquationExpression z;
    
    protected EquationSymbolicPoint() { super(); } // Empty constructor for subclasses, in case they need it.
    
    /**
     * @param v Starting value for the coordinates.
     */
    public EquationSymbolicPoint(int v){
        super();
        this.x = new EquationSymbolicValue(v+0);
        this.y = new EquationSymbolicValue(v+1);
        this.z = new EquationSymbolicValue(v+2);
    }
    
    @Override
	public EquationExpression getXExpression() {
        return this.x;
    }

    @Override
	public EquationExpression getYExpression() {
        return this.y;
    }

    @Override
	public EquationExpression getZExpression() {
        return this.z;
    }
    
    /**
     * @return A string containing the general identifier for the specific type of point.
     */
    protected abstract String getId();

    @Override
    public void fixX(double value) {
        this.x = EquationNumericValue.from(value);
    }

    @Override
    public void fixY(double value) {
        this.y = EquationNumericValue.from(value);
    }

}
