package org.geogebra.web.full.gui.toolbar;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Toolbar for GeoGebraWeb
 *
 * @author gabor
 */
public class ToolBarW extends FlowPanel
 implements ClickHandler, ToolBarInterface, MouseOutHandler {

	private AppW app;
	private int mode;

	// panels for mobile submenu view
	private FlowPanel submenuPanel;

	private ArrayList<ModeToggleMenuW> modeToggleMenus;
	private UnorderedList menuList;
	private GGWToolBar tb;
	private boolean mobileToolbar;
	private boolean isMouseDown = false;
	private int mousePosition;
	private int toolbarPosition;
	private Integer activeView = App.VIEW_EUCLIDIAN;
	private int maxButtons = 200;

	/**
	 * Constructor for responsive toolbar
	 * 
	 * @param tb
	 *            toolbar panel
	 * @param submenuPanel
	 *            submenu panel
	 */
	public ToolBarW(GGWToolBar tb, FlowPanel submenuPanel) {
		this.tb = tb;
		this.submenuPanel = submenuPanel;
		this.addStyleName("GGWToolbar");

		this.addDomHandler(this, ClickEvent.getType());
		this.addDomHandler(this, MouseOutEvent.getType());
		submenuPanel.addDomHandler(this, MouseOutEvent.getType());
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
		return null;
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		mode = -1;

		menuList = new UnorderedList();
		menuList.getElement().addClassName("toolbar_mainItem");
		modeToggleMenus = new ArrayList<>();
		addCustomModesToToolbar(menuList);

		clear();
		add(menuList);

		setMode(app.getMode(), ModeSetter.TOOLBAR);
		
		setVisible(true);
		tb.onResize();
		
		// update();
	}

	/**
	 * Rebuild the toolbar.
	 * 
	 * TODO: this function is just a temporary hack! Don't regenate the toolbar.
	 */
	public void update() {
		this.clear();
		int count = menuList.getWidgetCount();
		menuList.clear();
		for (int i = 0; i < count; i++) {
			menuList.add(modeToggleMenus.get(i));
		}
		this.add(menuList);
	}

	protected ArrayList<ModeToggleMenuW> getModeToggleMenus() {
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
				ModeToggleMenuW mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(tmpMode, m)) {
					success = true;
					break;
				}
			}
			
			if (!success && tmpMode != getFirstMode()) {
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
		ModeToggleMenuW mtm = modeToggleMenus.get(0);
		return mtm.getFirstMode();
	}

	public UnorderedList getMenuList() {
		return menuList;
	}

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
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();

			if (app.isModeValid(menu.get(0).intValue())) {
				ModeToggleMenuW mtm = createModeToggleMenu(app, menu, i);
				mtm.setButtonTabIndex(-1);
				modeToggleMenus.add(mtm);
				mainUl.add(mtm);
			}
		}
		if (modeToggleMenus.size() > 0) {
			modeToggleMenus.get(0).setButtonTabIndex(0);
			// end of Feature.TOOLBAR_ON_SMALL_SCREENS
		}
	}

	protected ModeToggleMenuW createModeToggleMenu(AppW appw, Vector<Integer> menu, int order) {
		// toolbarVecSize is i.e. 12 for AV, 14 for 3D
		if (maxButtons < getToolbarVecSize() || (maxButtons < 11 && getToolbarVecSize() < 11)) {
			mobileToolbar = true;
			return new ModeToggleMenuP(appw, menu, this, order, submenuPanel);
		}
		mobileToolbar = false;
		return new ModeToggleMenuW(appw, menu, this, order);
	}
	
	protected Vector<ToolbarItem> getToolbarVec() {
		Vector<ToolbarItem> toolbarVec;
		try {

			toolbarVec = ToolBar.parseToolbarString(
					app.getGuiManager().getToolbarDefinition());

		} catch (Exception e) {

			Log.debug("invalid toolbar string: "
					+ app.getGuiManager().getToolbarDefinition());

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

		return ToolBar.getAllTools(app);
	}

	public void setActiveView(Integer viewID) {
		activeView = viewID;
	}

	public int getActiveView() {
		return activeView;
	}

	/**
	 * @return whether a submenu is open
	 */
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
		if (modeToggleMenus != null) {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				modeToggleMenus.get(i).hideMenu();
			}
		}
		if (submenuPanel != null) {
			submenuPanel.clear();
		}
	}

	/**
	 * @param exceptMenu
	 *            check all except this one
	 * @return true if any of the submenus are opened
	 */
	public boolean isAnyOtherSubmenuOpen(ModeToggleMenuW exceptMenu) {
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenuW menu = modeToggleMenus.get(i);
			if (exceptMenu != menu && menu.isMenuShown()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO: maybe use CancelEvents.instance?
		event.stopPropagation();
	}

	public void selectMenuButton(int index) {
		tb.selectMenuButton(index);
	}

	/**
	 * Focus given submenu. Index is considered mod size.
	 * 
	 * @param index
	 *            index of submenu; may be negative
	 */
	public void selectMenu(int index) {
		tb.deselectButtons();
		int positiveIndex = index;
		if (index < 0) {
			positiveIndex = index + getModeToggleMenus().size();
		}
		ModeToggleMenuW mtm2 = getModeToggleMenus().get(positiveIndex);

		mtm2.getToolbarButtonPanel().getElement().focus();
	}

	/**
	 * @return number of groups or -1 if not initialized
	 */
	public int getGroupCount() {
		if (this.modeToggleMenus == null) {
			return -1;
		}
		return this.modeToggleMenus.size();
	}

	/**
	 * Update number of buttons that fit in a single row.
	 * 
	 * @param max
	 *            max number of buttons
	 */
	public void setMaxButtons(int max) {
		for (ModeToggleMenuW m : this.modeToggleMenus) {
			m.setMaxHeight(app.getHeight() - GLookAndFeel.TOOLBAR_OFFSET);
		}

		if (getToolbarVecSize() < 11) {
			if ((mobileToolbar && max >= 11) || !mobileToolbar && max < 11) {
				this.maxButtons = max;
				closeAllSubmenu();
				buildGui();

			}
		}
		// make sure gui is only rebuilt when necessary (when state changes
		// between web view and mobile view)
		else if ((mobileToolbar && max >= getToolbarVecSize())
				|| (!mobileToolbar && max < getToolbarVecSize())) {
			this.maxButtons = max;
			closeAllSubmenu();
			buildGui();

		} else {
			if (Math.min(max, this.getToolbarVec().size()) == this.getGroupCount()) {
				return;
			}
		}
	}

	@Override
	public boolean isMobileToolbar() {
		return mobileToolbar;
	}

	public int getMaxButtons() {
		return maxButtons;
	}

	public GGWToolBar getGGWToolBar() {
		return tb;
	}

	/**
	 * Scroll to given position.
	 * 
	 * @param positionX
	 *            x offset
	 */
	public void setPosition(int positionX) {
		if (isMouseDown) {
			if (this.isVisible()) {
				((ScrollPanel) this.getParent())
						.setHorizontalScrollPosition(toolbarPosition + (mousePosition - positionX));
			} else {
				((ScrollPanel) submenuPanel.getParent())
						.setHorizontalScrollPosition(toolbarPosition + (mousePosition - positionX));
			}
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		setMouseDown(false);
	}

	public void setMouseDown(boolean down) {
		isMouseDown = down;
	}

	/**
	 * @param mouse
	 *            mouse x-coord
	 * @param tb
	 *            toolbar x-coord
	 */
	public void setStartPositions(int mouse, int tb) {
		mousePosition = mouse;
		toolbarPosition = tb;
	}

	@Override
	public boolean isShown() {
		return isVisible();
	}

}
