package org.geogebra.common.properties.impl.algebra;

import static java.util.Map.entry;

import java.util.List;

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
        setNamedValues(List.of(
                entry(Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE, "DefinitionAndValue"),
                entry(Kernel.ALGEBRA_STYLE_VALUE, "Value"),
                entry(Kernel.ALGEBRA_STYLE_DEFINITION, "Definition"),
                entry(Kernel.ALGEBRA_STYLE_DESCRIPTION, "Description")
        ));
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
