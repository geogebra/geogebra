package org.geogebra.common.jre.cas.giac;

import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;

public class CASGiacBindingJre implements CASGiacBinding {

    @Override
    public Context createContext() {
        return new org.geogebra.common.jre.cas.giac.Context();
    }

    @Override
    public Gen createGen(String string, Context context) {
        return new org.geogebra.common.jre.cas.giac.Gen(string, context);
    }
}
