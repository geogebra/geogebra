package org.geogebra.keyboard;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.junit.Assert;
import org.junit.Test;

public class KeyboardLayoutTest {
	@Test
	public void testSpecialTab() {
		KeyboardFactory kbf = new KeyboardFactory();
		KeyboardModel kb = kbf.createSpecialSymbolsKeyboard().getModel();
		StringBuilder sb = new StringBuilder();
		for (Row row : kb.getRows()) {
			for (WeightedButton button : row.getButtons()) {
				sb.append(button.getResourceName() + ":"
						+ button.getActionName());
			}
		}
		Assert.assertEquals(
				"4oieOuKInlxxdWVzdGVxOuKJn+KJoDriiaDiiKc64oin4oioOuKIqOKGkjrihpLCrDrCrOKKlzriipfiiKU64oil4oqlOuKKpUVNUFRZX0lNQUdFOk5PTkXiiIg64oiI4oqCOuKKguKKhjriiobiiKA64oig4oyKeOKMizrijIrijIh44oyJOuKMiCY6JkA6QCM6I0VNUFRZX0lNQUdFOk5PTkVFTVBUWV9JTUFHRTpOT05FWzpbXTpdOjo6IjoiVHJhbnNsYXRlLmN1cnJlbmN5OlRyYW5zbGF0ZS5jdXJyZW5jecKrOsKrwrs6wrtFTVBUWV9JTUFHRTpOT05FQkFDS1NQQUNFX0RFTEVURTpCQUNLU1BBQ0VfREVMRVRFQUJDOlNXSVRDSF9UT19BQkMsOiwnOicgOiBMRUZUX0FSUk9XOkxFRlRfQ1VSU09SUklHSFRfQVJST1c6UklHSFRfQ1VSU09SUkVUVVJOX0VOVEVSOlJFVFVSTl9FTlRFUg==",
				Base64.encodeToString(sb.toString().getBytes(), false));
	}
}
