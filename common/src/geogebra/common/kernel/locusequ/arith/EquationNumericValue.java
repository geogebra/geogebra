package geogebra.common.kernel.locusequ.arith;

import geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents a Numeric Value.
 */
public class EquationNumericValue extends EquationValue {
	private double value;

    public static final EquationNumericValue ZERO = new EquationNumericValue(0);
    public static final EquationNumericValue ONE  = new EquationNumericValue(1);
    public static final EquationNumericValue TWO  = new EquationNumericValue(2);
    
    /**
     * There is no constructor for this object and this method should be used instead.
     * @param value the double value it represents.
     * @return An EquationNumericValue object.
     */
    public static EquationNumericValue from(double value) {
        if(value == 0) {
            return ZERO;
        } else if (value == 1) {
            return ONE;
        } else if (value == 2) {
            return TWO;
        } else {
            return new EquationNumericValue(value);
        }
    }
    
    /**
     * Initializes current object with a value.
     * @param value represented in this numeric value
     */
    protected EquationNumericValue(final double value) {
        super();
        this.value = value;
    }
    
    /**
     * 
     * @return The numeric value as double.
     */
    public double getValue() { return this.value; }
    
    @Override
    public boolean isNumericValue() {
        return true;
    }

    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return translator.number(this.getValue());
    }

    @Override
    public long toLong() {
        return (long) this.getValue();
    }

    @Override
    public String toString() {
    	return Double.toString(this.getValue());
    }

    @Override
	protected boolean containsSymbolicValuesImpl() {
        return false;
    }

    @Override
    protected double computeValueImpl() {
        return this.getValue();
    }
    
    @Override
    public EquationExpression getOpposite() {
    	return EquationNumericValue.from(-this.getValue());
    }
    
    @Override
    public EquationExpression getInverse() {
    	return EquationNumericValue.from(1/this.getValue());
    }

}
