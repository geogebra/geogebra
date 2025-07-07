package org.geogebra.common.properties.impl.algebra;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property setting for algebra description.
 */
public class AlgebraDescriptionProperty extends AbstractNamedEnumeratedProperty<Integer> {

    @Weak
    private final Kernel kernel;
    private boolean isSpreadsheet;

    /**
     * Constructs an algebra description property.
     * @param app App
     * @param localization localization
     */
    public AlgebraDescriptionProperty(App app, Localization localization) {
        super(localization, "AlgebraDescriptions");
        this.kernel = app.getKernel();
        List<Map.Entry<Integer, String>> algebraStyles = AlgebraStyle.getAvailableValues(app)
                .stream()
                .map(style -> Map.entry(style.getNumericValue(), style.getTranslationKey()))
                .collect(Collectors.toList());
        setNamedValues(algebraStyles);
    }

    @Override
    public Integer getValue() {
        return isSpreadsheet ? kernel.getAlgebraStyleSpreadsheet().getNumericValue()
                : kernel.getApplication().getAlgebraStyle().getNumericValue();
    }

    @Override
    protected void doSetValue(Integer value) {
        if (isSpreadsheet) {
            kernel.setAlgebraStyleSpreadsheet(AlgebraStyle.fromNumericValue(value));
        } else {
            kernel.getApplication().getSettings().getAlgebra().setStyle(
                    AlgebraStyle.fromNumericValue(value));
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
