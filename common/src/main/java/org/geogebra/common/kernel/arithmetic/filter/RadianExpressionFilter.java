package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * An {@link ExpressionFilter} based on the use of radian in expressions.
 */
public class RadianExpressionFilter implements ExpressionFilter {

    @Override
    public boolean isAllowed(ValidExpression expression) {
        boolean containsRadian = expression
                .inspect(expressionValue -> {
                    if (expressionValue instanceof MySpecialDouble) {
                        MySpecialDouble doubleVal = (MySpecialDouble) expressionValue;
                        return doubleVal.isAngle()
                                && doubleVal.toString(StringTemplate.defaultTemplate)
                                        .equalsIgnoreCase("rad");
                    }
                    return false;
                });
        return !containsRadian;
    }
}
