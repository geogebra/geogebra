package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.properties.EnumerableProperty;

/**
 * Equation form
 */
public class EquationFormProperty extends AbstractGeoElementProperty implements EnumerableProperty {

    private static final String[] equationFormNames = {
            "ImplicitLineEquation",
            "ExplicitLineEquation",
            "ParametricForm",
            "GeneralLineEquation",
            "InputForm"
    };

    public EquationFormProperty(GeoElement geoElement) {
        super("Equation", geoElement);
    }

    @Override
    public String[] getValues() {
        return equationFormNames;
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
