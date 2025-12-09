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

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class OmitGreenChannelProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, SettingsDependentProperty {
	private EuclidianView3DInterface euclidianView;

	/**
	 * Creates a  property for toggling green channel in
	 * {@link ProjectionsProperty} PROJECTION_GLASSES
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public OmitGreenChannelProperty(Localization localization,
			EuclidianView3DInterface euclidianView) {
		super(localization, "OmitGreen");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianView.setGlassesShutDownGreen(value);
		euclidianView.repaintView();
	}

	@Override
	public Boolean getValue() {
		return euclidianView.isGlassesShutDownGreen();
	}

	@Override
	public boolean isAvailable() {
		return euclidianView.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES;
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianView.getSettings();
	}
}
