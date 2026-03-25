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

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

/**
 * Algebra output filter for APPS-5926 (don't try to adapt to other use cases!).
 *
 * @implNote The idea with this (decorator) was to not having to touch/change the existing
 * code around {@link App#getAlgebraOutputFilter()}. When the CvTE exam is active, new behaviour
 * is added on top of what was there before (by wrapping it), using the same structure as before,
 * without having to touch the existing
 * {@link org.geogebra.common.gui.view.algebra.filter.ProtectiveAlgebraOutputFilter ProtectiveAlgebraOutputFilter} /
 * {@link org.geogebra.common.gui.view.algebra.filter.DefaultAlgebraOutputFilter DefaultAlgebraOutputFilter} code.
 */
public final class CvteAlgebraOutputFilter implements AlgebraOutputFilter {

    /**
     * "For Lines, Rays, Conics, Implicit Equations and Functions created with a command or tool,
     * we do not show the calculated equation."
     */
    @Override
    public boolean isAllowed(GeoElementND element) {
        return isCalculatedEquationAllowed(element);
    }

    /**
     * APPS-5926: "For Lines, Rays, Conics, Implicit Equations and Functions created with a
     * command or tool, we do not show the calculated equation."
     * <p/>
     * <b>Note:</b> The calculated equation will also be suppressed for all <i>dependent</i>
     * lines/conics/functions/curves.
     * @param element a {@link GeoElementND}
     * @return true if element matches the condition above.
     */
    private boolean isCalculatedEquationAllowed(@CheckForNull GeoElementND element) {
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
