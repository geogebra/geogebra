/**
 * 
 */
package geogebra.common.kernel.locusequ;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.diff;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sqr;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sqrt;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationArithHelper;
import geogebra.common.kernel.locusequ.arith.EquationExpression;

/**
 * @author sergio
 *
 */
public class SymbolicVector extends EquationPoint implements EquationAuxiliaryElement {

    /**
     * End point.
     */
    protected EquationPoint a;
    /**
     * Init point.
     */
    protected EquationPoint b;
    
    private EquationExpression module;
    
    private SymbolicVector normal;
    private SymbolicVector unitary;
    
    /**
     * Empty constructor for subclasses.
     */
    protected SymbolicVector() {}

    /**
     * General constructor.
     * @param a end point.
     * @param b init point.
     */
    public SymbolicVector(EquationPoint a, EquationPoint b) {
        this.a = a; //  Just in case
        this.b = b; //  Just in case
        this.xExpr = diff(this.a.getXExpression(), this.b.getXExpression());
        this.yExpr = diff(this.a.getYExpression(), this.b.getYExpression());
        this.zExpr = diff(this.a.getZExpression(), this.b.getZExpression());
    }
    
    /**
     * Returns the point resulting from applying this vector to p.
     * @param p initial point.
     * @return a new point.
     */
    public EquationPoint applyToPoint(final EquationPoint p) {
        return new EquationPointVectorPoint(this, p);
    }
    
    /**
     * Alias for getNormal.
     * @return normal vector.
     */
    public SymbolicVector normal() { return this.getNormal(); }
    
    /**
     * @return normal vector.
     */
    public SymbolicVector getNormal() {
        if(this.normal == null) {
            this.normal = new SymbolicVector();
            this.normal.xExpr = this.yExpr.getOpposite();
            this.normal.yExpr = this.xExpr;
            this.normal.a = new EquationNormalPoint(this.a);
            this.normal.b = new EquationNormalPoint(this.b);
            this.normal.normal = this;
        }
        return this.normal;
    }
    
    /**
     * @return true iff first coordinate is zero.
     */
    public boolean isFirstCoordinateZero(){
        return !this.getX().containsSymbolicValues() && this.getX().computeValue() == 0.0; 
    }
    
    /**
     * @return true iff second coordinate is zero.
     */
    public boolean isSecondCoordinateZero(){
        return !this.getY().containsSymbolicValues() && this.getY().computeValue() == 0.0;
    }
    
    /**
     * @return true iff third coordinate is zero.
     */
    public boolean isThirdCoordinateZero(){
        return !this.getZ().containsSymbolicValues() && this.getZ().computeValue() == 0.0;
    }
    
    /**
     * @return true iff no coordinate contain symbolic values.
     */
    public boolean isIndependent(){
        return !(this.getX().containsSymbolicValues() ||
                 this.getY().containsSymbolicValues() ||
                 this.getZ().containsSymbolicValues());
    }

    @Override
    public EquationExpression getXExpression() {
        return this.xExpr;
    }

    @Override
    public EquationExpression getYExpression() {
        return this.yExpr;
    }

    @Override
    public EquationExpression getZExpression() {
        return this.zExpr;
    }

    @Override
    public GeoPoint getPoint() {
        return this.a.getPoint();
    }
    
    /**
     * @return module of this vector.
     */
    public EquationExpression getModule() {
        if(this.module == null) {
            this.module = sqrt( EquationArithHelper.sum(sqr(diff(this.a.getX(), this.b.getX())),
                                                        sqr(diff(this.a.getY(), this.b.getY()))));
        }
        
        return this.module;
    }
    
    /**
     * Alias for getUnitary.
     * @return a unitary version of this vector.
     */
    public SymbolicVector unitary() {
    	return this.getUnitary();
    }
    
    /**
     * @return a unitary version of this vector.
     */
    public SymbolicVector getUnitary() {
        if(this.unitary == null) {
            this.unitary = new SymbolicVector(new EquationUnitaryPoint(this.a, this.getModule()),
                                              new EquationUnitaryPoint(this.b, this.getModule()));
            this.unitary.unitary = this.unitary;
            this.unitary.normal = this.getNormal().getUnitary();
            this.getNormal().getUnitary().normal = this.unitary;
        }
        
        return this.unitary;
    }

    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        // Do nothing
    }

    @Override
    public void fixX(double value) {
        // Do nothing
    }

    @Override
    public void fixY(double value) {
        // Do nothing
    }

    /**
     * @param otherVec other SymbolicVector
     * @return bisector of this vector and otherVector.
     */
    public SymbolicVector getBisector(final SymbolicVector otherVec) {
        return this.getUnitary().sum(otherVec.getUnitary());
    }

    /**
     * @param otherVec other SymbolicVector
     * @return sum of two vectors.
     */
    private SymbolicVector sum(SymbolicVector otherVec) {
        return new SumVector(this, otherVec);
    }
}
