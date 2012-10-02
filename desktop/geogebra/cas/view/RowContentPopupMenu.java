package geogebra.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.util.StringUtil;
import geogebra.gui.util.ImageSelection;
import geogebra.main.AppD;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
	
	public enum Panel { OUTPUT, INPUT}
	private Panel clickedPanel;

	/**
	 * initializes the menu
	 * 
	 * @param value
	 *            the {@link GeoCasCell} containing the value to copy
	 * @param table
	 *            needed to get the {@link AppD}
	 */
	public RowContentPopupMenu(AppD app, GeoCasCell value, CASTableCellEditorD editor , CASTableD table) {
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = Panel.OUTPUT;
		
		initMenu();
	}
	
	public RowContentPopupMenu(AppD app, GeoCasCell value, CASTableCellEditorD editor, CASTableD table, Panel clickedPanel) {
		this.value = value;
		this.table = table;
		this.editor = editor;
		this.app = app;
		this.clickedPanel = clickedPanel;

		initMenu();
	}

	private void initMenu() {
		switch(clickedPanel) {
		case OUTPUT:
			JMenuItem copyItem = new JMenuItem(table.getApplication().getMenu("Copy"));
			copyItem.setActionCommand("copy");
			copyItem.addActionListener(this);
			add(copyItem);
			addSeparator();
	
			JMenuItem copyToLatexItem = new JMenuItem(
					table.getApplication().getMenu("CopyAsLaTeX"));
			copyToLatexItem.setActionCommand("copyAsLatex");
			copyToLatexItem.addActionListener(this);
			add(copyToLatexItem);
			
			JMenuItem copyToLibreOfficeItem = new JMenuItem(
					table.getApplication().getMenu("CopyAsLibreOfficeFormula"));
			copyToLibreOfficeItem.setActionCommand("copyAsLibreOfficeMath");
			copyToLibreOfficeItem.addActionListener(this);
			add(copyToLibreOfficeItem);
	
			JMenuItem copyToImageItem = new JMenuItem(
					table.getApplication().getMenu("CopyAsImage"));
			copyToImageItem.setActionCommand("copyAsImage");
			copyToImageItem.addActionListener(this);
			add(copyToImageItem);
			break;
		case INPUT:
			JMenuItem pasteItem = new JMenuItem(table.getApplication().getMenu("Paste"));
			pasteItem.setActionCommand("paste");
			pasteItem.addActionListener(this);
			add(pasteItem);
			break;
		}
	}

	/**
	 * handles the {@link ActionEvent}s
	 */
	public void actionPerformed(ActionEvent e) {
		handleCopy(e);
		handlePaste(e);
	}
	
	private void handleCopy(ActionEvent e){
		String ac = e.getActionCommand();

		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = null;

		if (ac.equals("copy")) {
			data = new StringSelection(
					value.getOutput(StringTemplate.defaultTemplate));
		} else if (ac.equals("copyAsLatex")) {
			String latexOutput = value.getLaTeXOutput();
			data = new StringSelection(StringUtil.toLaTeXString(latexOutput,
					true));
			
		} else if (ac.equals("copyAsLibreOfficeMath")) {
			String libreofficeOutput = value.getOutput(StringTemplate.libreofficeTemplate);
			data = new StringSelection(libreofficeOutput);
			
		}else if (ac.equals("copyAsImage")) {
			ImageIcon latexIcon = new ImageIcon();
			AppD app = (AppD)table.getApplication();
			Font latexFont = new Font(app.getPlainFont().getName(),
					app.getPlainFont().getStyle(), app
							.getPlainFont().getSize() - 1);

			app.getDrawEquation().drawLatexImageIcon(app,
					latexIcon, value.getLaTeXOutput(), latexFont, false,
					geogebra.awt.GColorD.getAwtColor(value.getAlgebraColor()), table.getBackground());

			data = new ImageSelection(latexIcon.getImage());
		}
		
		if (data != null) {
			sysClip.setContents(data, null);
		}		
	}
	
	private void handlePaste(ActionEvent e){
		String ac = e.getActionCommand();
		String data = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		if(ac.equals("paste")){
			Transferable contents = sysClip.getContents(null);
		    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		    if ( hasTransferableText ) {
		      try {
		        data = (String)contents.getTransferData(DataFlavor.stringFlavor);
		      }
		      catch (Exception ex) {
		        System.out.println(ex);
		        ex.printStackTrace();
		      }
		    }
		    editor.insertText(data);
			app.storeUndoInfo();
		}
	}
}
