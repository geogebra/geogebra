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

package org.geogebra.common.keyboard;

import org.geogebra.common.main.App;
import org.geogebra.keyboard.base.impl.TemplateKeyProvider;

public class LocalizedTemplateKeyProvider implements TemplateKeyProvider {

	private final App app;

	public LocalizedTemplateKeyProvider(App app) {
		this.app = app;
	}

	@Override
	public String getPointFunction() {
		int dimension = app.getActiveEuclidianView().getDimension();
		return app.getSettings().getGeneral().getPointEditorTemplate() + ":" + dimension;
	}

	@Override
	public String getVectorFunction() {
		int dimension = app.getActiveEuclidianView().getDimension();
		return "$vector:" + dimension;
	}
}
