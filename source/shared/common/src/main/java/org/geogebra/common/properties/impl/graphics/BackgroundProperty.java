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

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class BackgroundProperty extends AbstractNamedEnumeratedProperty<Renderer.BackgroundStyle> {

	private App app;

	/**
	 * Constructs an BackgroundProperty
	 * @param app app
	 * @param localization the localization used
	 */
	public BackgroundProperty(App app, Localization localization) {
		super(localization, "ar.background");
		this.app = app;
		setNamedValues(List.of(
				entry(Renderer.BackgroundStyle.NONE, "Camera"),
				entry(Renderer.BackgroundStyle.TRANSPARENT, "ar.filter"),
				entry(Renderer.BackgroundStyle.OPAQUE, "ar.opaqueColor")
		));
	}

	@Override
	public Renderer.BackgroundStyle getValue() {
		return app.getEuclidianView3D().getRenderer().getBackgroundStyle();
	}

	@Override
	protected void doSetValue(Renderer.BackgroundStyle value) {
		EuclidianView3DInterface euclidianView3D = app.getEuclidianView3D();
		if (euclidianView3D.isXREnabled()) {
			euclidianView3D.getRenderer().setBackgroundStyle(value);
		}
	}
}
