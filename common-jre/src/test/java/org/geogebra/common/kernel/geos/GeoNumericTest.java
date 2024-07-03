package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.RecurringDecimal;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoNumericTest extends BaseUnitTest {

	@Test
	public void euclidianShowabilityOfOperationResult() {
		GeoNumeric numeric = addAvInput("4+6");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testFractionRounding() {
		GeoNumeric numeric = addAvInput("6048 * (1/3)");
		assertThat(numeric.toValueString(StringTemplate.maxDecimals), is("2016"));
		GeoNumeric nearFraction = addAvInput("(0.99999999/2)+1");
		assertThat(nearFraction.toValueString(StringTemplate.maxDecimals), is("1.499999995"));
	}

	@Test
	public void testNumericIsNotDrawableInCas() {
		getApp().setConfig(new AppConfigCas());
		GeoNumeric numeric = addAvInput("2");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testSliderIsVisibleInEv() {
		GeoNumeric numeric = new GeoNumeric(getConstruction());
		GeoNumeric.setSliderFromDefault(numeric, false);
		assertThat(numeric.isEuclidianShowable(), is(true));
	}

	@Test
	public void getLaTeXDescriptionRHS() {
		GeoNumeric numeric = addAvInput("1/2");

		String descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						true, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("0.5"));

		descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						false, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("\\frac{1}{2}"));
	}

	@Test
	public void getLaTeXAlgebraDescription() {
		GeoNumeric numeric = addAvInput("a = 1/2");

		String description =
				numeric.getLaTeXAlgebraDescription(
						true, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,0.5"));

		description =
				numeric.getLaTeXAlgebraDescription(
						false, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,\\frac{1}{2}"));
	}

	@Test
	public void testCopy() {
		GeoNumeric numeric = addAvInput("1");
		numeric.setDrawable(true, false);
		GeoNumeric copy = numeric.copy();
		assertThat(copy.isDrawable, is(true));
	}

	@Test
	public void sliderTagShouldStayInXmlAfterSetUndefined() {
		GeoNumeric slider = add("sl=Slider(0,1,.1)");
		assertThat(slider.isEuclidianVisible(), is(true));
		add("SetValue(sl,?)");
		slider.setDefinition(new ExpressionNode(getKernel(), Double.NaN));
		assertThat(slider.isEuclidianVisible(), is(false));
		String xml = slider.getXML();
		assertThat(xml, containsString("<slider"));
	}

	@Test
	public void undefinedSliderShouldBeSliderableAfterReload() {
		GeoNumeric slider = add("sl=Slider(0,1,.1)");
		add("SetValue(sl,?)");
		slider.setDefinition(new ExpressionNode(getKernel(), Double.NaN));
		reload();
		GeoElement reloaded = lookup("sl");
		add("SetValue(sl,.5)");
		assertThat(slider.isEuclidianVisible(), is(false));
		assertThat(((GeoNumeric) reloaded).isSliderable(), is(true));
	}

	@Test
	public void testRecurringSwitchSymbolic() {
		GeoNumeric recurring = addAvInput("1.2\u03053\u0305");
		assertThat(recurring.getDefinition().unwrap().isRecurringDecimal(), is(true));
		recurring.setSymbolicMode(false, true);
		assertThat(recurring.toValueString(StringTemplate.maxDecimals),
				is("1.232323232323232"));
		recurring.setSymbolicMode(true, true);
		assertThat(recurring.toValueString(StringTemplate.maxDecimals),
				is("122 / 99"));
	}

	@Test
	public void testIsRecurringDecimal() {
		assertThat(this.<GeoNumeric>add("1.2\u03053\u0305").isRecurringDecimal(), is(true));
		assertThat(this.<GeoNumeric>add("1.234").isRecurringDecimal(), is(false));
		assertThat(this.<GeoNumeric>add("12 / 34").isRecurringDecimal(), is(false));
	}

	@Test
	public void testAsRecurringDecimal() {
		assertThat(this.<GeoNumeric>add("1.02\u03053\u0305").asRecurringDecimal(),
				is(RecurringDecimal.parse(getKernel(), "1.0", "23")));
		assertThat(this.<GeoNumeric>add("1.234").asRecurringDecimal(), nullValue());
		assertThat(this.<GeoNumeric>add("12 / 34").asRecurringDecimal(), nullValue());
	}

	@Test
	public void testFormulaString() {
		GeoNumeric recurring = add("1 + 0.3\u0305");
		StringTemplate tpl = StringTemplate.defaultTemplate;
		assertThat(recurring.getFormulaString(tpl, true), is("1.33"));
		assertThat(recurring.getFormulaString(tpl, false), is("1 + 0.3\u0305"));
		recurring.setSymbolicMode(true, true);
		assertThat(recurring.getFormulaString(tpl, true), is("4 / 3"));
		assertThat(recurring.getFormulaString(tpl, false), is("1 + 0.3\u0305"));
	}

	@Test
	@Issue("APPS-5531")
	public void shouldKeepTrailingZeros() {
		GeoNumeric withTrailing = addAvInput("1.20");
		assertThat(withTrailing.getXML(), containsString("1.20"));
		withTrailing.setEuclidianVisible(true);
		assertThat(withTrailing.getXML(), containsString("1.20"));
	}

	@Test
	@Issue("APPS-5531")
	public void shouldKeepENotation() {
		GeoNumeric withTrailing = addAvInput("1.20E3");
		assertThat(withTrailing.getXML(), containsString("1.20E3"));
	}
}