package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.Application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private final Application app;

	/**
	 * initializes the menu
	 * @param value the {@link GeoCasCell} containing the value to copy
	 * @param table needed to get the {@link Application}
	 */
	public RowContentPopupMenu(GeoCasCell value, CASTable table) {
		this.value = value;
		app = table.app;

		initMenu();
	}

	private void initMenu() {
		JMenuItem copyItem = new JMenuItem(app.getMenu("Copy"));
		copyItem.setActionCommand("copy");
		copyItem.addActionListener(this);
		add(copyItem);

		JMenuItem copyToLaTeXItem = new JMenuItem(app.getMenu("CopyToLaTeX"));
		copyToLaTeXItem.setActionCommand("copyToLaTeX");
		copyToLaTeXItem.addActionListener(this);
		add(copyToLaTeXItem);
	}

	/**
	 * handles the {@link ActionEvent}s
	 */
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();

		Clipboard sysClip = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		Transferable data = null;
		
		if (ac.equals("copy")) {
			data = new StringSelection(value.toOutputValueString());
		} else if (ac.equals("copyToLaTeX")) {
			data = new StringSelection(value.getLaTeXOutput());
		} 
		
		if(data == null) {
			throw new NullPointerException("Transferable data is null");
		}
			
		sysClip.setContents(data, null);
	}
}
