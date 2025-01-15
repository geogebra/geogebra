package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Shared logic for CvTE exam restrictions.
 */
final class Cvte {

    /**
     * APPS-5926: "For Lines, Rays, Conics, Implicit Equations and Functions created with a
     * command or tool, we do not show the calculated equation."
     * <p/>
     * <b>Note:</b> The calculated equation will also be suppressed for all <i>dependent</i>
     * lines/conics/functions/curves.
     * @param element a {@link GeoElementND}
     * @return true if element matches the condition above.
     */
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    static boolean isCalculatedEquationAllowed(@Nullable GeoElementND element) {
        if (element == null) {
            return false;
        }
        // is Line, Ray, Conic, Implicit Equation or Function, ...
        if ((element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic()
                || element.isGeoFunction()
                || isImplicitEquation(element))
                // ...created with a command or tool;
                && (element.getParentAlgorithm() != null)) {
            return false;
        }
        return true;
    }

    private static boolean isImplicitEquation(@Nonnull GeoElementND geoElement) {
        if (geoElement instanceof EquationValue) {
            EquationValue equationValue = (EquationValue) geoElement;
            return equationValue.getEquation().isImplicit();
        }
        ExpressionNode definition = geoElement.getDefinition();
        if (definition != null && definition.unwrap() instanceof Equation) {
            Equation equation = (Equation) definition.unwrap();
            return equation.isImplicit();
        }
        return false;
    }
}
