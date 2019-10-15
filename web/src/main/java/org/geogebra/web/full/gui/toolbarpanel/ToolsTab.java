package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.toolcategorization.ToolsetLevel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ToolbarSettings;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * tab of tools
 */
public class ToolsTab extends ToolbarPanel.ToolbarTab {

	/**
	 *
	 */
	private final ToolbarPanel toolbarPanel;

	/**
	 * panel containing the tools
	 */
	Tools toolsPanel;

	/**
	 * button to get more tools
	 */
	StandardButton moreBtn;

	/**
	 * button to get less tools
	 */
	StandardButton lessBtn;

	/**
	 * tab containing the tools
	 */

	private ScrollPanel sp;

	private App app;
	/**
	 *
	 */
	public boolean isCustomToolbar = false;

	/**
	 * panel containing tools
	 * @param toolbarPanel TODO
	 */
	public ToolsTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.getApp();
		createContents();
		if (!isCustomToolbar) {
			handleMoreLessButtons();
		}
	}

	private void handleMoreLessButtons() {
		createMoreLessButtons();
		addMoreLessButtons();
	}

	private void createMoreLessButtons() {
		moreBtn = new StandardButton(
				app.getLocalization().getMenu("Tools.More"), app);
		AriaHelper.hide(moreBtn);
		moreBtn.addStyleName("moreLessBtn");
		moreBtn.removeStyleName("button");
		lessBtn = new StandardButton(
				app.getLocalization().getMenu("Tools.Less"), app);
		AriaHelper.hide(lessBtn);
		lessBtn.addStyleName("moreLessBtn");
		lessBtn.removeStyleName("button");
		moreBtn.setIgnoreTab();
		lessBtn.setIgnoreTab();
		moreBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onMorePressed();
			}
		});

		lessBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onLessPressed();
			}
		});
	}

	/** More button handler */
	protected void onMorePressed() {
		ToolsetLevel level = app.getSettings().getToolbarSettings()
				.getToolsetLevel();
		if (level.equals(ToolsetLevel.EMPTY_CONSTRUCTION)) {
			app.getSettings().getToolbarSettings()
					.setToolsetLevel(ToolsetLevel.STANDARD);
		} else if (level.equals(ToolsetLevel.STANDARD)) {
			app.getSettings().getToolbarSettings()
					.setToolsetLevel(ToolsetLevel.ADVANCED);
		}
		updateContent();
	}

	/** Less button handler */
	protected void onLessPressed() {
		ToolbarSettings toolbarSettings = app.getSettings().getToolbarSettings();
		ToolsetLevel level = toolbarSettings
				.getToolsetLevel();
		if (level.equals(ToolsetLevel.ADVANCED)) {
			toolbarSettings
					.setToolsetLevel(ToolsetLevel.STANDARD);
		} else if (level.equals(ToolsetLevel.STANDARD)
				&& toolbarSettings.hasEmptyConstruction()) {
			toolbarSettings
					.setToolsetLevel(ToolsetLevel.EMPTY_CONSTRUCTION);
		} else {
			toolbarSettings
					.setToolsetLevel(ToolsetLevel.STANDARD);
		}
		updateContent();
	}

	/**
	 * add more or less button to tool panel
	 */
	public void addMoreLessButtons() {
		boolean hasEmptyConstruction = app.getSettings().getToolbarSettings()
				.hasEmptyConstruction();
		ToolsetLevel level = app.getSettings().getToolbarSettings()
				.getToolsetLevel();

		switch (level) {
		case EMPTY_CONSTRUCTION:
			toolsPanel.add(moreBtn);
			break;
		case STANDARD:
			if (hasEmptyConstruction) {
				toolsPanel.add(lessBtn);
			}
			toolsPanel.add(moreBtn);
			break;

		case ADVANCED:
			toolsPanel.add(lessBtn);

		default:
			break;
		}

	}

	private void createContents() {
		sp = new ScrollPanel();
		sp.setAlwaysShowScrollBars(false);
		toolsPanel = new Tools((AppW) app, this);
		sp.add(toolsPanel);
		add(sp);
	}

	/**
	 * update the content of tool panel
	 */
	public void updateContent() {
		toolsPanel.removeFromParent();
		toolsPanel = new Tools((AppW) app, this);
		sp.clear();
		sp.add(toolsPanel);
		if (!isCustomToolbar) {
			if (moreBtn == null && lessBtn == null) {
				createMoreLessButtons();
			}
			this.toolbarPanel.setLabels();
			handleMoreLessButtons();
		}
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	void setMoveMode() {
		toolsPanel.setMoveMode();
	}

	/**
	 * Changes visual settings of selected mode..
	 *
	 * @param mode
	 *            the mode will be selected
	 */
	void setMode(int mode) {
		toolsPanel.setMode(mode);
	}

	@Override
	public void open() {
		toolbarPanel.openTools(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}

	@Override
	public void onResize() {
		super.onResize();
		int w = this.toolbarPanel.getTabWidth();
		if (w < 0) {
			return;
		}
		setWidth(2 * w + "px");
		getElement().getStyle().setLeft(w, Unit.PX);

		sp.setWidth(w + "px");
		double height = (app.isPortrait() ? toolbarPanel.getOffsetHeight() : app.getHeight())
				- ToolbarPanel.CLOSED_HEIGHT_PORTRAIT;
		if (height >= 0) {
			sp.setHeight(height + "px");
		}
		if (app.getWidth() < app.getHeight()) {
			w = 420;
		}
		ToolTipManagerW.sharedInstance().setTooltipWidthOnResize(w);
	}

	/**
	 * @param modeMove
	 *            mode ID
	 */
	public void showTooltip(int modeMove) {
		toolsPanel.showTooltip(modeMove);
	}

	@Override
	protected void onActive() {
		// unused
	}
}