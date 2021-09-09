package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.ScreenReader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

@SuppressWarnings("javadoc")
public class SymbolScreenReaderTest extends BaseUnitTest {
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	public void testDedicatedPrimes() {
		add("f = x");
		shouldRead("f'", readFunction("f prime"));
		shouldRead("f''", readFunction("f double prime"));
		shouldRead("f'''", readFunction("f triple prime"));
	}

	private void shouldRead(String definition, String expected) {
		GeoElement geo = add(definition);
		shouldRead(geo, expected);
	}

	private void shouldRead(GeoElement geo, String expected) {
		String actual = ScreenReader.getAuralText(geo,
				new ScreenReaderBuilder(geo.getKernel().getLocalization()));
		assertEquals(expected, actual);
	}

	private void shouldRead(GeoText geo, String expected) {
		String actual = ScreenReader.getAuralText(geo,
				new ScreenReaderBuilder(geo.getKernel().getLocalization()));
		assertEquals(expected, actual);
	}

	private String readFunction(String prime) {
		return "Function " + prime + " Press enter to edit Press tab to select controls";
	}

	@Test
	public void testManyPrimes() {
		add("f = x");
		shouldRead("f''''", readFunction("f prime prime prime prime"));
	}

	@Test
	public void testSliderDegreesPlural() {
		GeoNumeric slider = addSlider("α = 10°");
		shouldRead(slider, readSlider("alpha equals 10 degrees"));
	}

	private GeoNumeric addSlider(String definition) {
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		GeoNumeric slider = add(definition, info);
		slider.createSlider();
		return slider;
	}

	@Test
	public void testSliderDegreeSingle() {
		GeoNumeric slider = addSlider("α = 1°");
		shouldRead(slider, readSlider("alpha equals 1 degree"));
		slider.setValue(0);
		shouldRead(slider, readSliderUpOnly("alpha equals 0 degree"));
	}

	private String readSlider(String sider) {
		return "Slider " + sider
				+ " Press space to start animation Press up arrow to increase the value"
				+ " Press down arrow to decrease the value"
				+ " Press enter to edit Press tab to select controls";
	}

	private String readSliderUpOnly(String sider) {
		return "Slider " + sider
				+ " Press space to start animation"
				+ " Press up arrow to increase the value"
				+ " Press enter to edit Press tab to select controls";
	}

	@Test
	public void testSliderDegreeCaption() {
		GeoNumeric slider = addSlider("a = 10");
		slider.setCaption("$β=%v°$");
		shouldRead(slider, readSlider("beta equals 10 degrees"));
	}

	@Test
	public void testOneDegreeInGeoText() {
		shouldRead(this.<GeoText>add("\"sin(y)=1°\""),
				"sin open parenthesis y close parenthesis  equals 1 degree"
						+ " Press enter to edit Press tab to select controls");
	}

	@Test
	public void testMinusOneDegreeInGeoText() {
		shouldRead(this.<GeoText>add("\"sin(y)=-1°\""),
				"sin open parenthesis y close parenthesis  equals  minus 1 degree"
				+ " Press enter to edit Press tab to select controls");
	}

	@Test
	public void testDegreeInGeoTextPlurar() {
		shouldRead(this.<GeoText>add("\"sin(y)=35°\""),
				"sin open parenthesis y close parenthesis  equals 35 degrees"
				+ " Press enter to edit Press tab to select controls");
	}

	@Test
	public void testDegreeInGeoTextWithEqPlurar() {
		GeoText text = add("text1 = \"sin(x)=75°\"");
		text.setLaTeX(true, false);
		shouldRead(text,
				"sin open parenthesis x close parenthesis  equals 75 degrees"
				+ " Press enter to edit Press tab to select controls");
	}

	@Test
	public void testArcSinLatex() {
		atMinus1ShouldReadAsArc("sin");
	}

	@Test
	public void testArcCosLatex() {
		atMinus1ShouldReadAsArc("cos");
	}
	@Test
	public void testArcTanLatex() {
		atMinus1ShouldReadAsArc("tan");
	}
	@Test
	public void testArcCotLatex() {
		atMinus1ShouldReadAsArc("cot");
	}
	@Test
	public void testArcCSCLatex() {
		atMinus1ShouldReadAsArc("sin");
	}

	@Test
	public void testArcHyperbolicSinLatex() {
		atMinus1ShouldReadAsArcHyperbolic("sinh");
	}

	@Test
	public void testArcHyperbolicCosLatex() {
		atMinus1ShouldReadAsArcHyperbolic("cosh");
	}

	@Test
	public void testArcHyperbolicTanLatex() {
		atMinus1ShouldReadAsArcHyperbolic("tanh");
	}

	@Test
	public void testArcHyperbolicCotLatex() {
		atMinus1ShouldReadAsArcHyperbolic("coth");
	}

	@Test
	public void testArcHyperbolicSecLatex() {
		atMinus1ShouldReadAsArcHyperbolic("sech");
	}

	@Test
	public void testArcHyperbolicCscLatex() {
		atMinus1ShouldReadAsArcHyperbolic("csch");
	}

	private void atMinus1ShouldReadAsArc(String trigonometric) {
		GeoText geo = add("\"\\" + trigonometric + "^{-1}(x)\"");
		geo.setLaTeX(true, true);
		shouldRead(geo, " arc " + trigonometric + " open parenthesis x close parenthesis"
				+ " Press enter to edit Press tab to select controls");
	}

	private void atMinus1ShouldReadAsArcHyperbolic(String trigonometric) {
		GeoText geo = add("\"\\" + trigonometric + "^{-1}(x)\"");
		geo.setLaTeX(true, true);
		shouldRead(geo, " arc hyperbolic " + trigonometric.replace("h", "")
				+ " open parenthesis x close parenthesis"
				+ " Press enter to edit Press tab to select controls");
	}
}
