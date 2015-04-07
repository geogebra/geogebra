package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

public abstract class EquationExpression {
	protected EquationExpression opposite = null;
    protected EquationExpression inverse = null;
    private Boolean containsSymbolicValues = null;
    
    /**
     * Returns an expression for the opposite of current expression.
     * @return an {@link EquationExpression} for the opposite of <code>this</code>.
     */
    public EquationExpression getOpposite() {
        if(this.opposite == null) {
           this.opposite = new EquationOppositeOperator(this);
        }
        return this.opposite;
    }
    
    /**
     * Returns an expression for the inverse of current expression.
     * @return an {@link EquationExpression} for the inverse of <code>this</code>.
     */
    public EquationExpression getInverse() {
        if(this.inverse == null) {
           this.inverse = new EquationInverseOperator(this);
        }
        return this.inverse;
    }
    
    /**
     * Is it a Term?
     * @return <code>true</code> iff <code>this</code> is a Term.
     */
    public boolean isTerm(){
        return false;
    }

    /**
     * Is it a numeric value?
     * @return <code>true</code> iff <code>this</code> is a numeric value.
     */
    public boolean isNumericValue() {
        return false;
    }

    /**
     * Is it an auxiliar symbolic value?
     * @return <code>true</code> iff <code>this</code> is an auxiliar symbolic value?
     */
    public boolean isAuxiliarSymbolicValue() {
        return false;
    }
    
    /**
     * Is it a symbolic value?
     * @return <code>true</code> iff <code>this</code> is a symbolic value.
     */
    public boolean isSymbolicValue() {
        return false;
    }

    /**
     * Is it a special symbolic value?
     * @return <code>true</code> iff <code>this</code> is a special symbolic value.
     */
    public boolean isSpecialSymbolicValue() {
        return false;
    }
    
    /**
     * Is it any kind of symbolic value?
     * @return <code>true</code> iff <code>this</code> is a any kind of symbolic value.
     */
    public boolean isAnySymbolicValue() {
        return  this.isAuxiliarSymbolicValue() ||
                this.isSymbolicValue() ||
                this.isSpecialSymbolicValue();
    }
    
    /**
     * Is this an operator?
     * @return <code>true</code> iff <code>this</code> is an operator.
     */
    public boolean isOperator(){
        return false;
    }

    /**
     * Is this a sum operator?
     * @return <code>true</code> iff <code>this</code> is a sum operator.
     */
    public boolean isSum() {
        return false;
    }

    /**
     * Is this a substraction operator?
     * @return <code>true</code> iff <code>this</code> is a substraction operator.
     */
    public boolean isDiff() {
        return false;
    }

    /**
     * Is this a product operator?
     * @return <code>true</code> iff <code>this</code> is a product operator.
     */
    public boolean isProduct() {
        return false;
    }

    /**
     * Is this a div operator.
     * @return <code>true</code> iff <code>this</code> is a div operator.
     */
    public boolean isDiv() {
        return false;
    }

    /**
     * Is this an exponential operator?
     * @return <code>true</code> iff <code>this</code> is an exponential operator.
     */
    public boolean isExp() {
        return false;
    }
    
    /**
     * Is this a square root operator?
     * @return <code>true</code> iff <code>this</code> is a square root operator.
     */
    public boolean isSqrt() {
        return false;
    }
    
    /**
     * Is this opposite operator?
     * @return <code>true</code> iff <code>this</code> is this is an opposite operator.
     */
    public boolean isOpposite() {
        return false;
    }
    
    /**
     * Is this an inverse operator?
     * @return <code>true</code> iff <code>this</code> is this an inverse operator.
     */
    public boolean isInverse() {
        return false;
    }
    
    /**
     * Is this an absolute value operator?
     * @return <code>true</code> iff <code>this</code> is an absolute value operator.
     */
    public boolean isAbs() {
        return false;
    }
    
    /**
     * Check if this expression contains any kind of symbolic values.
     * @return <code>true</code> iff <code>this</code> contains any kind of symbolic value.
     */
    public boolean containsSymbolicValues() {
    	if(this.containsSymbolicValues == null) {
    		this.containsSymbolicValues = this.containsSymbolicValuesImpl();
    	}
    	
    	return this.containsSymbolicValues;
    }
    
    protected abstract boolean containsSymbolicValuesImpl();
    
    /**
     * Computes the value of current expression. It does so iff containsSymbolicValues
     * returns true.
     * @return the value of computing current expression. If it contains symbolic values
     * it returns NaN.
     */
    public double computeValue() {
        if(this.containsSymbolicValuesImpl()) {
            return Double.NaN;
        }
		return this.computeValueImpl();
    }
    
    /**
     * An abstract method with the actual implementation of
     * computeValue.
     * @return the result of computing current expression.
     */
    protected abstract double computeValueImpl();
    
    protected abstract <T> T translateImpl(EquationTranslator<T> translator);
    
    public <T> T translate(EquationTranslator<T> translator) {
        // TODO: Implement memoize.
//        if(translator.containsKey(this)) {
//            return translator.get(this);
//        }
        
        return translateImpl(translator);
    }

    public abstract long toLong();
    
    public boolean isSimplifiable() {
    	return !this.containsSymbolicValues() && !Double.isNaN(this.computeValue()); 
    }
    
    @Override
    public abstract String toString();
}