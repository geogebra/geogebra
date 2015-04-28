package org.geogebra.web.web.gui.toolbar;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.laf.GLookAndFeel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 *         Toolbar for GeoGebraWeb
 * 
 */
public class ToolBarW extends FlowPanel implements ClickHandler,
        ToolBarInterface {

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
	protected UnorderedList menuList;
	private GGWToolBar tb;

	/**
	 * Creates general toolbar. There is no app parameter here, because of
	 * UiBinder. After instantiate the ToolBar, call init(Application app) as
	 * well.
	 */
	public ToolBarW(GGWToolBar tb) {
		this.tb = tb;
		this.addStyleName("GGWToolbar");
		this.addDomHandler(this, ClickEvent.getType());
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
	/*
	 * public ToolBarW(AppW app, DockPanel dockPanel) { this(); this.app = app;
	 * this.dockPanel = dockPanel;
	 * 
	 * //setFloatable(false); //setBackground(getBackground()); }
	 */

	/**
	 * Initialization of the ToolBar object
	 * 
	 * @param app1
	 *            application
	 */
	public void init(AppW app1) {
		this.app = app1;
	}


	/**
	 * @return The dock panel associated with this toolbar or null if this is
	 *         the general toolbar.
	 */
	public DockPanel getDockPanel() {
		return dockPanel;
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		mode = -1;

		menuList = new UnorderedList();
		menuList.getElement().addClassName("toolbar_mainItem");
		modeToggleMenus = new ArrayList<ModeToggleMenu>();
		addCustomModesToToolbar(menuList);

		this.clear();
		this.add(menuList);

		setMode(app.getMode());
		// update();
	}

	// TODO: this function is just a temporary hack! Don't regenate the toolbar.
	public void update() {
		this.clear();
		int count = menuList.getWidgetCount();
		menuList.clear();
		for (int i = 0; i < count; i++) {
			menuList.add(modeToggleMenus.get(i));
		}
		this.add(menuList);
	}

	protected ArrayList<ModeToggleMenu> getModeToggleMenus() {
		return modeToggleMenus;
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
	@Override
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
			
			if (!success && tmpMode !=getFirstMode()) {
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

	public UnorderedList getMenuList() {
		return menuList;
	}

	private Integer activeView = App.VIEW_EUCLIDIAN;
	private int maxButtons = 200;

	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 */
	private void addCustomModesToToolbar(UnorderedList mainUl) {
		Vector<ToolbarItem> toolbarVec = getToolbarVec();
		// set toolbar
		for (int i = 0; i < toolbarVec.size() && i < this.maxButtons; i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();

			if (app.isModeValid(menu.get(0).intValue())) {
				ModeToggleMenu mtm = createModeToggleMenu(app, menu, i);
				mtm.setButtonTabIndex(-1);

				modeToggleMenus.add(mtm);
				mainUl.add(mtm);
			}
		}
		
		for (int i = this.maxButtons; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			modeToggleMenus.get(modeToggleMenus.size()-1).addModes(menu);
		}

		if (modeToggleMenus.size() > 0)
			modeToggleMenus.get(0).setButtonTabIndex(0);
	}

	protected ModeToggleMenu createModeToggleMenu(AppW app, Vector<Integer> menu, int order) {
		return new ModeToggleMenu(app, menu, this, order); 
	}
	
	private Vector<ToolbarItem> getToolbarVec() {
		Vector<ToolbarItem> toolbarVec;
		try {
			if (dockPanel != null) {
				toolbarVec = ToolBar.parseToolbarString(
				        dockPanel.getToolbarString());
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
		return toolbarVec;
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

		sb.append(org.geogebra.common.gui.toolbar.ToolBar.getAllToolsNoMacros(true,
		        app.isExam()));

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

	public boolean hasPopupOpen() {
		for (int i = 0; i < this.modeToggleMenus.size(); i++) {
			if (this.modeToggleMenus.get(i).isMenuShown()) {
				return true;
			}
		}
		return false;
	}

	public void closeAllSubmenu() {
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			modeToggleMenus.get(i).hideMenu();
		}
	}
	
	/**
	 * 
	 * @return true iff any of the submenus are opened
	 */
	public boolean isAnyOtherSubmenuOpen(ModeToggleMenu exceptMenu) {
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenu menu = modeToggleMenus.get(i);
			if (exceptMenu != menu && menu.isMenuShown()) {
				return true;
			}
		}
		return false;
	}

	public void onClick(ClickEvent event) {
		event.stopPropagation();
	}

	public void selectMenuBotton(int index) {
		tb.selectMenuButton(index);

	}

	public void selectMenu(int index) {
		tb.deselectButtons();
		int positiveIndex = index;
		if (index < 0) {
			positiveIndex = index + getModeToggleMenus().size();
		}
		ModeToggleMenu mtm2 = getModeToggleMenus().get(positiveIndex);

		mtm2.getToolbarButtonPanel().getElement().focus();
	}

	public int getGroupCount() {
		if(this.modeToggleMenus == null){
			return -1;
		}
	    return this.modeToggleMenus.size();
    }

	public void setMaxButtons(int max) {
		for(ModeToggleMenu m: this.modeToggleMenus){
			m.setMaxHeight(app.getHeight() - GLookAndFeel.TOOLBAR_OFFSET);
		}
	    if(Math.min(max, this.getToolbarVec().size()) == this.getGroupCount()){
	    	return;
	    }
	    this.maxButtons = max;
	    buildGui();
	    
    }

}
