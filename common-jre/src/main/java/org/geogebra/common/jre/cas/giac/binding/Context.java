package org.geogebra.common.jre.cas.giac.binding;

import javagiac.context;

class Context implements org.geogebra.common.cas.giac.binding.Context {

    context context;

    Context() {
        context = new context();
    }
}
