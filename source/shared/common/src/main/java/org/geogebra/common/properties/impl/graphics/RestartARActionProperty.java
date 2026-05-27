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

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

/** {@code Property} responsible for restarting AR mode. */
public final class RestartARActionProperty extends AbstractActionableProperty
		implements ActionableIconProperty, EuclidianView3DDependentProperty {
	private final EuclidianView3DInterface euclidianView3D;

	/**
	 * Constructs the property.
	 * @param localization localization for the label translation
	 * @param euclidianView3D the 3D euclidean view
	 */
	public RestartARActionProperty(Localization localization,
			EuclidianView3DInterface euclidianView3D) {
		super(localization, "ar.restart");
		this.euclidianView3D = euclidianView3D;
	}

	@Override
	protected void doPerformAction() {
		euclidianView3D.getRenderer().setARShouldRestart();
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_RELOAD_AR;
	}

	@Override
	public boolean isAvailable() {
		return euclidianView3D.isXREnabled();
	}

	@Override
	public @Nonnull EuclidianView3DInterface getEuclidianView3D() {
		return euclidianView3D;
	}

	@Override
	public boolean isDisplayedAsOutlinedButton() {
		return true;
	}
}
