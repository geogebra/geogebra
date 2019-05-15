package org.geogebra.keyboard;

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
		StringBuilder actions = new StringBuilder();
		StringBuilder resources = new StringBuilder();
		for (Row row : kb.getRows()) {
			for (WeightedButton button : row.getButtons()) {
				resources.append(
						button.getResourceName()
						+ ",");
				actions.append(button.getPrimaryActionName()
						+ ",");
			}
		}

		Assert.assertEquals(
				"\u221E,\u225F,\u2260,\u2227,\u2228,\u2192,\u00AC,\u2297,\u2225,\u27c2,\u2208,\u2282,\u2286,\u2220,FLOOR,CEIL,&,@,#,Translate.currency,EMPTY_IMAGE,(,),[,],:,\",\u2032,\u2033,EMPTY_IMAGE,BACKSPACE_DELETE,ABC,,,\', ,LEFT_ARROW,RIGHT_ARROW,RETURN_ENTER,",
				resources.toString());
		Assert.assertEquals(
				"\u221E,\u225F,\u2260,\u2227,\u2228,\u2192,\u00AC,\u2297,\u2225,\u27c2,\u2208,\u2282,\u2286,\u2220,\u230A,\u2308,&,@,#,Translate.currency,NONE,(,),[,],:,\",\u2032,\u2033,NONE,BACKSPACE_DELETE,SWITCH_TO_ABC,,,\', ,LEFT_CURSOR,RIGHT_CURSOR,RETURN_ENTER,",
				actions.toString());

	}
}
