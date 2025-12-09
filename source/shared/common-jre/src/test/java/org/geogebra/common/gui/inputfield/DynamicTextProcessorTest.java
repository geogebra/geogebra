/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
