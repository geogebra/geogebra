package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_GLASSES;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_OBLIQUE;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_PERSPECTIVE;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * This property controls the projection type for 3D view.
 */
public class ProjectionsProperty extends AbstractNamedEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private EuclidianView view;
	private EuclidianSettings3D euclidianSettings;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_PROJECTION_PARALLEL,
			PropertyResource.ICON_PROJECTION_PERSPECTIVE,
			PropertyResource.ICON_PROJECTION_GLASSES,
			PropertyResource.ICON_PROJECTION_OBLIQUE
	};

	/**
	 * Controls a grid style property.
	 * @param localization localization for the title
	 * @param view euclidian view.
	 * @param euclidianSettings euclidian settings.
	 */
	public ProjectionsProperty(Localization localization,
			EuclidianView view, EuclidianSettings3D euclidianSettings) {
		super(localization, "Projection");
		this.view = view;
		this.euclidianSettings = euclidianSettings;
		setNamedValues(List.of(
				entry(PROJECTION_ORTHOGRAPHIC, "stylebar.ParallelProjection"),
				entry(PROJECTION_PERSPECTIVE, "stylebar.PerspectiveProjection"),
				entry(PROJECTION_GLASSES, "stylebar.GlassesProjection"),
				entry(PROJECTION_OBLIQUE, "stylebar.ObliqueProjection")
		));
	}

	@Override
	public Integer getValue() {
		if (view.isXREnabled()) {
			return PROJECTION_PERSPECTIVE;
		}
		return euclidianSettings.getProjection();
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setProjection(value);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public boolean isEnabled() {
		return !view.isXREnabled();
	}
}
