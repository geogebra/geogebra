package org.geogebra.common.properties.impl.general;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.LabelSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

/**
 * Property for setting the labeling for new objects.
 */
public class LabelingProperty extends AbstractEnumerableProperty {

    private int[] labelVisibility = {
            ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON,
            ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF,
            ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY
    };

    private LabelSettings labelSettings;

    /**
     * @param localization localization
     * @param labelSettings labelSettings
     */
    public LabelingProperty(Localization localization, LabelSettings labelSettings) {
        super(localization, "Labeling");
        this.labelSettings = labelSettings;
        setValuesAndLocalize(new String[]{
                "Labeling.on",
                "Labeling.off",
                "Labeling.pointsOnly"
        });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        labelSettings.setLabelVisibility(LabelVisibility.get(labelVisibility[index]));
    }

    @Override
    public int getIndex() {
        int labelingStyle = labelSettings.getLabelVisibility().getValue();
        for (int i = 0; i < labelVisibility.length; i++) {
            if (labelingStyle == labelVisibility[i]) {
                return i;
            }
        }

        return -1;
    }
}
