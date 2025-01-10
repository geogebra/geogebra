package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.MaterialContextMenuItem.Delete;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class MaterialContextMenuTests {
	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	@Test
	public void testSingleContextMenuItem() {
		assertEquals(
				List.of(Delete),
				contextMenuFactory.makeMaterialContextMenu()
		);
	}
}
