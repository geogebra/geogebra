package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.ToolbarSvgResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.app.GGWToolBar;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel implements SetLabels, TabHandler {

	/**
	 * Tool categories
	 */
	private ToolCategorization mToolCategorization;
	/**
	 * application
	 */
	AppW app;
	/**
	 * move button
	 */
	private StandardButton moveButton;
	/**
	 * categories list
	 */
	private ArrayList<ToolCategorization.Category> categories;
	private ArrayList<CategoryPanel> categoryPanelList;

	/**
	 * @param app
	 *            application
	 */
	public Tools(AppW app) {
		this.app = app;
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
		this.clear();
		mToolCategorization = new ToolCategorization(app,
				app.getSettings().getToolbarSettings().getType(), app.getSettings().getToolbarSettings().getToolsetLevel(), false);
		String def = app.getGuiManager().getCustomToolbarDefinition();

		if (app.isUnbundled3D()) {
			def = app.getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_EUCLIDIAN3D).getToolbarString();
		}
		mToolCategorization
				.resetTools(def);

		categories = mToolCategorization
				.getCategories();
		categoryPanelList = new ArrayList<Tools.CategoryPanel>();
		for (int i = 0; i < categories.size(); i++) {
			CategoryPanel catPanel = new CategoryPanel(categories.get(i));
			categoryPanelList.add(catPanel);
			add(catPanel);
		}
		setMoveMode();

		StandardButton firstBtn = getFirstToolButton();
		if (firstBtn != null) {
			firstBtn.addTabHandler(this);
		}
	}

	private StandardButton getFirstToolButton() {
		if (categoryPanelList != null && !categoryPanelList.isEmpty()) {
			return categoryPanelList.get(0).getFirstButton();
		}
		return null;
	}

	@Override
	public void setLabels() {
		if (categoryPanelList!=null && !categoryPanelList.isEmpty()) {
			for (CategoryPanel categoryPanel : categoryPanelList) {
				categoryPanel.setLabels();
			}
		}
	}

	/**
	 * Focus the very first button of the panel.
	 */
	public void focusFirst() {
		if (categoryPanelList != null && !categoryPanelList.isEmpty()) {
			getFirstToolButton().setFocus(true);
		}

	}
	private class CategoryPanel extends FlowPanel implements SetLabels {
		private Category category;
		private FlowPanel toolsPanel;
		private Label categoryLabel;
		private ArrayList<StandardButton> toolBtnList;
		private ToolbarSvgResources toolSvgRes = ToolbarSvgResources.INSTANCE;

		public CategoryPanel(ToolCategorization.Category cat) {
			super();
			category = cat;
			initGui();
		}

		/**
		 * 
		 * @return the first button of the category.
		 */
		public StandardButton getFirstButton() {
			if (toolBtnList != null && toolBtnList.size() != 0) {
				return toolBtnList.get(0);
			}
			return null;
		}

		// /**
		// * @return the last button of the category.
		// */
		// public StandardButton getLasButton() {
		// if (toolBtnList != null && toolBtnList.size() != 0) {
		// return toolBtnList.get(toolBtnList.size() - 1);
		// }
		// return null;
		// }

		private void initGui() {
			categoryLabel = new Label(
					getmToolCategorization().getLocalizedHeader(category));
			add(categoryLabel);

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			ArrayList<Integer> tools = getmToolCategorization().getTools(
					getmToolCategorization().getCategories().indexOf(category));
			toolBtnList = new ArrayList<StandardButton>();
			ToolBar.parseToolbarString(
					app.getGuiManager().getToolbarDefinition());
			for (int i = 0; i < tools.size(); i++) {
				StandardButton btn = getButton(tools.get(i));
				toolBtnList.add(btn);
				toolsPanel.add(btn);
				if (tools.get(i) == EuclidianConstants.MODE_MOVE) {
					setMoveButton(btn);
				}
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private StandardButton getButton(final int mode) {
			int size = (mode == EuclidianConstants.MODE_DELETE
					|| mode == EuclidianConstants.MODE_IMAGE) ? 24 : 32;

			final StandardButton btn = new StandardButton(
					GGWToolBar.getImageURLNotMacro(toolSvgRes, mode), null,
					size, getApp());
			btn.setTitle(getApp().getLocalization()
					.getMenu(EuclidianConstants.getModeText(mode)));

			if (mode == EuclidianConstants.MODE_DELETE
					|| mode == EuclidianConstants.MODE_IMAGE) {
				btn.addStyleName("plusPadding");
			}
			btn.getElement().setAttribute("mode", mode + "");

			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					getApp().setMode(mode);
					boolean isIpad = Window.Navigator.getUserAgent()
							.toLowerCase().contains("ipad");
					// allow tooltips for iPad
					if (!Browser.isMobile() || isIpad) {
						ToolTipManagerW.sharedInstance().setBlockToolTip(false);
						ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
								getApp().getToolTooltipHTML(mode),
								getApp().getGuiManager().getTooltipURL(mode),
								ToolTipLinkType.Help, getApp(),
								getApp().getAppletFrame().isKeyboardShowing());
						ToolTipManagerW.sharedInstance().setBlockToolTip(true);
					}
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
			ArrayList<Integer> tools = getmToolCategorization().getTools(
					getmToolCategorization().getCategories().indexOf(category));
			for (int i = 0; i < tools.size(); i++) {
				String title = getApp().getLocalization()
						.getMenu(EuclidianConstants.getModeText(tools.get(i)));
				toolBtnList.get(i).setTitle(title);
				toolBtnList.get(i)
						.setAltText(getApp().getLocalization().getMenu(
								EuclidianConstants.getModeText(tools.get(i)))
								+ ". " + app.getToolHelp(tools.get(i)));
			}
		}
	}

	public boolean onTab(Widget source, boolean shiftDown) {
		if (source == getFirstToolButton() && shiftDown) {
			((GuiManagerW) app.getGuiManager()).focusLastButtonOnEV();
			return true;
		}
		return false;
	}
}
