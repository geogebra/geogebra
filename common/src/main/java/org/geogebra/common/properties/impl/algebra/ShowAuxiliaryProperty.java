package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * Show auxiliary objects property.
 */
public class ShowAuxiliaryProperty extends AbstractProperty implements BooleanProperty {

    private App app;

    /**
     * Constructs a property for showing or hiding auxiliary objects.
     *
     * @param app          app
     * @param localization localization
     */
    public ShowAuxiliaryProperty(App app, Localization localization) {
        super(localization, "AuxiliaryObjects");
        this.app = app;
    }

    @Override
    public boolean getValue() {
        return app.showAuxiliaryObjects();
    }

    @Override
    public void setValue(boolean value) {
        app.setShowAuxiliaryObjects(value);
    }
}
