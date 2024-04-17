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
