package org.geogebra.common.util;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Traverses an expression and returns true if it contains an undefined "?" (Double.NaN)
 * value or empty list.
 */
public class UndefinedOrEmptyChecker implements Inspecting {

    @Override
    public boolean check(ExpressionValue v) {
        // Return true for undefined "?"
        if (v instanceof MyDouble) {
            return !((MyDouble) v).isDefined();
        }

        // Return true for empty list
        if (v instanceof MyList
                && ((MyList) v).size() == 0) {
            return true;
        }

        // In case of a symbolic expression check its value
        if (v instanceof GeoSymbolic) {
            return (((GeoSymbolic) v).getValue()).inspect(this);
        }

        return false;

    }
}
