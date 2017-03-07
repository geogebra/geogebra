package org.geogebra.common.jre.cas.giac.binding;

import javagiac.context;

class Util {

    private Util() {
    }

    static context convert(org.geogebra.common.cas.giac.binding.Context context) {
        return ((Context) context).context;
    }
}
