package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.ContextMenu.makeInputContextMenu;
import static org.geogebra.common.contextmenu.InputContextMenuItem.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class InputContextMenuTests extends BaseUnitTest {
	@Test
	public void testWithHelpDisabled() {
		assertEquals(
				List.of(Expression, Text),
				makeInputContextMenu(false, item -> {}).getItems()
		);
	}

	@Test
	public void testWithHelpEnabled() {
		assertEquals(
				List.of(Expression, Text, Help),
				makeInputContextMenu(true, item -> {}).getItems()
		);
	}
}
