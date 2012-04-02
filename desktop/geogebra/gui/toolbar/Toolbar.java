/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.toolbar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JToolBar;

/**
 * Base class for a single toolbar, either for a dock panel or as a general
 * toolbar. Toolbars are always visible as part of a ToolbarContainer.
 */
public class Toolbar extends JToolBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = new Integer(-1);

	/**
	 * Instance of the application.
	 */
	private Application app;

	/**
	 * Dock panel associated to this toolbar or null if this is the general
	 * toolbar. Just a single toolbar might have no dock panel, otherwise the
	 * ToolbarContainer logic will not work properly.
	 */
	private DockPanel dockPanel;

	/**
	 * The mode selected at the moment.
	 */
	private int mode;

	private ArrayList<ModeToggleMenu> modeToggleMenus;

	/**
	 * Creates general toolbar.
	 * 
	 * @param app
	 */
	public Toolbar(Application app) {
		this(app, null);
	}

	/**
	 * Creates toolbar for a specific dock panel. Call buildGui() to actually
	 * create the GUI of this toolbar.
	 * 
	 * @param app
	 * @param dockPanel
	 */
	public Toolbar(Application app, DockPanel dockPanel) {
		this.app = app;
		this.dockPanel = dockPanel;

		setFloatable(false);
		setBackground(getBackground());
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		mode = -1;

		ModeToggleButtonGroup bg = new ModeToggleButtonGroup();
		modeToggleMenus = new ArrayList<ModeToggleMenu>();

		// create toolbar
		removeAll();

		setAlignmentX(LEFT_ALIGNMENT);

		// add menus with modes to toolbar
		addCustomModesToToolbar(bg);

		setMode(app.getMode());
	}

	/**
	 * Sets toolbar mode. This will change the selected toolbar icon.
	 * @param mode see EuclidianConstants for mode numbers
	 * 
	 * @param int mode Mode to set
	 * 
	 * @return actual mode number selected (might be different if it's not available)
	 */
	public int setMode(int mode) {
		boolean success = false;

		// there is no special icon/button for the selection listener mode, use
		// the
		// move mode button instead
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			mode = EuclidianConstants.MODE_MOVE;
		}

		if (modeToggleMenus != null) {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				ModeToggleMenu mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(mode)) {
					success = true;
					break;
				}
			}


			if (!success) {
					mode = setMode(getFirstMode());
				
			}
			
			this.mode = mode;

		}

		return mode;
	}

	public int getSelectedMode() {
		return mode;
	}

	public int getFirstMode() {
		if (modeToggleMenus == null || modeToggleMenus.size() == 0) {
			return -1;
		}
		ModeToggleMenu mtm = modeToggleMenus.get(0);
		return mtm.getFirstMode();
	}

	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 * @param modes
	 * @param tb
	 * @param bg
	 */
	@SuppressWarnings("unchecked")
	private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {
		Vector<Object> toolbarVec;
		try {
			if (dockPanel != null) {
				toolbarVec = parseToolbarString(dockPanel.getToolbarString());
			} else {
				toolbarVec = parseToolbarString(app.getGuiManager()
						.getToolbarDefinition());
			}
		} catch (Exception e) {
			if (dockPanel != null) {
				AbstractApplication.debug("invalid toolbar string: "
						+ dockPanel.getToolbarString());
			} else {
				AbstractApplication.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = parseToolbarString(getDefaultToolbarString());
		}

		// set toolbar
		boolean firstButton = true;
		for (int i = 0; i < toolbarVec.size(); i++) {
			Object ob = toolbarVec.get(i);

			// separator between menus
			if (ob instanceof Integer) {
				addSeparator();
				continue;
			}

			// new menu
			Vector<Integer> menu = (Vector<Integer>) ob;
			ModeToggleMenu tm = new ModeToggleMenu(app, this, bg);
			modeToggleMenus.add(tm);

			for (int k = 0; k < menu.size(); k++) {
				// separator
				int mode = menu.get(k).intValue();
				if (mode < 0) {
					// separator within menu:
					tm.addSeparator();
				} else { // standard case: add mode

					// check mode
					if (!"".equals(app.getToolName(mode))) {
						tm.addMode(mode);
						if (firstButton) {
							tm.getJToggleButton().setSelected(true);
							firstButton = false;
						}
					}
				}
			}

			if (tm.getToolsCount() > 0)
				add(tm);
		}
	}

	/**
	 * @return The dock panel associated with this toolbar or null if this is
	 *         the general toolbar.
	 */
	public DockPanel getDockPanel() {
		return dockPanel;
	}

	/**
	 * @return The top-most panel of the window this toolbar belongs to.
	 */
	public Component getMainComponent() {
		// if this is the general toolbar the main component is the application
		// main
		// component (not true for toolbars in EV)
		if (dockPanel == null) {
			return app.getMainComponent();
		}

		// this toolbar belongs to a dock panel
		// in frame?
		if (dockPanel.isOpenInFrame()) {
			return dockPanel;
		}

		// otherwise use the application main component
		return app.getMainComponent();
	}

	/**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9" where
	 * the int values are mode numbers, "," adds a separator within a menu, "|"
	 * starts a new menu and "||" adds a separator before starting a new menu.
	 * 
	 * @param toolbarString
	 *            toolbar definition string
	 * 
	 * @return toolbar as nested Vector objects with Integers for the modes.
	 *         Note: separators have negative values.
	 */
	public static Vector<Object> parseToolbarString(String toolbarString) {
		String[] tokens = toolbarString.split(" ");
		Vector<Object> toolbar = new Vector<Object>();
		Vector<Integer> menu = new Vector<Integer>();

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("|")) { // start new menu
				if (menu.size() > 0)
					toolbar.add(menu);
				menu = new Vector<Integer>();
			} else if (tokens[i].equals("||")) { // separator between menus
				if (menu.size() > 0)
					toolbar.add(menu);

				// add separator between two menus
				// menu = new Vector();
				// menu.add(SEPARATOR);
				// toolbar.add(menu);
				toolbar.add(SEPARATOR);

				// start next menu
				menu = new Vector<Integer>();
			} else if (tokens[i].equals(",")) { // separator within menu
				menu.add(SEPARATOR);
			} else { // add mode to menu
				try {
					if (tokens[i].length() > 0) {
						int mode = Integer.parseInt(tokens[i]);
						menu.add(new Integer(mode));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		// add last menu to toolbar
		if (menu.size() > 0)
			toolbar.add(menu);
		return toolbar;
	}

	/**
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		if (dockPanel != null) {
			return dockPanel.getDefaultToolbarString();
		}
		return Toolbar.getAllTools(app);
	}

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(Application app) {
		StringBuilder sb = new StringBuilder();

		sb.append(ToolBar.getAllToolsNoMacros());

		// macros
		Kernel kernel = app.getKernel();
		int macroNumber = kernel.getMacroNumber();

		// check if at least one macro is shown
		// to avoid strange GUI
		boolean at_least_one_shown = false;
		for (int i = 0; i < macroNumber; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.isShowInToolBar()) {
				at_least_one_shown = true;
				break;
			}
		}

		if (macroNumber > 0 && at_least_one_shown) {
			sb.append(" || ");
			int count = 0;
			for (int i = 0; i < macroNumber; i++) {
				Macro macro = kernel.getMacro(i);
				if (macro.isShowInToolBar()) {
					count++;
					sb.append(i + EuclidianConstants.MACRO_MODE_ID_OFFSET);
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}
}
