package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.BooleanProperty;

public class ShowAuxiliaryProperty extends AbstractProperty implements BooleanProperty {

    private App app;

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
