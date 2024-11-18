package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.InputContextMenuItem.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class InputContextMenuTests extends BaseUnitTest {
	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	@Test
	public void testWithHelpDisabled() {
		assertEquals(
				List.of(Expression,
						Text),
				contextMenuFactory.makeInputContextMenu(false)
		);
	}

	@Test
	public void testWithHelpEnabled() {
		assertEquals(
				List.of(Expression,
						Text,
						Help),
				contextMenuFactory.makeInputContextMenu(true)
		);
	}
}
