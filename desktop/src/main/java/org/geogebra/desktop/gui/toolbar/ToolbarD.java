/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.toolbar;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JToolBar;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

/**
 * Base class for a single toolbar, either for a dock panel or as a general
 * toolbar. Toolbars are always visible as part of a ToolbarContainer.
 */
public class ToolbarD extends JToolBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Instance of the application.
	 */
	private AppD app;

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

	private ArrayList<ModeToggleMenuD> modeToggleMenus;

	/**
	 * Creates general toolbar.
	 * 
	 * @param app
	 *            application
	 */
	public ToolbarD(AppD app) {
		this(app, null);
	}

	/**
	 * Creates toolbar for a specific dock panel. Call buildGui() to actually
	 * create the GUI of this toolbar.
	 * 
	 * @param app
	 *            application
	 * @param dockPanel
	 *            dock panel
	 */
	public ToolbarD(AppD app, DockPanel dockPanel) {
		this.app = app;
		this.dockPanel = dockPanel;

		setFloatable(false);
		setBackground(getBackground());

		// setLayout(new GridLayout(0, 2));
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {

		mode = -1;

		ModeToggleButtonGroup bg = new ModeToggleButtonGroup();
		modeToggleMenus = new ArrayList<>();

		// create toolbar
		removeAll();

		// use specific layout for 3D inputs requiring huge GUI
		if (app.useHugeGuiForInput3D()) {
			setLayout(new GridLayout(0, 2));
		}

		setAlignmentX(LEFT_ALIGNMENT);

		// add menus with modes to toolbar
		addCustomModesToToolbar(bg);

		setMode(app.getMode());
	}

	/**
	 * Sets toolbar mode. This will change the selected toolbar icon.
	 * 
	 * @param newMode
	 *            see EuclidianConstants for mode numbers
	 * 
	 * 
	 * @return actual mode number selected (might be different if it's not
	 *         available)
	 */
	public int setMode(int newMode) {
		boolean success = false;
		int tmpMode = newMode;
		// there is no special icon/button for the selection listener mode, use
		// the
		// move mode button instead
		if (tmpMode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			tmpMode = EuclidianConstants.MODE_MOVE;
		}

		if (modeToggleMenus != null) {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				ModeToggleMenuD mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(tmpMode)) {
					success = true;
					break;
				}
			}

			if (!success) {
				int firstMode = getFirstMode();
				// in case there are no tools!
				if (firstMode > -1) {
					mode = setMode(getFirstMode());
				}

			}

			this.mode = tmpMode;

		}

		return tmpMode;
	}

	/**
	 * @return currently selected mode
	 */
	public int getSelectedMode() {
		return mode;
	}

	/**
	 * @return first mode in this toolbar
	 */
	public int getFirstMode() {
		if (modeToggleMenus == null || modeToggleMenus.size() == 0) {
			return -1;
		}
		ModeToggleMenuD mtm = modeToggleMenus.get(0);
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
	private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {
		Vector<ToolbarItem> toolbarVec;
		try {
			if (dockPanel != null) {
				toolbarVec = ToolBar
						.parseToolbarString(dockPanel.getToolbarString());
			} else {
				toolbarVec = ToolBar.parseToolbarString(
						app.getGuiManager().getToolbarDefinition());
			}
		} catch (Exception e) {
			if (dockPanel != null) {
				Log.debug("invalid toolbar string: "
						+ dockPanel.getToolbarString());
			} else {
				Log.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = ToolBar.parseToolbarString(getDefaultToolbarString());
		}

		// set toolbar
		boolean firstButton = true;

		// make the loop go backwards for eg Hebrew / Arabic
		int first = app.getLocalization().isRightToLeftReadingOrder()
				? toolbarVec.size() - 1 : 0;
		int increment = app.getLocalization().isRightToLeftReadingOrder() ? -1
				: 1;

		beginAdd();

		// for (int i = 0; i < toolbarVec.size(); i++) {
		for (int i = first; i >= 0 && i < toolbarVec.size(); i += increment) {
			ToolbarItem ob = toolbarVec.get(i);

			// new menu
			Vector<Integer> menu = ob.getMenu();
			ModeToggleMenuD tm = new ModeToggleMenuD(app, this, bg);
			modeToggleMenus.add(tm);

			for (int k = 0; k < menu.size(); k++) {
				// separator
				int addMode = menu.get(k).intValue();
				if (addMode < 0) {
					// separator within menu:
					tm.addSeparator();
				} else { // standard case: add mode

					// check mode
					if (!"".equals(app.getToolName(addMode))) {
						tm.addMode(addMode);
						if (i == 0 && firstButton) {
							tm.getJToggleButton().setSelected(true);
							firstButton = false;
						}
					}
				}
			}

			if (tm.getToolsCount() > 0) {
				add(tm);
			}
		}

		endAdd();

	}

	private ArrayList<Component> componentsToAdd;

	private void beginAdd() {
		if (app.useHugeGuiForInput3D()) {
			if (componentsToAdd == null) {
				componentsToAdd = new ArrayList<>();
			}
		}
	}

	@Override
	public Component add(Component c) {
		if (app.useHugeGuiForInput3D()) {
			componentsToAdd.add(c);
			return c;
		}

		return super.add(c);
	}

	private void endAdd() {
		if (app.useHugeGuiForInput3D()) {
			int size = componentsToAdd.size();
			int halfSize = size / 2;
			for (int i = 0; i < halfSize; i++) {
				super.add(componentsToAdd.get(i));
				if (halfSize + i < size) {
					super.add(componentsToAdd.get(halfSize + i));
				}
			}
			componentsToAdd.clear();
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
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		if (dockPanel != null) {
			return dockPanel.getDefaultToolbarString();
		}
		return ToolbarD.getAllTools(app);
	}

	/**
	 * @param app
	 *            application
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(AppD app) {
		StringBuilder sb = new StringBuilder();

		sb.append(ToolBar.getAllToolsNoMacros(false, false, app));

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
			for (int i = 0; i < macroNumber; i++) {
				Macro macro = kernel.getMacro(i);
				if (macro.isShowInToolBar()) {
					sb.append(i + EuclidianConstants.MACRO_MODE_ID_OFFSET);
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}

	/**
	 * @return true when tooltips are suppressed
	 */
	protected boolean preventToolTipDelay() {
		return !app.showToolBarHelp();
	}

}
