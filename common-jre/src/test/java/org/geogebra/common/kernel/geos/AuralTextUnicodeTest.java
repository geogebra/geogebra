package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

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

	@Test
	public void testLaTeX() {
		aural("LaTeX(x-y)", "x" + Unicode.MINUS + "y");
		aural("LaTeX(\"x-y\")", "x" + Unicode.MINUS + "y");
		aural("LaTeX(\"\\text{x-y}\")", "x\u2010y");
		aural("LaTeX((-1,2))", "( " + Unicode.MINUS + "1, 2)");
		aural("LaTeX((1,2,3))", "( 1, 2, 3)");
	}

	@Test
	public void testNumbers() {
		auralValue("-1", "a = " + Unicode.MINUS + "1");
		auralDefinition("-1", Unicode.MINUS + "1");
		auralDefinition("a-1-a", "a " + Unicode.MINUS + " 1 " + Unicode.MINUS + " a");
		auralValue("x-y", "d(x, y) = x " + Unicode.MINUS + " y");
	}

	private void aural(String in, String expected) {
		GeoElement geo = add(in);
		String aural = geo.getAuralText(new ScreenReaderBuilderDot(getApp().getLocalization()))
				.split("\\. ")[0];
		assertEquals(expected, aural);
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