package org.geogebra.common.properties.impl.algebra;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraStyle;
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
                entry(AlgebraStyle.DEFINITION_AND_VALUE, "DefinitionAndValue"),
                entry(AlgebraStyle.VALUE, "Value"),
                entry(AlgebraStyle.DEFINITION, "Definition"),
                entry(AlgebraStyle.DESCRIPTION, "Description")
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

    /**
     * Switch the target view between AV and spreadsheet.
     * @param isSpreadsheet whether this is for (classic) spreadsheet
     */
    public void usesSpreadsheet(boolean isSpreadsheet) {
        this.isSpreadsheet = isSpreadsheet;
    }
}
