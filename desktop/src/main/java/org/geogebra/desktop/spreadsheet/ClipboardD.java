package org.geogebra.desktop.spreadsheet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.desktop.gui.view.spreadsheet.DataImportD;

public class ClipboardD implements ClipboardInterface {

	private final Clipboard clipboard;

	public ClipboardD() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		clipboard = toolkit.getSystemClipboard();

	}

	@Override
	public String getContent() {
		Transferable contents = clipboard.getContents(null);
		return DataImportD.convertTransferableToString(contents);
	}

	@Override
	public void setContent(String content) {
		clipboard.setContents(new StringSelection(content), null);
	}
}
