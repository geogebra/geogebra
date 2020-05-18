package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.properties.EnumerableProperty;

/**
 * Equation form
 */
public class EquationForm extends AbstractGeoElementProperty implements EnumerableProperty {

    public EquationForm(GeoElement geoElement) {
        super("Equation", geoElement);
    }

    @Override
    public String[] getValues() {
        return new String[0];
    }

    @Override
    public int getIndex() {
        return getElement().getToStringMode();
    }

    @Override
    public void setIndex(int equationForm) {
        if (getElement() instanceof GeoVec3D) {
            GeoVec3D vec3d = (GeoVec3D) getElement();
            vec3d.setMode(equationForm);
            vec3d.updateRepaint();
        }
    }
}
