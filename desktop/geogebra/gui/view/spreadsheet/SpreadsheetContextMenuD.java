package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import geogebra.main.AppD;

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

	private Color bgColor = Color.white;
	private Color fgColor = Color.black;

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
		return isEmptySelection() && AppD.hasFullPermissions();
	}

	/**
	 * Import data file
	 */
	@Override
	public void cmdImportDataFile() {

		File dataFile = ((AppD) app).getGuiManager().getDataFile();
		if (dataFile != null) {
			((SpreadsheetView) table.getView())
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
	public void addMenuItem(final String cmdString, String text, boolean enabled) {
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
	public void addSubMenuItem(Object menu, final String cmdString,
			String text, boolean enabled) {
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

	private void addSubItem(Object menu, Object item) {
		Component mi = (Component) item;
		mi.setBackground(bgColor);
		((JMenu) menu).add(mi);
	}

	private ImageIcon getIcon(String cmdString) {

		if (cmdString == null) {
			return ((AppD) app).getEmptyIcon();
		}

		String iconString = null;

		switch (MenuCommand.valueOf(cmdString)) {
		case Copy:
			iconString = "menu-edit-copy.png";
			break;
		case Cut:
			iconString = "menu-edit-cut.png";
			break;
		case Paste:
			iconString = "menu-edit-paste.png";
			break;
		case Delete:
		case DeleteObjects:
			iconString = "delete_small.gif";
			break;
		case ShowObject:
			iconString = "mode_showhideobject_16.gif";
			break;
		case ShowLabel:
			iconString = "mode_showhidelabel_16.gif";
			break;
		case RecordToSpreadsheet:
			iconString = "spreadsheettrace.gif";
			break;
		case Properties:
			iconString = "view-properties16.png";
			break;
		case SpreadsheetOptions:
			iconString = "view-properties16.png";
			break;
		}

		// convert string to icon
		if (iconString != null) {
			return ((AppD) app).getScaledIcon(iconString);
		}
		return ((AppD) app).getEmptyIcon();
	}

}