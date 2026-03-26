/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.spreadsheet;

import static org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu.MenuCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpreadsheetContextMenuTest extends BaseAppTestSetup {

	private MyTable table;
	private TabularRange selection;

	private static class TestMenu extends SpreadsheetContextMenu<List<Object>> {
		private final List<Object> content = new ArrayList<>();

		private TestMenu(MyTable table, SpreadsheetToolProcessor toolProcessor) {
			super(table, toolProcessor);
		}

		@Override
		public void addMenuItem(MenuCommand command, String text, boolean enabled) {
			content.add(command);
		}

		@Override
		public void addCheckBoxMenuItem(MenuCommand command, String text,
				boolean isSelected) {
			content.add(command);
		}

		@Override
		public List<Object> addSubMenu(String text, MenuCommand command) {
			content.add(command);
			ArrayList<Object> sub = new ArrayList<>();
			content.add(sub);
			return sub;
		}

		@Override
		public void addSubMenuItem(List<Object> menu, MenuCommand cmdString, String text,
				boolean enabled) {
			menu.add(cmdString);
		}

		@Override
		public void createGUI() {
			initMenu();
		}
	}

	private static class TestCopyPaste extends CopyPasteCut {

		private String buffer;

		private TestCopyPaste(App app, MyTable table) {
			super(app, table);
		}

		@Override
		public void copy(int column1, int row1, int column2, int row2, boolean skipGeoCopy) {
			buffer = copyStringToBuffer(column1, row1, column2, row2);
		}

		@Override
		public boolean paste(int column1, int row1, int column2, int row2) {
			String[][] data = DataImport.parseExternalData(app, buffer, false);
			return pasteExternalMultiple(data, new TabularRange(row1, column1, row2, column2));
		}
	}

	@BeforeEach
	void setup() {
		setupClassicApp();
		table = mock(MyTable.class);
		evaluate("A1=1");
		evaluate("A2=2");
		evaluate("B1=3");
		evaluate("B2=4");
		doReturn(getApp()).when(table).getApplication();
		doReturn(SelectionType.CELLS).when(table).getSelectionType();
		TestCopyPaste copyPaste = new TestCopyPaste(getApp(), table);
		doReturn(copyPaste).when(table).getCopyPasteCut();
		doAnswer((ignore) -> selection).when(table).getFirstSelection();
		doAnswer((ignore) -> new ArrayList<>(List.of(selection)))
				.when(table).getSelectedRanges();
	}

	@Test
	public void testCreateGUIEmpty() {
		TestMenu menu = new TestMenu(table, new SpreadsheetToolProcessor(getApp(), null));
		menu.createGUI();
		List<Object> expected = List.of(MenuCommand.Copy, MenuCommand.Paste, MenuCommand.Cut,
				MenuCommand.Delete, MenuCommand.SpreadsheetOptions);
		assertEquals(expected, menu.content);
	}

	@Test
	public void testCreateGUISingleCell() {
		selection = new TabularRange(0, 0, 0, 0);
		getApp().getSelectionManager().addSelectedGeo(lookup("A1"));
		TestMenu menu = new TestMenu(table, new SpreadsheetToolProcessor(getApp(), null));
		menu.createGUI();
		List<Object> expected = List.of(MenuCommand.Copy, MenuCommand.Paste, MenuCommand.Cut,
				MenuCommand.Delete, MenuCommand.Create,
					List.of(MenuCommand.List, MenuCommand.ListOfPoints, MenuCommand.Matrix,
							MenuCommand.Table, MenuCommand.PolyLine, MenuCommand.OperationTable),
				MenuCommand.ShowLabel, MenuCommand.RecordToSpreadsheet, MenuCommand.Properties);
		assertEquals(expected, menu.content);
	}

	@Test
	public void testCopyPaste() {
		selection = new TabularRange(0, 0, 0, 0);
		getApp().getSelectionManager().addSelectedGeo(lookup("A1"));
		TestMenu menu = new TestMenu(table, new SpreadsheetToolProcessor(getApp(), null));
		menu.doCommand(MenuCommand.Copy);
		selection = new TabularRange(5, 5, 5, 5);
		menu.updateFields();
		menu.doCommand(MenuCommand.Paste);
		assertEquals("1", lookup("F6").toValueString(StringTemplate.algebraTemplate));
	}
}

