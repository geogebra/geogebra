package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationFirstCoordinateValue;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;
import geogebra.common.kernel.locusequ.arith.EquationSecondCoordinateValue;
import geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;
import geogebra.common.kernel.locusequ.arith.EquationThirdCoordinateValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sergio
 * Represents a point.
 */
public abstract class EquationPoint {
	/**
	 * Contain {@link EquationExpression} for coordinate x.
	 */
	protected EquationExpression xExpr;
    
	/**
	 * Contain {@link EquationExpression} for coordinate y.
	 */
	protected EquationExpression yExpr;
	
	/**
	 * Contain {@link EquationExpression} for coordinate z.
	 */
	protected EquationExpression zExpr;
	
    /**
     * Creates a new Equation Point
     */
    public EquationPoint() {
        /*
         * This seems quite absurd, but since I use a JRuby REPL for debugging,
         * knowing what each expression represents is kind of useful.
         */
    	this.xExpr = new EquationFirstCoordinateValue(this);
        this.yExpr = new EquationSecondCoordinateValue(this);
        this.zExpr = new EquationThirdCoordinateValue(this);
    }
    
    /**
     * Represents the origin (0,0,0).
     */
    public final static EquationPoint ORIGIN = new EquationPoint(){

    	@Override
        public boolean isIndependent() {
            return true;
        }

        @Override
		public EquationExpression getXExpression() {
            return EquationNumericValue.ZERO;
        }

        @Override
		public EquationExpression getYExpression() {
            return EquationNumericValue.ZERO;
        }

        @Override
		public EquationExpression getZExpression() {
            return EquationNumericValue.ZERO;
        }

        @Override
        public GeoPoint getPoint() {
            return null;
        }

        @Override
        public void getIndexesFrom(EquationPoint newPoint) {
            // Do nothing, this is supposed to be for symbolic points
        }

        @Override
        public void fixX(double value) {
            // Do nothing.
        }

        @Override
        public void fixY(double value) {
            // Do nothing.
        }
    };
    
    /**
     * Creates a symbolic 2D point.
     * @param x coordinate
     * @param y coordinate
     * @return new point
     */
    public static EquationPoint fromCoordinates(final EquationExpression x, final EquationExpression y) {
        return fromCoordinates(x,y,EquationNumericValue.ZERO);
    }
    
    /**
     * Creates a symbolic 3D point.
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     * @return new point
     */
    public static EquationPoint fromCoordinates(final EquationExpression x,
                                                final EquationExpression y,
                                                final EquationExpression z) {
        return new EquationPoint(){

            @Override
            public boolean isIndependent() {
                return false;
            }

            @Override
            public EquationExpression getXExpression() {
                return x;
            }

            @Override
            public EquationExpression getYExpression() {
                return y;
            }

            @Override
            public EquationExpression getZExpression() {
                return z;
            }

            @Override
            public GeoPoint getPoint() {
                return null;
            }

            @Override
            public void getIndexesFrom(EquationPoint newPoint) {
                // Do nothing, this is supposed to be for symbolic points
            }

            @Override
            public void fixX(double value) {
                // Do nothing.
            }

            @Override
            public void fixY(double value) {
                // Do nothing.
            }
            
        };
    }
    
    /**
     * @return true iff it contains no symbolic values.
     */
    public boolean isIndependent() {
        return  this.getXExpression().isNumericValue() &&
                this.getYExpression().isNumericValue() /*&&
                this.getZ().isNumericValue()*/;
    }
    
    /**
     * Use this method only if really necessary. Otherwise, use getX().
     * @return A {@link EquationExpression} representing the x coordinate.
     */
    public abstract EquationExpression getXExpression();
    
    /**
     * @return x coordinate as an {@link EquationExpression}.
     */
    public EquationExpression getX() {
        return this.xExpr;
    }
    
    /**
     * Use this method only if really necessary. Otherwise, use getY().
     * @return A {@link EquationExpression} representing the y coordinate.
     */
    public abstract EquationExpression getYExpression();
    
    /**
     * @return y  coordinate as an {@link EquationExpression}.
     */
    public EquationExpression getY() {
        return this.yExpr;
    }
    
    /**
     * Use this method only if really necessary. Otherwise, use getZ().
     * @return A {@link EquationExpression} representing the z coordinate.
     */
    public abstract EquationExpression getZExpression();
    
    /**
     * @return z  coordinate as an {@link EquationExpression}.
     */
    public EquationExpression getZ() {
        return this.zExpr;
    }
    
    /**
     * @return The {@link GeoPoint} <b>this</b> represents.
     */
    public abstract GeoPoint getPoint();
    
    public boolean isFixable() {
        return true;
    }

    public abstract void getIndexesFrom(EquationPoint newPoint);
    
    public abstract void fixX(double value);
    
    public abstract void fixY(double value);
    
    /**
     * @return set of non-numeric variables of this point.
     */
    public Collection<? extends EquationSymbolicValue> getVariables() {
        Set<EquationSymbolicValue> set = new HashSet<EquationSymbolicValue>(2);
        
        if(this.getXExpression().isAnySymbolicValue()) {
            set.add((EquationSymbolicValue) this.getXExpression());
        }
        
        if(this.getYExpression().isAnySymbolicValue()) {
            set.add((EquationSymbolicValue) this.getYExpression());
        }
        
        return set;
    }
}
