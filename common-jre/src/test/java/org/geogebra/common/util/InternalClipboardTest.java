package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAcumulator;
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
		assertEquals("CLIPBOARDmagicSTRINGA CLIPBOARDmagicSTRINGB CLIPBOARDmagicSTRINGs",
				labels.trim());
		EventAcumulator acu = new EventAcumulator();
		getApp().getEventDispatcher().addEventListener(acu);
		InternalClipboard.pasteGeoGebraXMLInternal(getApp(), Arrays.asList(labels.split(" ")),
				clipboard.substring(clipboard.indexOf("\n")));
		assertEquals("PASTE_ELMS_COMPLETE [A_{1} = (0, 0), B_{1} = (1, 1), s_{1} = 1.41]",
				acu.getEvents().get(acu.getEvents().size() - 1));
	}
}
