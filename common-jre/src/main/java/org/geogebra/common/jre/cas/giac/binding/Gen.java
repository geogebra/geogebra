package org.geogebra.common.jre.cas.giac.binding;

import org.geogebra.common.cas.giac.binding.Context;

import javagiac.gen;

class Gen implements org.geogebra.common.cas.giac.binding.Gen {

    gen gen;

    Gen(gen g) {
        gen = g;
    }

    Gen(String string, Context context) {
        gen = new gen(string, Util.convert(context));
    }

    @Override
    public org.geogebra.common.cas.giac.binding.Gen eval(int level, Context context) {
        javagiac.gen g = gen.eval(level, Util.convert(context));
        return new Gen(g);
    }

    @Override
    public String print(Context context) {
        return gen.print(Util.convert(context));
    }
}
