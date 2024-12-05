package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.algos.ConstructionElement;

/**
 * Contract for setting up or configuring a {@link ConstructionElement}.
 */
public interface ConstructionElementSetup {
    /**
     * Sets up or configures the given {@link ConstructionElement}.
     * @param constructionElement The {@link ConstructionElement} to be configured
     */
    void applyTo(ConstructionElement constructionElement);
}
