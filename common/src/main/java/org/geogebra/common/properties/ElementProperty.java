package org.geogebra.common.properties;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

import java.util.ArrayList;

public abstract class ElementProperty<T> extends AbstractProperty {
    private final App mApp;
    private ArrayList<GeoElement> geos;

    /**
     * Constructs an abstract property.
     *
     * @param app  for localization & notifications
     * @param name the name to be localized
     */
    public ElementProperty(App app, String name) {
        super(app.getLocalization(), name);
        mApp = app;
    }

    /**
     * @param geoElementsList list of relevant elements
     */
    public void setGeos(ArrayList<GeoElement> geoElementsList) {
        geos = geoElementsList;
    }

    @Override
    public final boolean isEnabled() {
        if (isEmpty()) {
            return false;
        }
        for (GeoElementND geo : geos) {
            if (!isEnabled(geo)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty() {
        return geos == null || geos.isEmpty();
    }

    /**
     * @param value property value
     */
    public final void setValue(T value) {
        if (!isEmpty()) {
            for (GeoElementND geo : geos) {
                setValue(geo, value);
            }
            mApp.setPropertiesOccured();
        }
    }

    /**
     * @return property value
     */
    public T getValue() {
        if (isEmpty()) {
            return getDefaultValue();
        }
        return getValue(geos.get(0));
    }

    public abstract T getValue(GeoElementND geo);

    public abstract T getDefaultValue();

    public abstract void setValue(GeoElementND geo, T value);

    public abstract boolean isEnabled(GeoElementND geo);
}
