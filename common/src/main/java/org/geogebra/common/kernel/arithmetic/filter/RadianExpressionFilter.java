package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

public class RadianExpressionFilter implements ExpressionFilter {

    @Override
    public boolean isAllowed(ValidExpression expression) {
        boolean containsRadian = expression
                .inspect(expressionValue -> {
                    if (expressionValue instanceof MySpecialDouble) {
                        return (((MySpecialDouble) expressionValue).isAngle() &&
                                ((MySpecialDouble) expressionValue).toString(StringTemplate.defaultTemplate) == "rad");
                    }
                    return false;
                });
        return !containsRadian;
    }
}
