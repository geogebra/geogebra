package org.geogebra.common.jre.cas.giac.binding;

import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;

import javagiac.gen;

class GenImpl implements Gen {

    private gen gen;

    GenImpl(String string, Context context) {
        gen = new gen(string, Util.convert(context));
    }

    private GenImpl(gen g) {
        gen = g;
    }

    @Override
    public Gen eval(int level, Context context) {
		gen g = gen.eval(level, Util.convert(context));
		return new GenImpl(g);
    }

    @Override
    public String print(Context context) {
        return gen.print(Util.convert(context));
    }
}
