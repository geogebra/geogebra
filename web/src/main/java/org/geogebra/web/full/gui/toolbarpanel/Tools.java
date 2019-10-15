package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel implements SetLabels {

	/**
	 * Tool categories
	 */
	private ToolCategorization mToolCategorization;
	/**
	 * application
	 */
	AppW app;
	/**
	 * see {@link ToolsTab}
	 */
	ToolsTab parentTab;
	/**
	 * move button
	 */
	private StandardButton moveButton;
	/**
	 * categories list
	 */
	private ArrayList<ToolCategory> categories;
	private ArrayList<CategoryPanel> categoryPanelList;

	/**
	 * @param app
	 *            application
	 * @param parentTab
	 *            see {@link ToolsTab}
	 */
	public Tools(AppW app, ToolsTab parentTab) {
		this.app = app;
		this.parentTab = parentTab;
		this.addStyleName("toolsPanel");
		buildGui();
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMode(EuclidianConstants.MODE_MOVE);
		clearSelectionStyle();
		if (moveButton != null) {
			moveButton.getElement().setAttribute("selected", "true");
		}
	}

	/**
	 * Changes visual settings of selected mode.
	 * 
	 * @param mode
	 *            the mode will be selected
	 */
	public void setMode(int mode) {
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			return;
		}
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					if ((mode + "").equals(panelTools.getWidget(j).getElement()
							.getAttribute("mode"))) {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "true");
					} else {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "false");
					}
				}
			}
		}

	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return tool categorization
	 */
	public ToolCategorization getmToolCategorization() {
		return mToolCategorization;
	}

	/**
	 * @param moveButton
	 *            floating action move btn
	 */
	public void setMoveButton(StandardButton moveButton) {
		this.moveButton = moveButton;
	}

	/**
	 * Clears visual selection of all tools.
	 */
	public void clearSelectionStyle() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					panelTools.getWidget(j).getElement()
							.setAttribute("selected", "false");
				}
			}
		}
	}

	/**
	 * Builds the panel of tools.
	 */
	public void buildGui() {
		// clear panel
		this.clear();
		categoryPanelList = new ArrayList<>();
		// decide if custom toolbar or not
		String def = app.getGuiManager().getCustomToolbarDefinition();
		boolean isCustomToolbar = !ToolBar.isDefaultToolbar(def)
				&& !ToolBar.isDefaultToolbar3D(def);
		parentTab.isCustomToolbar = isCustomToolbar;
		// build tools panel depending on if custom or not
		if (!isCustomToolbar) {
			mToolCategorization = app.createToolCategorization();
			if (app.isUnbundled3D()) {
				def = app.getGuiManager().getLayout().getDockManager()
						.getPanel(App.VIEW_EUCLIDIAN3D).getToolbarString();
			}
			mToolCategorization.resetTools(def);
			categories = mToolCategorization.getCategories();
			for (int i = 0; i < categories.size(); i++) {
				CategoryPanel catPanel = new CategoryPanel(categories.get(i));
				categoryPanelList.add(catPanel);
				add(catPanel);
			}
		} else {
			this.addStyleName("customToolbar");
			Vector<ToolbarItem> toolbarItems = getToolbarVec(def);
			for (ToolbarItem toolbarItem : toolbarItems) {
				CategoryPanel catPanel = new CategoryPanel(toolbarItem);
				categoryPanelList.add(catPanel);
				add(catPanel);
			}
		}
		setMoveMode();
	}

	/**
	 * @param toolbarString
	 *            string definition of toolbar
	 * @return the vector of groups of tools
	 */
	protected Vector<ToolbarItem> getToolbarVec(String toolbarString) {
		Vector<ToolbarItem> toolbarVec;
		try {
			toolbarVec = ToolBar.parseToolbarString(toolbarString);
		} catch (Exception e) {
			toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
		}
		return toolbarVec;
	}

	@Override
	public void setLabels() {
		if (categoryPanelList != null && !categoryPanelList.isEmpty()) {
			for (CategoryPanel categoryPanel : categoryPanelList) {
				categoryPanel.setLabels();
			}
		}
	}

	private class CategoryPanel extends FlowPanel implements SetLabels {
		private ToolCategory category;
		private FlowPanel toolsPanel;
		private Label categoryLabel;
		private ArrayList<ToolButton> toolButtonList;

		public CategoryPanel(ToolCategory cat) {
			super();
			category = cat;
			initGui();
		}

		public CategoryPanel(ToolbarItem toolbarItem) {
			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			toolButtonList = new ArrayList<>();
			Vector<Integer> tools = toolbarItem.getMenu();
			for (Integer mode : tools) {
				if (app.isModeValid(mode)) {
					addToolButton(mode);
				}
			}
			add(toolsPanel);
		}

		private void addToolButton(Integer mode) {
			ToolButton btn = getToolButton(mode);
			toolButtonList.add(btn);
			toolsPanel.add(btn);
			if (mode == EuclidianConstants.MODE_MOVE) {
				setMoveButton(btn);
			}
		}

		private void initGui() {
			categoryLabel = new Label(
					getmToolCategorization().getLocalizedHeader(category));
			categoryLabel.setStyleName("catLabel");
			add(categoryLabel);
			AriaHelper.hide(categoryLabel);

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			ArrayList<Integer> tools = getmToolCategorization().getTools(
					getmToolCategorization().getCategories().indexOf(category));
			toolButtonList = new ArrayList<>();
			ToolBar.parseToolbarString(
					app.getGuiManager().getToolbarDefinition());
			for (int i = 0; i < tools.size(); i++) {
				addToolButton(tools.get(i));
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private ToolButton getToolButton(final int mode) {
			final ToolButton btn = new ToolButton(mode, getApp());
			AriaHelper.hide(btn);
			btn.setIgnoreTab();
			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					getApp().setMode(mode);
					showTooltip(mode);
					getApp().updateDynamicStyleBars();
				}
			});
			return btn;
		}

		@Override
		public void setLabels() {
			// update label of category header
			categoryLabel.setText(
					getmToolCategorization().getLocalizedHeader(category));
			// update tooltips of tools
			for (ToolButton toolButton : toolButtonList) {
				toolButton.setLabel();
			}
		}
	}

	private boolean allowTooltips() {
		// allow tooltips for iPad
		boolean isIpad = Window.Navigator.getUserAgent().toLowerCase()
				.contains("ipad");
		return (!Browser.isMobile() || isIpad) && app.showToolBarHelp();
	}

	/**
	 * @param modeMove
	 *            mode number
	 */
	public void showTooltip(int modeMove) {
		if (allowTooltips()) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance()
					.showBottomInfoToolTip(app.getToolTooltipHTML(modeMove),
							((GuiManagerW) app.getGuiManager()).getTooltipURL(
									modeMove),
							ToolTipLinkType.Help, app,
							app.getAppletFrame().isKeyboardShowing());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}
}
