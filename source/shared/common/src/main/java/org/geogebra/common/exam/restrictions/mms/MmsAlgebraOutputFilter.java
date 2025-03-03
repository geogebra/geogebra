package org.geogebra.common.exam.restrictions.mms;

import javax.annotation.Nullable;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public final class MmsAlgebraOutputFilter implements AlgebraOutputFilter {

    private final @Nullable AlgebraOutputFilter wrappedFilter;

    public MmsAlgebraOutputFilter(@Nullable AlgebraOutputFilter wrappedFilter) {
        this.wrappedFilter = wrappedFilter;
    }

    @Override
    public boolean isAllowed(GeoElementND element) {
        if (element == null) {
            return false;
        }
        if (!Mms.isOutputAllowed(element)) {
            return false;
        }
        if (wrappedFilter != null) {
            return wrappedFilter.isAllowed(element);
        }
        return true;
    }
}
