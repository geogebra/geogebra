package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * This property controls the projection type for 3D view.
 */
public class ProjectionsProperty extends AbstractEnumerableProperty
		implements IconsEnumerableProperty {

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
	 * @param euclidianSettings
	 *            euclidian settings.
	 */
	public ProjectionsProperty(Localization localization,
                               EuclidianSettings3D euclidianSettings) {
		super(localization, "Projection");
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
}
