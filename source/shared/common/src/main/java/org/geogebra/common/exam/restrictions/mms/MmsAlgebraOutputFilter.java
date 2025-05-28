package org.geogebra.common.exam.restrictions.mms;

import javax.annotation.CheckForNull;

import org.geogebra.common.exam.restrictions.wtr.AlgebraConversionFilter;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public final class MmsAlgebraOutputFilter implements AlgebraOutputFilter {

    private final @CheckForNull AlgebraOutputFilter wrappedFilter;
    private final AlgebraConversionFilter algebraConversionFilter;

    /**
     * @param wrappedFilter parent filter
     */
    public MmsAlgebraOutputFilter(@CheckForNull AlgebraOutputFilter wrappedFilter) {
        this.wrappedFilter = wrappedFilter;
        this.algebraConversionFilter = new AlgebraConversionFilter();
    }

    @Override
    public boolean isAllowed(GeoElementND element) {
        if (element == null) {
            return false;
        }
        if (!Mms.isOutputAllowed(element)) {
            return false;
        }
        if (!algebraConversionFilter.isAllowed(element)) {
            return false;
        }
        if (wrappedFilter != null) {
            return wrappedFilter.isAllowed(element);
        }
        return true;
    }
}
