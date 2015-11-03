package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW;
import org.geogebra.web.web.html5.AttachedToDOM;
//import org.geogebra.web.html5.gui.util.ImageSelection;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;

public class RowContentPopupMenuW extends GPopupMenuW implements AttachedToDOM {

	private static final long serialVersionUID = 1L;

	private final GeoCasCell value;
	private final CASTableCellEditor editor;
	private final CASTableW table;
	private final AppW app;

	public enum Panel {
		OUTPUT, INPUT
	}

	private Panel clickedPanel;

	/**
	 * initializes the menu
	 * 
	 * @param value
	 *            the {@link GeoCasCell} containing the value to copy
	 * @param table
	 *            needed to get the {@link AppD}
	 */
	public RowContentPopupMenuW(AppW app, GeoCasCell value,
			CASTableCellEditor editor, CASTableW table) {
		super(app);
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = Panel.OUTPUT;

		initMenu();
	}

	public RowContentPopupMenuW(AppW app, GeoCasCell value,
			CASTableCellEditor editor, CASTableW table, Panel clickedPanel) {
		super(app);
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = clickedPanel;

		initMenu();
	}

	private void initMenu() {
		switch (clickedPanel) {
		case OUTPUT:
			MenuItem copyItem = new MenuItem(app.getMenu("Copy"),
					new ScheduledCommand() {
						public void execute() {
							actionPerformed("copy");
						}
					});
			addItem(copyItem);
			copyItem.addStyleName("mi_no_image");

			addSeparator();

			MenuItem copyToLatexItem = new MenuItem(app.getMenu("CopyAsLaTeX"),
					new ScheduledCommand() {
						public void execute() {
							// ??
							// actionPerformed(app.getMenu("copyAsLatex"));
							actionPerformed("copyAsLatex");
						}
					});
			addItem(copyToLatexItem);
			copyToLatexItem.addStyleName("mi_no_image");
			break;
		case INPUT:
			// not implemented yet
			/*JMenuItem pasteItem = new JMenuItem(table.getApplication().getMenu(
					"Paste"));
			pasteItem.setActionCommand("paste");
			pasteItem.addActionListener(this);
			add(pasteItem);*/
			break;
		}
	}

	/**
	 * handles the {@link ActionEvent}s
	 */
	public void actionPerformed(String ac) {
		handleCopy(ac);
		// handlePaste(e);
	}

	private void handleCopy(String ac) {

		String toBeCopied = null;

		if (value != null) {
			if (ac.equals("copy")) {
				toBeCopied = value.getOutput(StringTemplate.xmlTemplate);
				// use xmlTemplate so that sin(2x) -> sin(2*x)
				// so that it can be pasted into other software
			} else if (ac.equals("copyAsLatex")) {
				String latexOutput = value.getLaTeXOutput();
				toBeCopied = StringUtil.toLaTeXString(latexOutput, true);
			}
		}

		// HOW to put the data toBeCopied to Web clipboard?
		if (toBeCopied != null) {
			CopyPasteCutW.copyToSystemClipboard(toBeCopied);
		}
	}



	/*private void handlePaste(ActionEvent e) {
		String ac = e.getActionCommand();
		String data = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (ac.equals("paste")) {
			Transferable contents = sysClip.getContents(null);
			boolean hasTransferableText = (contents != null)
					&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (hasTransferableText) {
				try {
					data = (String) contents
							.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception ex) {
					System.out.println(ex);
					ex.printStackTrace();
				}
			}
			editor.insertText(data);
			app.storeUndoInfo();
		}
	}*/
}
