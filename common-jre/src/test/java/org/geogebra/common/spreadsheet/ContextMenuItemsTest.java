package org.geogebra.common.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.spreadsheet.core.ContextMenuItems;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.junit.Test;

public class ContextMenuItemsTest {
	private ContextMenuItems items;

	@Test
	public void testCellMenuKeys() {
		testMenuKeys(1, 1,
				"Delete", "Copy", "Cut", "Paste");
	}

	private void testMenuKeys(int column, int row, String... keys) {
		items = new ContextMenuItems(null, null);
		Map<String, Runnable> map = items.get(row, column);
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

	@Test
	public void testDeleteCell() {
		TabularData<Object> data = createTabularData();
		items = new ContextMenuItems(data, null);
		assertEquals("cell12", data.contentAt(1, 2));
		Runnable delete = items.get(2, 1).get("Delete");
		delete.run();
		assertNull(data.contentAt(2, 1));
	}

	private TabularData<Object> createTabularData() {
		TestTabularData data = new TestTabularData();
		for (int column = 0; column < data.numberOfColumns(); column++) {
			for (int row = 0; row < data.numberOfRows(); row++) {
				data.setContent(row, column, "cell" + row + "" + column);
			}
		}
		return data;

	}
}
