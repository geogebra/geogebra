/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties;

import javax.annotation.Nonnull;

/**
 * A settings property of the GeoGebra App.
 */
public interface Property extends PropertySupplier {
    /**
     * Returns the localized name of the property.
     *
     * @return the name of the property
     *
     * TODO rename to getLocalizedName()
     */
    String getName();

    /**
     * @return The raw, unlocalized name of the property.
     */
    @Nonnull String getRawName();

    /**
     * Returns whether the property is enabled. In some cases
     * properties can depend on other settings, so they may be disabled.
     * This generally corresponds to read-only state in the UI.
     *
     * @return true iff the property is enabled
     */
    boolean isEnabled();

    /**
     * Some properties need to be shown or hidden in the UI dynamically,
     * depending on other properties.
     * @return whether the property is available
     */
    boolean isAvailable();

    /**
     * @return whether the property is read-only
     */
    boolean isFrozen();

    /**
     * Mark as read-only.
     * @param frozen whether it's read-only
     */
    void setFrozen(boolean frozen);

    @Override
    default Property updateAndGet() {
        return this;
    }

    @Override
    default Property get() {
        return this;
    }
}
