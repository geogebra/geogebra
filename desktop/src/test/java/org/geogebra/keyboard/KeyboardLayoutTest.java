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
				"4oieOuKInuKJnzriiZ/iiaA64omg4oinOuKIp+KIqDriiKjihpI64oaSwqw6wqziipc64oqX4oilOuKI"
						+ "peKKpTriiqVFTVBUWV9JTUFHRTpOT05F4oiIOuKIiOKKgjriioLiioY64oqG4oigOuKIoOKMinjijIs6"
						+ "4oyK4oyIeOKMiTrijIgmOiZAOkAjOiNFTVBUWV9JTUFHRTpOT05FRU1QVFlfSU1BR0U6Tk9ORVs6W106"
						+ "XTo6OiI6IlRyYW5zbGF0ZS5jdXJyZW5jeTpUcmFuc2xhdGUuY3VycmVuY3nCqzrCq8K7OsK7RU1QVFlf"
						+ "SU1BR0U6Tk9ORUJBQ0tTUEFDRV9ERUxFVEU6QkFDS1NQQUNFX0RFTEVURUFCQzpTV0lUQ0hfVE9fQUJD"
						+ "LDosJzonIDogTEVGVF9BUlJPVzpMRUZUX0NVUlNPUlJJR0hUX0FSUk9XOlJJR0hUX0NVUlNPUlJFVFVSTl9FTlRFUjpSRVRVUk5fRU5URVI=",
				Base64.encodeToString(sb.toString().getBytes(), false));
	}
}
