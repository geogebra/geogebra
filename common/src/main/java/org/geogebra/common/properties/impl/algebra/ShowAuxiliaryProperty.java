package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractBooleanProperty;

public class ShowAuxiliaryProperty extends AbstractBooleanProperty {

    private App app;

    public ShowAuxiliaryProperty(App app, Localization localization) {
        super(localization, "AuxiliaryObjects");
        this.app = app;
    }

    @Override
    public boolean getValue() {
        return app.showAuxiliaryObjects();
    }

    public void setValueSafe(boolean value) {
        app.setShowAuxiliaryObjects(value);
    }
}
