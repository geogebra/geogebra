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
		testMenuKeys(1, 1,
				"Delete", "Insert", "Copy", "Cut", "Paste");
	}

	private void testMenuKeys(int column, int row, String... keys) {
		items = new ContextMenuItems(null, null);
		Map<String, Runnable> map = items.get(column, row);
		assertEquals(map.keySet(), Set.of(keys));
	}

	@Test
	public void testRowMenuKeys() {
		testMenuKeys(-1, 1,
				"ContextMenu.insertRowAbove",
				"ContextMenu.insertRowBelow", "ContextMenu.deleteRow");
	}

	@Test
	public void testColumnMenuKeys() {
		testMenuKeys(1, -1,
				"ContextMenu.insertColumnLeft", "ContextMenu.insertColumnRight",
					"ContextMenu.deleteColumn");
	}
}
