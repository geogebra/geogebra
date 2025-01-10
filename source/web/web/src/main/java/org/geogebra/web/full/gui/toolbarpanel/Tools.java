package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.tooltip.ComponentSnackbar;
import org.geogebra.web.html5.gui.tooltip.ToolTip;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.DomGlobal;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel implements SetLabels {

	/**
	 * application
	 */
	private final AppW app;
	/**
	 * see {@link ToolsTab}
	 */
	private final ToolsTab parentTab;

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
	 * Builds the panel of tools.
	 */
	public void buildGui() {
		// clear panel
		this.clear();
		categoryPanelList = new ArrayList<>();
		// decide if custom toolbar or not
		String def = app.getGuiManager().getCustomToolbarDefinition();
		boolean isCustomToolbar = !ToolBar.isDefaultToolbar(def);

		parentTab.isCustomToolbar = isCustomToolbar;
		// build tools panel depending on if custom or not
		if (isCustomToolbar) {
			this.addStyleName("customToolbar");
		}

		List<ToolCategory> categories = parentTab.toolCollection.getCategories();

		for (int i = 0; i < categories.size(); i++) {
			ToolCategory category = categories.get(i);
			if (GlobalScope.examController.isIdle() || category == null
					|| category.isAllowedInExam()) {
				CategoryPanel catPanel = new CategoryPanel(category,
						parentTab.toolCollection.getTools(i));
				categoryPanelList.add(catPanel);
				add(catPanel);
			}
		}
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

		private final ToolCategory category;
		private final List<Integer> tools;

		private FlowPanel toolsPanel;
		private Label categoryLabel;
		private ArrayList<ToolButton> toolButtonList;

		CategoryPanel(ToolCategory category, List<Integer> tools) {
			super();
			this.category = category;
			this.tools = tools;
			initGui();
		}

		private void addToolButton(Integer mode) {
			ToolButton btn = getToolButton(mode);
			toolButtonList.add(btn);
			toolsPanel.add(btn);
		}

		private void initGui() {
			if (category != null) {
				categoryLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
						category.getLocalizedHeader(app.getLocalization()), "catLabel");
				add(categoryLabel);
				AriaHelper.hide(categoryLabel);
			}

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			toolButtonList = new ArrayList<>();
			for (Integer tool : tools) {
				addToolButton(tool);
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private ToolButton getToolButton(final int mode) {
			final ToolButton btn = new ToolButton(mode, getApp());
			AriaHelper.hide(btn);
			btn.addFastClickHandler(source -> {
				App app = getApp();
				app.setMode(mode);
				showTooltip(mode);
				app.updateDynamicStyleBars();
				Analytics.logEvent(Analytics.Event.TOOL_SELECTED, Analytics.Param.TOOL_NAME,
						app.getInternalToolName(mode));
			});
			return btn;
		}

		@Override
		public void setLabels() {
			// update label of category header
			if (categoryLabel != null) {
				categoryLabel.setText(category.getLocalizedHeader(app.getLocalization()));
			}
			// update tooltips of tools
			for (ToolButton toolButton : toolButtonList) {
				toolButton.setLabel();
			}
		}
	}

	private boolean allowTooltips() {
		// allow tooltips for iPad
		boolean isIpad = DomGlobal.navigator.userAgent.toLowerCase()
				.contains("ipad");
		return (!NavigatorUtil.isMobile() || isIpad) && app.showToolBarHelp();
	}

	/**
	 * @param mode
	 *            mode number
	 */
	public void showTooltip(int mode) {
		if (allowTooltips()) {
			app.getToolTipManager().setBlockToolTip(false);
			app.getToolTipManager()
					.showBottomInfoToolTip(new ToolTip(app.getToolName(mode), app.getToolHelp(mode),
							"Help",
							app.getGuiManager().getTooltipURL(mode)), app,
							ComponentSnackbar.TOOL_TOOLTIP_DURATION);
			app.getToolTipManager().setBlockToolTip(true);
		}
	}
}
