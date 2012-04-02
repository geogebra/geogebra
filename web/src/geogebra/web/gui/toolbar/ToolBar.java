package geogebra.web.gui.toolbar;

import java.util.Vector;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

import com.google.gwt.user.client.ui.MenuBar;



/**
 * @author gabor
 * 
 * Toolbar for GeoGebraWeb
 *
 */
public class ToolBar extends MenuBar {
	
	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = new Integer(-1);
	
	private Application app;
	private int mode;

	public ToolBar(Application app) {
		this.app = app;
	}
	
	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		mode = -1;
		clearItems();
		
		addCustomModesToToolbar();
		
	}
	
	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 */
	private void addCustomModesToToolbar() {
		Vector<Object> toolbarVec;
		
		try {
			//AGif (dockPanel != null) {
			//AG	toolbarVec = parseToolbarString(dockPanel.getToolbarString());
			//AG} else {
				toolbarVec = parseToolbarString(app.getGuiManager()
						.getToolbarDefinition());
			//AG}
		} catch (Exception e) {
			//AGif (dockPanel != null) {
			//AG	AbstractApplication.debug("invalid toolbar string: "
			//AG			+ dockPanel.getToolbarString());
			//AG} else {
				AbstractApplication.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = parseToolbarString(getDefaultToolbarString());
		//AG}
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
		//AGif (dockPanel != null) {
		//AG	return dockPanel.getDefaultToolbarString();
		//AG}
		return ToolBar.getAllTools(app);
	}

	

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(Application app) {
		StringBuilder sb = new StringBuilder();
	
		sb.append(geogebra.common.gui.toolbar.ToolBar.getAllToolsNoMacros());
	
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
