package org.geogebra.common.jre.cas.giac.binding;

import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;

import javagiac.gen;

class GenImpl implements Gen {

    private final gen wrappedGen;

    GenImpl(String string, Context context) {
        wrappedGen = new gen(string, Util.convert(context));
    }

    private GenImpl(gen g) {
        wrappedGen = g;
    }

    @Override
    public Gen eval(int level, Context context) {
		gen g = wrappedGen.eval(level, Util.convert(context));
		return new GenImpl(g);
    }

    @Override
    public String print(Context context) {
        return wrappedGen.print(Util.convert(context));
    }
}
