package org.geogebra.common.jre.cas.giac;

import javagiac.context;
import javagiac.gen;

class Util {

    private Util() {
    }

    static context getContext(org.geogebra.common.cas.giac.binding.Context context) {
        return ((Context) context).context;
    }

    static gen getGen(org.geogebra.common.cas.giac.binding.Gen gen) {
        return ((Gen) gen).gen;
    }
}
