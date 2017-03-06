package org.geogebra.common.jre.cas.giac;

import org.geogebra.common.cas.giac.binding.Context;

import javagiac.gen;

public class Gen implements org.geogebra.common.cas.giac.binding.Gen {

    gen gen;

    Gen(gen gen) {
        this.gen = gen;
    }

    Gen(String string, Context context) {
        this(new gen(string, Util.getContext(context)));
    }

    @Override
    public org.geogebra.common.cas.giac.binding.Gen eval(int level, Context context) {
        return new Gen(gen.eval(level, Util.getContext(context)));
    }

    @Override
    public String print(Context context) {
        return gen.print(Util.getContext(context));
    }
}
