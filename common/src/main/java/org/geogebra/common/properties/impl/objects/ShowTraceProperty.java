package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Show trace
 */
public class ShowTraceProperty extends AbstractGeoElementProperty implements BooleanProperty {

    public ShowTraceProperty(GeoElement geoElement) {
        super("ShowTrace", geoElement);
    }

    @Override
    public boolean getValue() {
        return getElement().getTrace();
    }

    @Override
    public void setValue(boolean trace) {
        GeoElement element = getElement();
        if (element.isTraceable()) {
            ((Traceable) element).setTrace(trace);
        }
    }
}
