package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.ScreenReader;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class SymbolScreenReaderTest extends BaseUnitTest {
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
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		GeoNumeric slider = add("α = 10°", info);
		slider.createSlider();
		shouldRead(slider, readSlider("α equals 10 degrees"));
	}

	@Test
	public void testSliderDegreeSingle() {
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		GeoNumeric slider = add("α = 1°", info);
		slider.createSlider();
		shouldRead(slider, readSlider("α equals 1 degree"));
		slider.setValue(0);
		shouldRead(slider, readSliderUpOnly("α equals 0 degree"));
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
}
