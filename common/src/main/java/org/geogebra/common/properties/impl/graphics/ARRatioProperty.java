package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.RatioProperty;

public class ARRatioProperty extends AbstractProperty
        implements RatioProperty {

    private Renderer renderer;
    private Kernel kernel;

    /**
     * Constructs an AR Ratio property.
     *
     * @param localization      localization for the title
     * @param renderer            euclidianView3D
     * @param kernel            kernel
     */
    public ARRatioProperty(Localization localization, Renderer renderer, Kernel kernel) {
        super(localization, "Ratio");
        this.renderer = renderer;
        this.kernel = kernel;
    }

    @Override
    public String getValue() {
        return renderer.getARRatio();
    }

    @Override
    public void setValue(String value) {
        GeoNumberValue ratio = !value.trim().equals("") ? getNumberValue(value) : null;
        if (ratio != null && !Double.isNaN(ratio.getDouble())) {
            renderer.setARRatio(ratio.getDouble());
        }
    }

    @Override
    public boolean isValid(String value) {
        GeoNumberValue number = getNumberValue(value);
        return number != null && !Double.isNaN(number.getDouble());
    }

    private GeoNumberValue getNumberValue(String value) {
        return kernel.getAlgebraProcessor()
                .evaluateToNumeric(value.trim(), ErrorHelper.silent());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUnits() {
        return renderer.getARRatioUnits();
    }
}
