package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Contract for setting up or configuring a {@link GeoElementND}.
 */
public interface GeoElementSetup {
    /**
     * Sets up or configures the given {@link GeoElementND}.
     * @param geoElementND The {@link GeoElementND} to be configured
     */
    void applyTo(GeoElementND geoElementND);
}
