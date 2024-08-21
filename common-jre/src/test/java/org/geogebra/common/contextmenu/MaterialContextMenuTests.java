package org.geogebra.common.contextmenu;

import static org.geogebra.regexp.shared.contextmenu.ContextMenuFactory.makeMaterialContextMenu;
import static org.geogebra.regexp.shared.contextmenu.MaterialContextMenuItem.Delete;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class MaterialContextMenuTests {
	@Test
	public void testSingleContextMenuItem() {
		assertEquals(
				List.of(Delete),
				makeMaterialContextMenu()
		);
	}
}
