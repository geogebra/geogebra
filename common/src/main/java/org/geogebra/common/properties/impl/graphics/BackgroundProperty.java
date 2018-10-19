package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class BackgroundProperty extends AbstractEnumerableProperty {

    private App app;

    private int[] backgroundStyles = new int[]{
            ConstructionDefaults.BACKGROUND_VISIBLE_NONE,
            ConstructionDefaults.BACKGROUND_VISIBLE_TRANSPARENT,
            ConstructionDefaults.BACKGROUND_VISIBLE_OPAQUE
    };

    /**
     * Constructs an AbstractEnumerableProperty
     *
     * @param app          app
     * @param localization the localization used
     */
    public BackgroundProperty(App app, Localization localization) {
        super(localization, "Background");
        this.app = app;

        setValuesAndLocalize(new String[]{
                "None",
                "Transparent",
                "Opaque"
        });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        // after settings
        EuclidianView3D euclidianView3D = (EuclidianView3D) app.getActiveEuclidianView();
        if (euclidianView3D.isAREnabled()) {
			switch (index) {
			case 0:
				euclidianView3D.getRenderer().setNoneBackground();
				euclidianView3D.getRenderer().setBackgroundStyle(
						ConstructionDefaults.BACKGROUND_VISIBLE_NONE);
				break;
			case 1:
				euclidianView3D.getRenderer().setTransparentBackground();
				euclidianView3D.getRenderer().setBackgroundStyle(
						ConstructionDefaults.BACKGROUND_VISIBLE_TRANSPARENT);
				break;
			case 2:
				// set background color white
				euclidianView3D.getRenderer()
						.setOpaqueBackground(new float[] { 1, 1, 1, 1 });
				euclidianView3D.getRenderer().setBackgroundStyle(
						ConstructionDefaults.BACKGROUND_VISIBLE_OPAQUE);
				break;
			default:
				break;
			}
        }
    }

    @Override
    public int getIndex() {
        int backgroundState = app.getEuclidianView3D().getRenderer().getBackgroundStyle();
        for (int i = 0; i < backgroundStyles.length; i++) {
            if (backgroundState == backgroundStyles[i]) {
                return i;
            }
        }
        return -1;
    }
}
