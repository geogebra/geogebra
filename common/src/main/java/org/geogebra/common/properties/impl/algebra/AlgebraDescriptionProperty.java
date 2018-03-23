package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class AlgebraDescriptionProperty extends AbstractEnumerableProperty {

    private int[] algebraStyles = {
            Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE,
            Kernel.ALGEBRA_STYLE_VALUE,
            Kernel.ALGEBRA_STYLE_DEFINITION,
            Kernel.ALGEBRA_STYLE_DESCRIPTION
    };

    private Kernel kernel;

    public AlgebraDescriptionProperty(Kernel kernel, Localization localization) {
        super(localization, "AlgebraDescriptions");
        this.kernel = kernel;
        setValuesAndLocalize(new String[] {
                "DefinitionAndValue",
                "Value",
                "Definition",
                "Description"
        });
    }

    @Override
    public int getCurrent() {
        int algebraStyle = kernel.getAlgebraStyle();
        for (int i = 0; i < algebraStyles.length; i++) {
            if (algebraStyles[i] == algebraStyle) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void setValueSafe(String value, int index) {
        kernel.setAlgebraStyle(algebraStyles[index]);
        kernel.updateConstruction();
    }
}
