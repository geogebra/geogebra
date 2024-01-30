package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Test;

public class AuralTextUnicodeTest extends BaseUnitTest {

	@Override
	public AppCommon3D createAppCommon() {
		return new AppCommon3D(new LocalizationCommonUTF(3), new AwtFactoryCommon()) {
			@Override
			public StringTemplate getScreenReaderTemplate() {
				return StringTemplate.screenReaderUnicode;
			}
		};
	}

	@Test
	public void functionTest() {
		add("a=7");
		auralDefinition("a+sqrt(x)", "a + sqrt(x)");
		auralValue("a+sqrt(x)", "g(x) = 7 + sqrt(x)");
	}

	private void auralValue(String in, String expected) {
		GeoElement geo = add(in);
		assertEquals(expected, geo.toString(getApp().getScreenReaderTemplate()));
	}

	private void auralDefinition(String in, String expected) {
		GeoElement geo = add(in);
		assertEquals(expected, geo.getDefinition(getApp().getScreenReaderTemplate()));
	}
}