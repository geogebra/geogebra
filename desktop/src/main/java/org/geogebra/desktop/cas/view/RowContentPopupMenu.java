package org.geogebra.desktop.cas.view;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.ScaledIcon;

/**
 * Provides a popup menu for copying the text of a {@link GeoCasCell} to the
 * clipboard or to LaTeX.
 * 
 * @author Johannes Renner
 */
public class RowContentPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final GeoCasCell value;
	private final CASTableCellEditorD editor;
	private final CASTableD table;
	private final AppD app;

	public enum Panel {
		OUTPUT, INPUT
	}

	private final Panel clickedPanel;

	/**
	 * initializes the menu
	 * 
	 * @param value
	 *            the {@link GeoCasCell} containing the value to copy
	 * @param table
	 *            needed to get the {@link AppD}
	 */
	public RowContentPopupMenu(AppD app, GeoCasCell value,
			CASTableCellEditorD editor, CASTableD table) {
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = Panel.OUTPUT;

		initMenu();
	}

	/**
	 * @param app application
	 * @param value CAS cell
	 * @param editor editor
	 * @param table CAS table
	 * @param clickedPanel clicked panel
	 */
	public RowContentPopupMenu(AppD app, GeoCasCell value,
			CASTableCellEditorD editor, CASTableD table, Panel clickedPanel) {
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = clickedPanel;

		initMenu();
	}

	private void initMenu() {
		Localization loc = table.getApplication().getLocalization();
		switch (clickedPanel) {
		case OUTPUT:
			JMenuItem copyItem = new JMenuItem(
					loc.getMenu("Copy"));
			copyItem.setActionCommand("copy");
			copyItem.addActionListener(this);
			add(copyItem);
			addSeparator();

			JMenuItem copyToLatexItem = new JMenuItem(
					loc.getMenu("CopyAsLaTeX"));
			copyToLatexItem.setActionCommand("copyAsLatex");
			copyToLatexItem.addActionListener(this);
			add(copyToLatexItem);

			JMenuItem copyToLibreOfficeItem = new JMenuItem(
					loc.getMenu("CopyAsLibreOfficeFormula"));
			copyToLibreOfficeItem.setActionCommand("copyAsLibreOfficeMath");
			copyToLibreOfficeItem.addActionListener(this);
			add(copyToLibreOfficeItem);

			JMenuItem copyToImageItem = new JMenuItem(
					loc.getMenu("CopyAsImage"));
			copyToImageItem.setActionCommand("copyAsImage");
			copyToImageItem.addActionListener(this);
			add(copyToImageItem);
			break;
		case INPUT:
			JMenuItem pasteItem = new JMenuItem(
					loc.getMenu("Paste"));
			pasteItem.setActionCommand("paste");
			pasteItem.addActionListener(this);
			add(pasteItem);
			break;
		}
	}

	/**
	 * handles the {@link ActionEvent}s
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		handleCopy(e);
		handlePaste(e);
	}

	private void handleCopy(ActionEvent e) {
		String ac = e.getActionCommand();

		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = null;

		if ("copy".equals(ac)) {
			data = new StringSelection(
					// use xml template so that sin(2x) -> sin(2*x)
					// so that it can be pasted into other software
					value.getOutput(StringTemplate.casCopyTemplate));

		} else if ("copyAsLatex".equals(ac)) {
			String latexOutput = value.getLaTeXOutput(false);
			data = new StringSelection(
					StringUtil.toLaTeXString(latexOutput, true));

		} else if ("copyAsLibreOfficeMath".equals(ac)) {
			String libreofficeOutput = value
					.getOutput(StringTemplate.libreofficeTemplate);
			data = new StringSelection(libreofficeOutput);

		} else if ("copyAsImage".equals(ac)) {
			ScaledIcon latexIcon = new ScaledIcon(this);
			Font latexFont = new Font(app.getPlainFont().getName(),
					app.getPlainFont().getStyle(),
					app.getPlainFont().getSize() - 1);

			app.getDrawEquation().drawLatexImageIcon(app, latexIcon,
					value.getLaTeXOutput(), latexFont, false,
					GColorD.getAwtColor(value.getAlgebraColor()),
					table.getBackground());

			data = new ImageSelection(latexIcon.getImage());
		}

		if (data != null) {
			sysClip.setContents(data, null);
		}
	}

	private void handlePaste(ActionEvent e) {
		String ac = e.getActionCommand();
		String data = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

		if ("paste".equals(ac)) {
			Transferable contents = sysClip.getContents(null);
			boolean hasTransferableText = (contents != null)
					&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (hasTransferableText) {
				try {
					data = (String) contents
							.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception ex) {
					Log.debug(ex);
				}
			}
			editor.insertText(data);
			app.storeUndoInfo();
		}
	}
}
