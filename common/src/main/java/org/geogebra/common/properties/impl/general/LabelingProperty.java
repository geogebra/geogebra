package org.geogebra.common.properties.impl.general;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class LabelingProperty extends AbstractEnumerableProperty {

    private int[] labelingStyles = {
            ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON,
            ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF,
            ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY
    };

    private App app;

    public LabelingProperty(App app, Localization localization) {
        super(localization, "Labeling");
        this.app = app;
        setValuesAndLocalize(new String[] {
                "Labeling.on",
                "Labeling.off",
                "Labeling.pointsOnly"
        });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        app.setLabelingStyle(labelingStyles[index]);
    }

    @Override
    public int getCurrent() {
        int labelingStyle = app.getLabelingStyle();
        for (int i = 0; i < labelingStyles.length; i++) {
            if (labelingStyle == labelingStyles[i]) {
                return i;
            }
        }

        return NONE;
    }
}
