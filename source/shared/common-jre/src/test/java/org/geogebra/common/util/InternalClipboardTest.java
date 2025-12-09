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

package org.geogebra.common.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.EventAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InternalClipboardTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	public void clipboardItemsShouldBeSorted() {
		getApp().setRounding("2");
		evaluate("A=(0,0)");
		evaluate("B=(1,1)");
		GeoElement s = evaluateGeoElement("s=Segment(A,B)");
		String clipboard = InternalClipboard.getTextToSave(getApp(),
				Collections.singletonList(s), txt -> txt);
		String labels = clipboard.split("\n")[0];
		assertEquals("@A @B @s",
				labels.trim().replace(CopyPaste.labelPrefix, "@"));
		EventAccumulator acu = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(acu);
		InternalClipboard.pasteGeoGebraXMLInternal(getApp(), Arrays.asList(labels.split(" ")),
				clipboard.substring(clipboard.indexOf("\n")));
		assertEquals("PASTE_ELMS_COMPLETE [A_{1} = (0, 0), B_{1} = (1, 1), s_{1} = 1.41]",
				acu.getEvents().get(acu.getEvents().size() - 2)); // last event is STOREUNDO
	}

	@ParameterizedTest
	@CsvSource(value = {
			"Stadium((1,1),(3,1),2);Stadium((-1, 0), (1, 0), 2)",
			"BezierCurve((1,1),(3,1),(2,3),(5,3));BezierCurve((-2, -1), (0, -1), (-1, 1), (2, 1))"
	}, delimiter = ';')
	public void pastedElementsShouldBeCentered(String input, String centered) {
		getApp().setNotesConfig();
		evaluate("ZoomIn(-10,-10,10,10)");
		GeoElement s = evaluateGeoElement("s=" + input);
		String clipboard = InternalClipboard.getTextToSave(getApp(),
				List.of(s), txt -> txt);
		String labels = clipboard.split("\n")[0];
		InternalClipboard.pasteGeoGebraXMLInternal(getApp(), Arrays.asList(labels.split(" ")),
				clipboard.substring(clipboard.indexOf("\n")));
		assertArrayEquals(new String[]{"s", "s_{1}"},
				getApp().getGgbApi().getAllObjectNames());
		assertEquals(centered,
				getKernel().lookupLabel("s_{1}").getDefinition(StringTemplate.testTemplate));
	}

}
