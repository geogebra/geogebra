package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Before;
import org.junit.Test;

public class TableValuesInputProcessorTest extends BaseUnitTest {

	private TableValuesInputProcessor processor;
	private GeoList list;

	@Before
	public void setUp() {
		TableValues view = new TableValuesView(getKernel());
		getKernel().attach(view);
		processor = new TableValuesInputProcessor(getConstruction());
		list = new GeoList(getConstruction());
	}

	@Test
	public void testValidInput() {
		processor.processInput("1", list, 0);
		processor.processInput("2", list, 1);
		processor.processInput("99.9", list, 2);
		processor.processInput("0.01", list, 3);
		processor.processInput("", list, 4);
		processor.processInput("20e-2", list, 6);
		processor.processInput("10", list, 10);
		assertThat(list.size(), is(11));
		assertValue("1", 0);
		assertValue("2", 1);
		assertValue("99.9", 2);
		assertValue("0.01", 3);
		assertEmptyInput(4);
		assertEmptyInput(5);
		assertValue("0.2", 6);
		assertValue("10", 10);
	}

	private void assertValue(String value, int index) {
		GeoElement element = list.get(index);
		assertTrue(element instanceof GeoNumeric);
		assertThat(element.toString(StringTemplate.defaultTemplate), is(value));
	}

	private void assertEmptyInput(int index) {
		GeoElement element = list.get(index);
		assertTrue(element instanceof GeoText);
		assertThat(((GeoText) element).getTextString(), is(""));
	}

	@Test
	public void testInvalidInputWithComma() {
		processor.processInput("10,2", list, 0);
		assertEmptyInput("10,2");
	}

	private void assertEmptyInput(String input) {
		GeoElement element = list.get(0);
		assertTrue(element instanceof GeoText);
		assertEquals(input, ((GeoText) element).getTextString());

	}

	@Test
	public void testInvalidInputWithOperators() {
		processor.processInput("10 + 2", list, 0);
		assertEmptyInput("10 + 2");
	}

	@Test
	public void testInvalidInputWithLetters() {
		processor.processInput("a", list, 0);
		assertEmptyInput("a");
	}
}
