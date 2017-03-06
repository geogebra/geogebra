package org.geogebra.common.jre.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiacB;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.kernel.Kernel;

/**
 * Giac for Desktop and Android
 */
public abstract class CASgiacJre extends CASgiacB {

    public CASgiacJre(CASparser casParser) {
        super(casParser);
    }

    public CASgiacJre(CASparser casParser, Kernel kernel) {
        this(casParser);
    }

    @Override
    protected CASGiacBinding createBinding() {
        return new CASGiacBindingJre();
    }
}
