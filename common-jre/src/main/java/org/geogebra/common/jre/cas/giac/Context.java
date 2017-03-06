package org.geogebra.common.jre.cas.giac;

import javagiac.context;

class Context implements org.geogebra.common.cas.giac.binding.Context {

    context context;

    Context() {
        context = new context();
    }
}
