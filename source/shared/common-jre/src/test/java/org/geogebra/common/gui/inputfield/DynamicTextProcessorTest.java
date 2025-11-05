package org.geogebra.common.gui.inputfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.TextDispatcher;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.test.TestErrorHandler;
import org.junit.Test;

public class DynamicTextProcessorTest extends BaseUnitTest {

	@Test
	public void updateShouldNotResetLabeling() {
		add("A=(1,1)");
		GeoText dynamic = add("dynamic=\"\"");
		List<DynamicTextElement> foo = List.of(
				new DynamicTextElement("foo", DynamicTextElement.DynamicTextType.STATIC),
				new DynamicTextElement("yA", DynamicTextElement.DynamicTextType.FORMULA_TEXT));
		new DynamicTextProcessor(getApp()).process(foo, dynamic, true,
				TestErrorHandler.INSTANCE, () -> {});
		assertFalse(getConstruction().isSuppressLabelsActive());
		assertEquals("\"foo\" + (FormulaText(X = y * A)) + \"\"",
				lookup("dynamic").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	public void parseDistanceText() {
		TextDispatcher td = new TextDispatcher(getKernel(), getApp().getEuclidianView1());
		GeoText text = (GeoText) td.createDistanceText(add("(1,1)"), (GeoPointND) add("(2,1)"));
		List<String> list = new DynamicTextProcessor(getApp())
				.buildDynamicTextList(text).stream()
				.map(Object::toString)
				.collect(Collectors.toList());

		assertEquals(List.of("DEFINITION: \"A\"", "DEFINITION: \"B\"",
				"STATIC: \" = \"", "VALUE: \"distanceAB\""), list);
	}
}
