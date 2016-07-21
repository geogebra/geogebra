package org.geogebra.web.web.gui.toolbar;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
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

	protected AppW app;
	private int mode;

	/**
	 * Dock panel associated to this toolbar or null if this is the general
	 * toolbar. Just a single toolbar might have no dock panel, otherwise the
	 * ToolbarContainer logic will not work properly.
	 */
	private DockPanel dockPanel;

	// panels for mobile submenu view
	private FlowPanel submenuPanel;

	protected ArrayList<ModeToggleMenu> modeToggleMenus;
	boolean keepDown;
	protected UnorderedList menuList;
	private GGWToolBar tb;
	private boolean isMobileToolbar;

	/**
	 * Creates general toolbar. There is no app parameter here, because of
	 * UiBinder. After instantiate the ToolBar, call init(Application app) as
	 * well.
	 */
	public ToolBarW(GGWToolBar tb) {
		this.tb = tb;
		this.addStyleName("GGWToolbar");
		this.addDomHandler(this, ClickEvent.getType());
		/*
		 * this.addHandler(new BlurHandler() {
		 * 
		 * @Override public void onBlur(BlurEvent event) { if (isMobileToolbar)
		 * { submenuPanel.clear(); } else { closeAllSubmenu(); } Log.debug(
		 * "onBlur close submenu"); } }, BlurEvent.getType());
		 */
	}

	/**
	 * Constructor for responsive toolbar
	 * 
	 * @param tb
	 * @param submenuPanel
	 */
	public ToolBarW(GGWToolBar tb, FlowPanel submenuPanel) {
		this.tb = tb;
		this.submenuPanel = submenuPanel;
		this.addStyleName("GGWToolbar");
		this.addStyleName("GGWToolbarResponsive");
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

		setMode(app.getMode(), ModeSetter.TOOLBAR);
		tb.onResize();
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
	public int setMode(int newMode, ModeSetter m) {
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
				if (mtm.selectMode(tmpMode, m)) {
					success = true;
					break;
				}
			}
			
			if (!success && tmpMode !=getFirstMode()) {
				tmpMode = setMode(getFirstMode(), m);

			}

			this.mode = tmpMode;
			app.getKernel().notifyModeChanged(mode, ModeSetter.DOCK_PANEL);

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
	/*
	 * private void addCustomModesToToolbar(UnorderedList mainUl) {
	 * Vector<ToolbarItem> toolbarVec = getToolbarVec(); // set toolbar for (int
	 * i = 0; i < toolbarVec.size() && i < this.maxButtons; i++) { ToolbarItem
	 * ob = toolbarVec.get(i); Vector<Integer> menu = ob.getMenu();
	 * 
	 * if (app.isModeValid(menu.get(0).intValue())) { ModeToggleMenu mtm =
	 * createModeToggleMenu(app, menu, i); mtm.setButtonTabIndex(-1);
	 * 
	 * modeToggleMenus.add(mtm); mainUl.add(mtm); } }
	 * 
	 * for (int i = this.maxButtons; i < toolbarVec.size(); i++) { ToolbarItem
	 * ob = toolbarVec.get(i); Vector<Integer> menu = ob.getMenu();
	 * modeToggleMenus.get(modeToggleMenus.size()-1).addModes(menu); }
	 * 
	 * if (modeToggleMenus.size() > 0)
	 * modeToggleMenus.get(0).setButtonTabIndex(0); }
	 */
	private void addCustomModesToToolbar(UnorderedList mainUl) {
		if(app.has(Feature.TOOLBAR_ON_SMALL_SCREENS)){
		Vector<ToolbarItem> toolbarVec = getToolbarVec();
		// set toolbar
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();

			if (app.isModeValid(menu.get(0).intValue())) {
				ModeToggleMenu mtm = createModeToggleMenu(app, menu, i);
				mtm.setButtonTabIndex(-1);
				modeToggleMenus.add(mtm);
				mainUl.add(mtm);
			}
		}
		if (modeToggleMenus.size() > 0)
			modeToggleMenus.get(0).setButtonTabIndex(0);
			// end of Feature.TOOLBAR_ON_SMALL_SCREENS

		} else {
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
				modeToggleMenus.get(modeToggleMenus.size() - 1).addModes(menu);
			}
			 
			 if (modeToggleMenus.size() > 0)
			 modeToggleMenus.get(0).setButtonTabIndex(0); 
		}
	}


	protected ModeToggleMenu createModeToggleMenu(AppW app, Vector<Integer> menu, int order) {

		if (app.has(Feature.TOOLBAR_ON_SMALL_SCREENS)) {
			// toolbarVecSize is i.e. 12 for AV, 14 for 3D
			if (maxButtons < getToolbarVecSize()) {
				isMobileToolbar = true;
				return new ModeToggleMenuP(app, menu, this, order, submenuPanel);
			} else {
				isMobileToolbar = false;
				return new ModeToggleMenu(app, menu, this, order);
			}

		} else {
			return new ModeToggleMenu(app, menu, this, order);
		}
	}
	
	protected Vector<ToolbarItem> getToolbarVec() {
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
				Log.debug("invalid toolbar string: "
				        + dockPanel.getToolbarString());
			} else {
				Log.debug("invalid toolbar string: "
				        + app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = ToolBar.parseToolbarString(getDefaultToolbarString());
		}
		return toolbarVec;
    }

	public int getToolbarVecSize() {
		return this.getToolbarVec().size();
	}
	/**
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		if (dockPanel != null) {
			return dockPanel.getDefaultToolbarString();
		}
		return ToolBar.getAllTools(app);
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

	@Override
	public void closeAllSubmenu() {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				modeToggleMenus.get(i).hideMenu();
			}
	}

	

	/**
	 * 
	 * @return true if any of the submenus are opened
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
		// TODO: maybe use CancelEvents.instance?
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

		if (app.has(Feature.TOOLBAR_ON_SMALL_SCREENS)) {
			// make sure gui is only rebuilt when necessary (when state changes
			// between web view and mobile view)
			if ((isMobileToolbar && max >= getToolbarVecSize()) || (!isMobileToolbar && max < getToolbarVecSize())) {
				this.maxButtons = max;
				// Log.debug("max buttons: " + max);
				if (isMobileToolbar) {
					submenuPanel.clear();
				}
				buildGui();
			} else {
				if (Math.min(max, this.getToolbarVec().size()) == this.getGroupCount()) {
					return;
				}
			}

		} else {

			if (Math.min(max, this.getToolbarVec().size()) == this.getGroupCount()) {
				return;
			}
			this.maxButtons = max;
			Log.debug("max buttons: " + max);
			buildGui();
		}
    }

	public String getImageURL(int mode) {
		return GGWToolBar.getImageURL(mode, app);
	}

	public boolean isMobileToolbar() {
		return isMobileToolbar;
	}

	public int getMaxButtons() {
		return maxButtons;
	}

	public GGWToolBar getGGWToolBar() {
		return tb;
	}
}
