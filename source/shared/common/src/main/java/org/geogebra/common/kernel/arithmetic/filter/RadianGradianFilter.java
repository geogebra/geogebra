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

package org.geogebra.common.kernel.arithmetic.filter;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;

/**
 * An {@link ExpressionFilter} based on the use of radian or gradian in expressions.
 */
public class RadianGradianFilter implements ExpressionFilter {

    @Override
    public boolean isAllowed(@Nonnull ExpressionValue expression) {
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
