package org.geogebra.common.jre.cas.giac.binding;

import org.geogebra.common.cas.giac.binding.Context;

import javagiac.context;

final class Util {

    private Util() {
    }

    static context convert(Context context) {
        return ((ContextImpl) context).wrappedContext;
    }
}
