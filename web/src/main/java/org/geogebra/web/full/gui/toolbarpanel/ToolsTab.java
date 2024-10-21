package org.geogebra.web.full.gui.toolbarpanel;

import java.util.Collection;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolsetLevel;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.ScrollPanel;

/**
 * tab of tools
 */
public class ToolsTab extends ToolbarTab implements ExamListener {

	/**
	 *
	 */
	private final ToolbarPanel toolbarPanel;

	/**
	 * panel containing the tools
	 */
	private Tools toolsPanel;

	/**
	 * button to get more tools
	 */
	private StandardButton moreBtn;

	/**
	 * button to get less tools
	 */
	private StandardButton lessBtn;

	/**
	 * tab containing the tools
	 */
	private ScrollPanel sp;

	private final App app;

	public boolean isCustomToolbar = false;

	/**
	 * Tool categories
	 */
	ToolCollection toolCollection;

	/**
	 * panel containing tools
	 * @param toolbarPanel toolbar panel
	 */
	public ToolsTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.getApp();

		toolCollection = app.getAvailableTools();

		createContents();
		if (!isCustomToolbar) {
			handleMoreLessButtons();
		}
		GlobalScope.examController.addListener(this);
	}

	private void handleMoreLessButtons() {
		createMoreLessButtons();
		addMoreLessButtons();
	}

	private void createMoreLessButtons() {
		moreBtn = new StandardButton(
				app.getLocalization().getMenu("Tools.More"));
		AriaHelper.hide(moreBtn);
		moreBtn.setStyleName("materialTextButton");
		lessBtn = new StandardButton(
				app.getLocalization().getMenu("Tools.Less"));
		AriaHelper.hide(lessBtn);
		lessBtn.setStyleName("materialTextButton");
		moreBtn.addFastClickHandler(source -> onMorePressed());

		lessBtn.addFastClickHandler(source -> onLessPressed());
	}

	/** More button handler */
	private void onMorePressed() {
		toolCollection.setLevel(toolCollection.getLevel().getNext());
		updateContent();
	}

	/** Less button handler */
	private void onLessPressed() {
		toolCollection.setLevel(toolCollection.getLevel().getPrevious());
		updateContent();
	}

	/**
	 * add more or less button to tool panel
	 */
	private void addMoreLessButtons() {
		Collection<ToolsetLevel> levels = toolCollection.getLevels();
		ToolsetLevel level = toolCollection.getLevel();

		if (levels.contains(level.getPrevious())) {
			toolsPanel.add(lessBtn);
		}

		if (levels.contains(level.getNext())) {
			toolsPanel.add(moreBtn);
		}
	}

	private void createContents() {
		sp = new ScrollPanel();
		CustomScrollbar.apply(sp);
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
		setMoveMode();
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
		toolbarPanel.close(false);
	}

	@Override
	public void onResize() {
		super.onResize();
		int w = this.toolbarPanel.getTabWidth();
		if (w < 0) {
			return;
		}
		setWidth(w + "px");
		sp.setWidth(w + "px");
		double height = toolbarPanel.getTabHeight();
		if (height >= 0) {
			sp.setHeight(height + "px");
		}
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

	@Override
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.TOOLS;
	}

	@Override
	public void setLabels() {
		toolsPanel.setLabels();
		if (moreBtn != null && lessBtn != null) {
			moreBtn.setText(app.getLocalization().getMenu("Tools.More"));
			lessBtn.setText(app.getLocalization().getMenu("Tools.Less"));
		}
	}

	@Override
	public void examStateChanged(ExamState newState) {
		toolCollection = app.getAvailableTools();
		updateContent();
	}
}
