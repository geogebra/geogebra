/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_GLASSES;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_OBLIQUE;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC;
import static org.geogebra.common.euclidian3D.EuclidianView3DInterface.PROJECTION_PERSPECTIVE;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
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

	private final EuclidianView3DInterface view;
	private final EuclidianSettings3D euclidianSettings;

	private static final PropertyResource[] icons = new PropertyResource[]{
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
			EuclidianView3DInterface view, EuclidianSettings3D euclidianSettings) {
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
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return !view.isXREnabled();
	}
}
