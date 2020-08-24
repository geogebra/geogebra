package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;

/**
 * Helper class for implementing the localized name of a property.
 */
public abstract class AbstractProperty implements Property {

    private Localization localization;
    private String name;

    /**
     * Constructs an abstract property.
     *
     * @param localization this is used to localize the name
     * @param name         the name to be localized
     */
    public AbstractProperty(Localization localization, String name) {
        this.localization = localization;
        this.name = name;
    }

    @Override
    public String getName() {
        return localization.getMenu(name);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the localization of the class.
     *
     * @return localization used
     */
    protected Localization getLocalization() {
        return localization;
    }
}
