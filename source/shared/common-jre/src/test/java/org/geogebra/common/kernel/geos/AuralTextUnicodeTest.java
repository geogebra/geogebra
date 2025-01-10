package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.LatexRendererSettings;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.serializer.ScreenReaderSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

public class AuralTextUnicodeTest extends BaseUnitTest {

	@Override
	public AppCommon3D createAppCommon() {
		return new AppCommon3D(new LocalizationCommonUTF(3), new AwtFactoryCommon(),
				new AppConfigDefault()) {
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
	public void testAuralDegreeUnicode() {
		auralText("LaTeX(\"\\text{a 7\\degree}\")", "a 7°");
		auralText("LaTeX(\"\\text{a 1\\degree}\")", "a 1°");
	}

	private void auralText(String in, String expected) {
		GeoElement geo = add(in);
		String aural = geo.getAuralText(new ScreenReaderBuilderDot(getApp().getLocalization()))
				.split("\\. ")[0];
		assertEquals(expected, aural);
	}

	@Test
	public void testLaTeX() {
		auralText("LaTeX(x-y)", "x" + Unicode.MINUS + "y");
		auralText("LaTeX(\"x-y\")", "x" + Unicode.MINUS + "y");
		auralText("LaTeX(\"\\text{x-y}\")", "x\u2010y");
		auralText("LaTeX((-1,2))", "( " + Unicode.MINUS + "1, 2)");
		auralText("LaTeX((1,2,3))", "( 1, 2, 3)");
	}

	@Test
	public void testNumbers() {
		auralValue("-1", "a = " + Unicode.MINUS + "1");
		auralDefinition("-1", Unicode.MINUS + "1");
		auralDefinition("a-1-a", "a " + Unicode.MINUS + " 1 " + Unicode.MINUS + " a");
		auralValue("x-y", "d(x, y) = x " + Unicode.MINUS + " y");
	}

	@Test
	public void shouldHaveSpaceForScreenReader() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		add("A=(1,1)");
		GeoInputBox input = add("ib=InputBox(A)");
		final MathFieldCommon mf = new MathFieldCommon(new MetaModel(), null);
		SymbolicEditorCommon editor = new SymbolicEditorCommon(mf, getApp());
		editor.attach(input, new Rectangle(),
				LatexRendererSettings.create());
		SerializationAdapter adapter = ScreenReader.getSerializationAdapter(getApp());
		assertThat(ScreenReaderSerializer.fullDescription(editor.getMathFieldInternal().getFormula()
				.getRootComponent(), adapter), equalTo("( 1, 1)"));

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