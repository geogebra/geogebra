package geogebra.web.gui.toolbar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.gui.toolbar.ToolbarItem;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;



/**
 * @author gabor
 * 
 * Toolbar for GeoGebraWeb
 *
 */
public class ToolBarW extends FlowPanel{
	
	private AppW app;
	private int mode;

	/**
	 * Dock panel associated to this toolbar or null if this is the general
	 * toolbar. Just a single toolbar might have no dock panel, otherwise the
	 * ToolbarContainer logic will not work properly.
	 */
	private DockPanel dockPanel;

	private ArrayList<ModeToggleMenu> modeToggleMenus;
	boolean keepDown;
	private UnorderedList menuList;

	/**
	 * Creates general toolbar.
	 * There is no app parameter here, because of UiBinder.
	 * After instantiate the ToolBar, call init(Application app) as well.
	 */
	public ToolBarW() {
		this.addStyleName("GGWToolbar");
	}

	/**
	 * Creates toolbar for a specific dock panel. Call buildGui() to actually
	 * create the GUI of this toolbar.
	 * 
	 * @param app application
	 * @param dockPanel dock panel
	 */
/*	public ToolBarW(AppW app, DockPanel dockPanel) {
		this();
		this.app = app;
		this.dockPanel = dockPanel;

		//setFloatable(false);
		//setBackground(getBackground());
	}
*/

	/**
	 * Initialization of the ToolBar object
	 * 
	 * @param app1 application
	 */
	public void init(AppW app1){
		this.app = app1;
	}

	/**
	 * @return The dock panel associated with this toolbar or null if this is
	 *         the general toolbar.
	 */
	public DockPanel getDockPanel() {
		App.debug("ToolBarW.getDockPanel");
		return dockPanel;
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		App.debug("ToolBarW.buildGui");
		mode = -1;
	
		menuList = new UnorderedList();
		menuList.getElement().addClassName("toolbar_mainItem");
		modeToggleMenus = new ArrayList<ModeToggleMenu>();
		addCustomModesToToolbar(menuList);
		
		this.clear();
		this.add(menuList);

		setMode(app.getMode());
//		update();
	}
	
	//TODO: this function is just a temporary hack! Don't regenate the toolbar.
	public void update(){
		this.clear();
		int count = menuList.getWidgetCount();
		menuList.clear();
		for(int i=0; i<count; i++){
			menuList.add(modeToggleMenus.get(i));
		}
		this.add(menuList);
	}
	
	public ArrayList<ModeToggleMenu> getModeToggleMenus(){
		return modeToggleMenus;
	}
	
	/**
	 * Sets toolbar mode. This will change the selected toolbar icon.
	 * @param newMode see EuclidianConstants for mode numbers
	 * 
	 * 
	 * @return actual mode number selected (might be different if it's not available)
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
				ModeToggleMenu mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(tmpMode)) {
					success = true;
					break;
				}
			}


			if (!success) {
					mode = setMode(getFirstMode());
				
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
		ModeToggleMenu mtm = modeToggleMenus.get(0);
		return mtm.getFirstMode();
	}
	
	public UnorderedList getMenuList(){
		return menuList;
	}

	private Integer activeView = App.VIEW_EUCLIDIAN;
	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 */
	//private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {
	private void addCustomModesToToolbar(UnorderedList mainUl) {
		App.debug("ToolBarW.addCustomModesToToolbar");
		Vector<ToolbarItem> toolbarVec;
		
		try {
			if (dockPanel != null) {
				toolbarVec = ToolBar.parseToolbarString(dockPanel.getToolbarString());
			} else {
				toolbarVec = ToolBar.parseToolbarString(app.getGuiManager()
						.getToolbarDefinition());
			}
		} catch (Exception e) {
			if (dockPanel != null) {
				App.debug("invalid toolbar string: "
						+ dockPanel.getToolbarString());
			} else {
				App.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = ToolBar.parseToolbarString(getDefaultToolbarString());
		}
		
		// set toolbar
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			
			ModeToggleMenu mtm = new ModeToggleMenu(app, menu, this);
			modeToggleMenus.add(mtm);
			mainUl.add(mtm);
		}
    }

	/**
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		if (dockPanel != null) {
			return dockPanel.getDefaultToolbarString();
		}
		return ToolBarW.getAllTools(app);
	}

	

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(AppW app) {
		StringBuilder sb = new StringBuilder();
	
		sb.append(geogebra.common.gui.toolbar.ToolBar.getAllToolsNoMacros(true, true));
	
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

	public void setActiveView(Integer viewID) {
	    activeView = viewID;
    }

	public int getActiveView() {
		return activeView;
	}
	
	public boolean hasPopupOpen(){
		for(int i=0; i<this.modeToggleMenus.size(); i++){
			if (this.modeToggleMenus.get(i).isSubmenuOpen()){
				return true;
			}
		}
		return false;
	}

	public void closeAllSubmenu() {
		for(int i=0; i<modeToggleMenus.size(); i++){
			modeToggleMenus.get(i).hideMenu();
		}
    }
}
