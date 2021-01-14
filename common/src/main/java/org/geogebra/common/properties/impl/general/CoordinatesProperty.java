package org.geogebra.common.properties.impl.general;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the coordinates.
 */
public class CoordinatesProperty extends AbstractEnumerableProperty {

    @Weak
    private Kernel kernel;

    /**
     * Constructs a coordinates property.
     *
     * @param kernel       kernel
     * @param localization localization
     */
    public CoordinatesProperty(Kernel kernel, Localization localization) {
        super(localization, "Coordinates");
        this.kernel = kernel;
        setValuesAndLocalize(new String[]{
                "A = (x, y)",
                "A(x | y)",
                "A: (x, y)"
        });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        kernel.setCoordStyle(index);
		kernel.updateConstruction(false);
    }

    @Override
    public int getIndex() {
        return kernel.getCoordStyle();
    }
}
