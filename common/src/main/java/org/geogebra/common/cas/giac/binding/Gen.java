package org.geogebra.common.cas.giac.binding;

public interface Gen {

    Gen eval(int level, Context context);

    String print(Context context);
}
