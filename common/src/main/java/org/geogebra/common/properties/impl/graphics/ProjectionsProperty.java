package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

/**
 * This property controls the projection type for 3D view.
 */
public class ProjectionsProperty extends AbstractEnumerableProperty
		implements IconsEnumerableProperty {

    private EuclidianView view;
	private EuclidianSettings3D euclidianSettings;

	private PropertyResource[] icons = new PropertyResource[] {
			PropertyResource.ICON_PROJECTION_PARALLEL,
            PropertyResource.ICON_PROJECTION_PERSPECTIVE,
            PropertyResource.ICON_PROJECTION_GLASSES,
            PropertyResource.ICON_PROJECTION_OBLIQUE
	};

	/**
	 * Controls a grid style property.
	 *
	 * @param localization
	 *            localization for the title
     * @param view
     * 	          euclidian view.
	 * @param euclidianSettings
	 *            euclidian settings.
	 */
	public ProjectionsProperty(Localization localization,
                               EuclidianView view,
                               EuclidianSettings3D euclidianSettings) {
		super(localization, "Projection");
		this.view = view;
		this.euclidianSettings = euclidianSettings;
		setValuesAndLocalize(new String[] {
		        "stylebar.ParallelProjection",
                "stylebar.PerspectiveProjection",
				"stylebar.GlassesProjection",
                "stylebar.ObliqueProjection"
		});
	}

	@Override
	public int getIndex() {
	    if (view.isXREnabled()) {
	        return EuclidianView3DInterface.PROJECTION_PERSPECTIVE;
        }
		return euclidianSettings.getProjection();
	}

	@Override
	protected void setValueSafe(String value, int index) {
		euclidianSettings.setProjection(index);
	}

	@Override
	public PropertyResource[] getIcons() {
		return icons;
	}

    @Override
    public boolean isEnabled() {
        return !view.isXREnabled();
    }
}
