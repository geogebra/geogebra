package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property setting for algebra description.
 */
public class AlgebraDescriptionProperty extends AbstractNamedEnumeratedProperty<Integer> {

    @Weak
    private Kernel kernel;
    private boolean isSpreadsheet;

    /**
     * Constructs an algebra description property.
     *
     * @param kernel       kernel
     * @param localization localization
     */
    public AlgebraDescriptionProperty(Kernel kernel, Localization localization) {
        super(localization, "AlgebraDescriptions");
        this.kernel = kernel;
        setValueNames("DefinitionAndValue", "Value", "Definition", "Description");
        setValues(Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE, Kernel.ALGEBRA_STYLE_VALUE,
                Kernel.ALGEBRA_STYLE_DEFINITION, Kernel.ALGEBRA_STYLE_DESCRIPTION);
    }

    @Override
    public Integer getValue() {
        return isSpreadsheet ? kernel.getAlgebraStyleSpreadsheet() : kernel.getAlgebraStyle();
    }

    @Override
    protected void doSetValue(Integer value) {
        if (isSpreadsheet) {
            kernel.setAlgebraStyleSpreadsheet(value);
        } else {
            kernel.setAlgebraStyle(value);
        }
        kernel.updateConstruction();
    }

    public void usesSpreadsheet(boolean isSpreadsheet) {
        this.isSpreadsheet = isSpreadsheet;
    }
}
