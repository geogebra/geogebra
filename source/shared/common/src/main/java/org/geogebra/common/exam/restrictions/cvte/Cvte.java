package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.CheckForNull;

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
    static boolean isCalculatedEquationAllowed(@CheckForNull GeoElementND element) {
        if (element == null) {
            return false;
        }
        // is Line, Ray, Conic, Implicit Equation or Function, ...
        if ((element.isGeoLine()
                || element.isGeoRay()
                || element.isGeoConic()
                || element.isGeoFunction()
                || element.isImplicitEquation())
                // ...created with a command or tool;
                && (element.getParentAlgorithm() != null)) {
            return false;
        }
        return true;
    }
}
