package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class CreateSliderTest extends BaseSymbolicTest {

	private CreateSlider createSlider;

	@Before
	public void setUp() {
		LabelController controller = new LabelController();
		createSlider = new CreateSlider(ap, controller);
	}

	@Test
	public void testExecute() {
		GeoSymbolic symbolic = add("4.669");

		createSlider.execute(symbolic);

		GeoNumeric numeric = (GeoNumeric) lookup("a");
		assertThat(numeric.isSliderable(), is(true));
		assertThat(numeric.getLabelSimple(), is("a"));
	}

	@Test
	public void testIsAvailable() {
		GeoSymbolic numeric = add("4.669");
		assertThat(createSlider.isAvailable(numeric), is(true));
		GeoSymbolic angle = add("4.669" + Unicode.DEGREE_STRING);
		assertThat(createSlider.isAvailable(angle), is(true));
	}

	@Test
	public void testAngleSetSlider() {
		GeoSymbolic symbolic = add("45°");
		createSlider.execute(symbolic);
		GeoAngle angle = (GeoAngle) lookup(Unicode.alpha + "");
		Assert.assertTrue(DoubleUtil.isEqual(angle.getIntervalMin(), 0));
		Assert.assertTrue(DoubleUtil.isEqual(angle.getIntervalMax(), 2 * Math.PI));
	}

	@Test
	public void testUndefinedVariableCannotBecomeSlider() {
		GeoElement element = add("undefa");
		assertThat(createSlider.isAvailable(element), is(false));
	}

	@Test
	public void testFunctionCannotBecomeSlider() {
		GeoElement element = add("x^2");
		assertThat(createSlider.isAvailable(element), is(false));
	}

	@Test
	public void testExpressionCannotBecomeSlider() {
		String[] expressions = {"1+2", "2*9", "1/4", "5^6"};
		for (String expression : expressions) {
			GeoElement element = add(expression);
			assertThat(createSlider.isAvailable(element), is(false));
		}
	}

	@Test
	public void testCommandsCannotBecomeSlider() {
		String[] expressions = {"Cross((1,2),(3,4))", "Dot((1,2),(3,4))", "Degree(x^2)"};
		for (String expression : expressions) {
			GeoElement element = add(expression);
			assertThat(createSlider.isAvailable(element), is(false));
		}
	}

	@Test
	public void testShowAlgebraIsStoredInXML() {
		GeoElement symbolic = add("a = 5");
		createSlider.execute(symbolic);
		GeoNumeric element = (GeoNumeric) lookup("a");

		String xml = element.getXML();
		assertThat(xml.matches("[\\s\\S]*<slider [^>]* showAlgebra=\"true\"[\\s\\S]*"), is(true));
	}


	@Test
	public void testUndoRedoKeepsShowingExtendedAV() {
		GeoElement symbolic = add("a = 5");
		createSlider.execute(symbolic);

		app.setXML(app.getXML(), true);
		GeoNumeric element = (GeoNumeric) lookup("a");
		Assert.assertTrue(element.isShowingExtendedAV());
	}

	@Test
	public void testWithSubstitute() {
		add("f(x) = xa + 3");
		GeoSymbolic symbolic = add("b = 1");
		add("Substitute(f,a,b)");

		createSlider.execute(symbolic);

		GeoNumeric numeric = (GeoNumeric) lookup("b");
		assertThat(numeric.isSliderable(), is(true));
	}
}
