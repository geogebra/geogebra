package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public abstract class AbstractProperty implements Property {

    private Localization localization;
    private String name;
    private String type;

    public AbstractProperty(Localization localization, String name, String type) {
        this.localization = localization;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return localization.getMenu(name);
    }

    @Override
    public String getType() {
        return type;
    }
}
