package org.geogebra.common.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.spreadsheet.core.ContextMenuItems;
import org.junit.Test;

public class ContextMenuItemsTest {
	private ContextMenuItems items;

	@Test
	public void testCellMenuKeys() {
		items = new ContextMenuItems(null, null);
		Map<String, Runnable> map = items.get(0, 1);
		shouldHaveKeys(map, "Delete", "Insert", "Copy", "Cut", "Paste");
	}

	private void shouldHaveKeys(Map<String, Runnable> map, String... keys) {
		assertEquals(map.keySet(), Set.of(keys));
	}
}
