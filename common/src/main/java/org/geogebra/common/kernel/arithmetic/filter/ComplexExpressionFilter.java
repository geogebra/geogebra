package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;

/**
 * An {@link ExpressionFilter} based on complex values in expressions.
 */
public class ComplexExpressionFilter implements ExpressionFilter {

    @Override
    public boolean isAllowed(ValidExpression expression) {
        boolean containsComplexValues = expression
                .inspect(expressionValue -> expressionValue.getValueType() == ValueType.COMPLEX);
        return !containsComplexValues;
    }
}
