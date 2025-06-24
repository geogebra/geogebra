package org.geogebra.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoStadium;
import org.geogebra.test.EventAccumulator;
import org.junit.Test;

public class InternalClipboardTest extends BaseUnitTest {

	@Test
	public void clipboardItemsShouldBeSorted() {
		add("A=(0,0)");
		add("B=(1,1)");
		GeoElement s = add("s=Segment(A,B)");
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

	@Test
	public void pastedStadiumShouldBeCentered() {
		getApp().setNotesConfig();
		add("ZoomIn(-10,-10,10,10)");
		GeoStadium s = add("s=Stadium((1,1),(3,1),2)");
		String clipboard = InternalClipboard.getTextToSave(getApp(),
				Collections.singletonList(s), txt -> txt);
		String labels = clipboard.split("\n")[0];
		InternalClipboard.pasteGeoGebraXMLInternal(getApp(), Arrays.asList(labels.split(" ")),
				clipboard.substring(clipboard.indexOf("\n")));
		assertArrayEquals(new String[]{"s", "s_{1}"}, getApp().getGgbApi().getAllObjectNames());
		assertEquals("Stadium((-1, 0), (1, 0), 2)",
				lookup("s_{1}").getDefinition(StringTemplate.testTemplate));
	}

}
