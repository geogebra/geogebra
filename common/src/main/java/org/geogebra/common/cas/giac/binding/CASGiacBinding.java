package org.geogebra.common.cas.giac.binding;

public interface CASGiacBinding {

    Context createContext();

    Gen createGen(String string, Context context);
}
