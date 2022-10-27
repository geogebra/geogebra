package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property setting for algebra description.
 */
public class AlgebraDescriptionProperty extends AbstractEnumerableProperty {

    private int[] algebraStyles = {
            Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE,
            Kernel.ALGEBRA_STYLE_VALUE,
            Kernel.ALGEBRA_STYLE_DEFINITION,
            Kernel.ALGEBRA_STYLE_DESCRIPTION
    };

    @Weak
    private Kernel kernel;
    private boolean isSpreadsheet;

    /**
     * Constructs an algebra description property.
     *
     * @param kernel       kernel
     * @param localization localization
     * @param isSpreadsheet wheter it is user for spreadsheet
     */
    public AlgebraDescriptionProperty(Kernel kernel, Localization localization,
            boolean isSpreadsheet) {
        super(localization, "AlgebraDescriptions");
        this.kernel = kernel;
        this.isSpreadsheet = isSpreadsheet;
        setValuesAndLocalize(new String[]{
                "DefinitionAndValue",
                "Value",
                "Definition",
                "Description"
        });
    }

    @Override
    public int getIndex() {
        int algebraStyle = isSpreadsheet ? kernel.getAlgebraStyleSpreadsheet()
                : kernel.getAlgebraStyle();
        for (int i = 0; i < algebraStyles.length; i++) {
            if (algebraStyles[i] == algebraStyle) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void setValueSafe(String value, int index) {
        if (isSpreadsheet) {
            kernel.setAlgebraStyleSpreadsheet(algebraStyles[index]);
        } else {
            kernel.setAlgebraStyle(algebraStyles[index]);
        }
		kernel.updateConstruction(false);
    }
}
