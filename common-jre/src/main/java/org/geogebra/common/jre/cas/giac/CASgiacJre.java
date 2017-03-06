package org.geogebra.common.jre.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiacB;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;

/**
 * Giac for Desktop and Android
 */
public abstract class CASgiacJre extends CASgiacB {

    public CASgiacJre(CASparser casParser) {
        super(casParser);
    }

    @Override
    protected CASGiacBinding createBinding() {
        return new CASGiacBindingJre();
    }
}
