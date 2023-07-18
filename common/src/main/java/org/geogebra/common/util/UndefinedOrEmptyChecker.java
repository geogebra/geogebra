package org.geogebra.common.util;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.plugin.Operation;

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

        // In case of a symbolic expression check its value
        if (v instanceof GeoSymbolic) {
            return check(((GeoSymbolic) v).getValue());
        }

        // For lists return true if empty, else check all elements
        if (v instanceof MyList) {
            if (((MyList) v).getLength() == 0) {
                return true;
            }
            for (int i = 0; i < ((MyList) v).getLength(); i++) {
                if (check(((MyList) v).getItem(i))) {
                    return true;
                }
            }
            return false;
        }

        // Check both sides of equations
        if (v instanceof Equation) {
            return check(((Equation) v.unwrap()).getLHS())
                    || check(((Equation) v.unwrap()).getRHS());
        }

        // Check both sides of expression nodes, avoid NPE
        if (v instanceof ExpressionNode) {
            ExpressionNode node = (ExpressionNode) v;
            boolean l = false;
            boolean r = false;
            if (node.getLeft() != null) {
                l = check(node.getLeft());
            }

            if (node.getRight() != null) {
                r = check(node.getRight());
            }
            return (r || l) && !hasOperationWithNan((ExpressionNode) v);
        }
        return false;

    }

    // Some operations contain "?" by default, check added to avoid false positives
    private boolean hasOperationWithNan(ExpressionNode node) {
        return Operation.LOG.equals(node.getOperation())
                || Operation.SQRT.equals(node.getOperation())
                || Operation.CBRT.equals(node.getOperation())
                || Operation.SIN.equals(node.getOperation())
                || Operation.COS.equals(node.getOperation());
    }
}
