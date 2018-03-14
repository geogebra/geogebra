package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public abstract class AbstractProperty implements Property {

    private Localization localization;
    private String name;

    public AbstractProperty(Localization localization, String name) {
        this.localization = localization;
        this.name = name;
    }

    @Override
    public String getName() {
        return localization.getMenu(name);
    }
}
