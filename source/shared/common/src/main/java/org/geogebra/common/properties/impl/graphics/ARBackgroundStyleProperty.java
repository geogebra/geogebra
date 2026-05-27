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

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/** {@code Property} responsible for changing the style of the background in AR mode. */
public final class ARBackgroundStyleProperty
		extends AbstractNamedEnumeratedProperty<Renderer.BackgroundStyle>
		implements EuclidianView3DDependentProperty {
	private final EuclidianView3DInterface euclidianView3D;

	/**
	 * Constructs the property.
	 * @param localization localization for the title and option translations
	 * @param euclidianView3D the 3D euclidian view
	 */
	public ARBackgroundStyleProperty(Localization localization,
			EuclidianView3DInterface euclidianView3D) {
		super(localization, "ar.background");
		this.euclidianView3D = euclidianView3D;
		setNamedValues(List.of(
				entry(Renderer.BackgroundStyle.NONE, "Camera"),
				entry(Renderer.BackgroundStyle.TRANSPARENT, "ar.filter"),
				entry(Renderer.BackgroundStyle.OPAQUE, "ar.opaqueColor")
		));
	}

	@Override
	public Renderer.BackgroundStyle getValue() {
		return euclidianView3D.getRenderer().getBackgroundStyle();
	}

	@Override
	protected void doSetValue(Renderer.BackgroundStyle value) {
		if (euclidianView3D.isXREnabled()) {
			euclidianView3D.getRenderer().setBackgroundStyle(value);
		}
	}

	@Override
	public boolean isAvailable() {
		return euclidianView3D.isXREnabled();
	}

	@Override
	public @Nonnull EuclidianView3DInterface getEuclidianView3D() {
		return euclidianView3D;
	}
}
