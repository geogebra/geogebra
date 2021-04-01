package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * Subclass of SpreadsheetContextMenu, implements the spreadsheet context menu
 * for desktop.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuD extends SpreadsheetContextMenu {

	/** Desktop popUp panel */
	protected JPopupMenu popup;

	private static final Color bgColor = Color.white;
	private static final Color fgColor = Color.black;

	/**
	 * Constructor
	 * 
	 * @param table
	 *            spreadsheet table
	 */
	public SpreadsheetContextMenuD(MyTable table) {
		super(table);
	}

	/**
	 * @return true if data file can be read locally
	 */
	@Override
	public boolean enableDataImport() {
		return isEmptySelection();
	}

	/**
	 * Import data file
	 */
	@Override
	public void cmdImportDataFile() {

		File dataFile = ((AppD) app).getGuiManager().getDataFile();
		if (dataFile != null) {
			((SpreadsheetViewD) table.getView())
					.loadSpreadsheetFromURL(dataFile);
		}
	}

	// ======================================
	// GUI implementation
	// ======================================

	@Override
	public Object getMenuContainer() {
		return popup;
	}

	@Override
	public void createGUI() {
		popup = new JPopupMenu();
		popup.setBackground(bgColor);
		((AppD) app).setComponentOrientation(popup);
		initMenu();

	}

	// setTitle (copied from gui.ContextMenuGeoElement)
	@Override
	public void setTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(((AppD) app).getBoldFont());
		title.setBackground(bgColor);
		title.setForeground(fgColor);

		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));
		popup.add(title);

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popup.setVisible(false);
			}
		});

	}

	@Override
	public void addMenuItem(final String cmdString, String text,
			boolean enabled) {
		JMenuItem item = new JMenuItem(text);
		item.setIcon(getIcon(cmdString));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCommand(cmdString);
			}
		});
		item.setEnabled(enabled);
		addItem(item);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
			boolean isSelected) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
		item.setIcon(getIcon(cmdString));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCommand(cmdString);
			}
		});
		item.setSelected(isSelected);
		addItem(item);
	}

	@Override
	public Object addSubMenu(String text, String cmdString) {
		JMenu menu = new JMenu(text);
		menu.setIcon(getIcon(cmdString));
		addItem(menu);
		return menu;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString, String text,
			boolean enabled) {
		JMenuItem item = new JMenuItem(text, getIcon(cmdString));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCommand(cmdString);
			}
		});
		item.setEnabled(enabled);
		addSubItem(menu, item);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
	}

	private void addItem(Object item) {
		Component mi = (Component) item;
		mi.setBackground(bgColor);
		popup.add(mi);
	}

	private static void addSubItem(Object menu, Object item) {
		Component mi = (Component) item;
		mi.setBackground(bgColor);
		((JMenu) menu).add(mi);
	}

	private ImageIcon getIcon(String cmdString) {

		if (cmdString == null) {
			return ((AppD) app).getEmptyIcon();
		}

		ImageResourceD iconString = null;

		switch (MenuCommand.valueOf(cmdString)) {
		default:
			// do nothing
			break;
		case Copy:
			iconString = GuiResourcesD.MENU_EDIT_COPY;
			break;
		case Cut:
			iconString = GuiResourcesD.MENU_EDIT_CUT;
			break;
		case Paste:
			iconString = GuiResourcesD.MENU_EDIT_PASTE;
			break;
		case Delete:
		case DeleteObjects:
			iconString = GuiResourcesD.DELETE_SMALL;
			break;
		case ShowObject:
			iconString = GuiResourcesD.MODE_SHOWHIDEOBJECT_GIF;
			break;
		case ShowLabel:
			iconString = GuiResourcesD.MODE_SHOWHIDELABEL;
			break;
		case RecordToSpreadsheet:
			iconString = GuiResourcesD.SPREADSHEETTRACE;
			break;
		case Properties:
			iconString = GuiResourcesD.VIEW_PROPERTIES_16;
			break;
		case SpreadsheetOptions:
			iconString = GuiResourcesD.VIEW_PROPERTIES_16;
			break;
		}

		// convert string to icon
		if (iconString != null) {
			return ((AppD) app).getScaledIcon(iconString);
		}
		return ((AppD) app).getEmptyIcon();
	}

}