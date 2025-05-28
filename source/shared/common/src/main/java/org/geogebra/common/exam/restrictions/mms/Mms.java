package org.geogebra.common.exam.restrictions.mms;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;

final class Mms {

    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    static boolean isOutputAllowed(@CheckForNull GeoElementND element) {
        if (element == null) {
            return false;
        }
        GeoElementND unwrapped = element.unwrapSymbolic();
        if (unwrapped instanceof BarChartGeoNumeric) {
            return false;
        }
        if (unwrapped instanceof FunctionalNVar) {
            return false;
        }
        return true;
    }
}
