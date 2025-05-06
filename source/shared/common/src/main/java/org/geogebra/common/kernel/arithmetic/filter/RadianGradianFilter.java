package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * An {@link ExpressionFilter} based on the use of radian or gradian in expressions.
 */
public class RadianGradianFilter implements ExpressionFilter {

    @Override
    public boolean isAllowed(ValidExpression expression) {
        boolean containsDegree = false;
        for (ExpressionValue expressionValue: expression) {
            if (expressionValue instanceof MySpecialDouble) {
                MySpecialDouble doubleVal = (MySpecialDouble) expressionValue;
                String valString = doubleVal.toString(StringTemplate.defaultTemplate);
                if (isForbidden(valString)) {
                    return false;
                }
            }
        }
        return !containsDegree;
    }

    private boolean isForbidden(String valString) {
        switch (valString) {
        case "\u1d4d": // gradian sign
        case "rad":
            return true;
        default:
            return false;
        }
    }
}
