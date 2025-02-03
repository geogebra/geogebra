package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.junit.Test;

public class HatchingHandlerTest {

	/** Test for APPS-4819 */
	@Test
	public void symbolDimensions() {
		AppCommon app = AppCommonFactory.create();
		for (Integer size: Arrays.asList(27, 28, 29)) {
			AwtFactoryCommon.GTexturePaintCommon texture = getSymbolFill(app, size);
			assertEquals(27, texture.subImage.getHeight());
			assertEquals(27, texture.rect.getHeight(), 0.1);
		}
	}

	private AwtFactoryCommon.GTexturePaintCommon getSymbolFill(App app, int size) {
		return (AwtFactoryCommon.GTexturePaintCommon) new HatchingHandler().setHatching(
				new DefaultBasicStroke(),
				GColor.GREEN, GColor.RED, 0, size / 2.5, 0,
				FillType.SYMBOLS, "X", app);
	}

}
