package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

public class SpreadsheetControlsDelegateW implements
		org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate {
	private AppW appW;

	public SpreadsheetControlsDelegateW(AppW appW) {
		this.appW = appW;
	}
	@Override
	public SpreadsheetCellEditor getCellEditor() {
		return null;
	}

	@Override
	public void showContextMenu(List<ContextMenuItem> actions, GPoint coords) {
		GPopupMenuW contextMenu = new GPopupMenuW(appW);
		for (ContextMenuItem item : actions) {
			contextMenu.addItem(new AriaMenuItem(appW.getLocalization()
					.getMenu(item.getLocalizationKey()), false, () -> item.performAction()));
		}
		contextMenu.showAtPoint(coords.x, coords.y);
	}

	@Override
	public void hideCellEditor() {

	}

	@Override
	public void hideContextMenu() {

	}

	@Override
	public ClipboardInterface getClipboard() {
		return null;
	}
}
