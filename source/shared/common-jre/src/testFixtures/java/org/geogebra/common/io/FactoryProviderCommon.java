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

package org.geogebra.common.io;

import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.FontFactory;
import com.himamis.retex.renderer.share.platform.geom.GeomFactory;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;

public class FactoryProviderCommon extends FactoryProvider {

	@Override
	protected GeomFactory createGeomFactory() {
		return mock(GeomFactory.class);
	}

	@Override
	protected FontFactory createFontFactory() {
		return mock(FontFactory.class, withSettings().defaultAnswer(
				RETURNS_MOCKS));
	}

	@Override
	protected GraphicsFactory createGraphicsFactory() {
		return mock(GraphicsFactory.class, withSettings().defaultAnswer(
				RETURNS_MOCKS));
	}

}
