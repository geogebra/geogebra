/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
