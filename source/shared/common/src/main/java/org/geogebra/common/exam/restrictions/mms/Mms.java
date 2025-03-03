package org.geogebra.common.exam.restrictions.mms;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

final class Mms {

    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    static boolean isOutputAllowed(@Nullable GeoElementND element) {
        if (element == null) {
            return false;
        }
        if (element instanceof BarChartGeoNumeric
                || (element instanceof GeoSymbolic
                && ((GeoSymbolic) element).getTwinGeo() instanceof BarChartGeoNumeric)) {
            return false;
        }
        return true;
    }
}
