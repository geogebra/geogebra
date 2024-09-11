package org.geogebra.web.full.gui.toolbarpanel;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.io.layout.DockPanelData.TabIds;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

/**
 * Navigation rail or bottom bar
 */
class NavigationRail extends FlowPanel {
	private MenuToggleButton btnMenu;
	private @CheckForNull StandardButton btnAlgebra;
	private @CheckForNull StandardButton btnTools;
	private @CheckForNull StandardButton btnDistribution;
	private @CheckForNull StandardButton btnTableView;
	private @CheckForNull StandardButton btnSpreadsheet;
	private final FlowPanel contents;
	private FlowPanel center;
	private boolean animating = false;
	private boolean lastOrientation;

	/**
	 * application
	 */
	AppW app;
	/**
	 * Parent tool panel
	 */
	final ToolbarPanel toolbarPanel;
	private FocusableWidget focusableMenuButton;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 */
	public NavigationRail(ToolbarPanel toolbarPanel) {
		this.app = toolbarPanel.getApp();
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);
		if (app.getAppletParameters().getDataParamShowMenuBar(false)) {
			createMenuButton();
			toolbarPanel.setMenuButton(focusableMenuButton);
		}
		createCenter();
		setLabels();
		setTabIndexes();
		lastOrientation = app.isPortrait();
		setStyleName("header");
		updateIcons(!examController.isIdle());
	}

	private void createCenter() {
		if (!toolbarPanel.needsNavRail()) {
			return;
		}

		center = new FlowPanel();
		center.addStyleName("center");

		if (!app.getConfig().hasDistributionView()) {
			createAlgebraButton();
			center.add(btnAlgebra);
		}

		boolean showToolPanel = app.getConfig().showToolsPanel();

		if (showToolPanel) {
			createToolsButton();
			center.add(btnTools);
		}
		if (app.getConfig().hasDistributionView()) {
			createDistributionButton();
			center.add(btnDistribution);
		}
		if (app.getConfig().hasTableView()) {
			createTableViewButton();
			center.add(btnTableView);
		}
		if (app.getConfig().hasSpreadsheetView()) {
			createSpreadsheetButton();
			center.add(btnSpreadsheet);
		}
		if (btnMenu != null && !isHeaderExternal()) {
			center.addStyleName("withMenu");
		}
		contents.add(center);
	}

	private void createAlgebraButton() {
		btnAlgebra = createTabButton("Algebra",
				MaterialDesignResources.INSTANCE.toolbar_algebra_graphing());
		btnAlgebra.addFastClickHandler(source -> onAlgebraPressed());
	}

	private void createToolsButton() {
		btnTools = createTabButton("Tools",
				MaterialDesignResources.INSTANCE.toolbar_tools());
		btnTools.addFastClickHandler(source -> onToolsPressed());
	}

	private void createTableViewButton() {
		btnTableView = createTabButton("Table",
				MaterialDesignResources.INSTANCE.toolbar_table_view_black());
		btnTableView.addFastClickHandler(source -> onTableViewPressed());
	}

	private void createDistributionButton() {
		btnDistribution = createTabButton("Distribution",
				MaterialDesignResources.INSTANCE.toolbar_distribution());
		btnDistribution.addFastClickHandler(source -> onDistributionPressed());
	}

	private void createSpreadsheetButton() {
		btnSpreadsheet = createTabButton("Perspective.Spreadsheet",
				MaterialDesignResources.INSTANCE.toolbar_spreadsheet());
		btnSpreadsheet.addFastClickHandler(source -> onSpreadsheetPressed());
	}

	private StandardButton createTabButton(String label, SVGResource icon) {
		StandardButton btn = new StandardButton(icon, label, 24);
		btn.addStyleName("tabButton");
		AriaHelper.hide(btn);
		return btn;
	}

	/**
	 * Handler for Algebra button.
	 */
	protected void onAlgebraPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.ALGEBRA) {
			if (app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC) {
				return;
			}
			onClosePressed(false);
			return;
		}
		toolbarPanel.openAlgebra(isOpen());
		toolbarPanel.getFrame().closeKeyboard();
		toolbarPanel.getFrame().showKeyboardButton(true);
	}

	/**
	 * Handler for tools button.
	 */
	protected void onToolsPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.TOOLS) {
			onClosePressed(false);
			return;
		}
		toolbarPanel.getFrame().closeKeyboard();
		toolbarPanel.getFrame().showKeyboardButton(false);
		toolbarPanel.openTools(isOpen());
	}

	/**
	 * Handler for table view button.
	 */
	protected void onTableViewPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.TABLE) {
			if (app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC) {
				return;
			}
			onClosePressed(false);
			return;
		}
		toolbarPanel.getFrame().closeKeyboard();
		toolbarPanel.getFrame().showKeyboardButton(true);
		toolbarPanel.openTableView(null, isOpen());
		if (app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC) {
			toolbarPanel.openTableFunctionDialogIfEmpty();
		}
	}

	/**
	 * Handler for distribution view button.
	 */
	protected void onDistributionPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.DISTRIBUTION) {
			onClosePressed(false);
			return;
		}
		toolbarPanel.openDistributionView(isOpen());
	}

	/**
	 * Handler for spreadsheet view button.
	 */
	protected void onSpreadsheetPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.SPREADSHEET) {
			onClosePressed(false);
			return;
		}
		toolbarPanel.openSpreadsheetView(isOpen());
	}

	/**
	 * Handler for Close button.
	 */
	protected void onClosePressed(boolean snap) {
		app.hideMenu();
		onClose(snap, ToolbarPanel.OPEN_ANIM_TIME);
		toolbarPanel.getFrame().showKeyboard(false, null, true);
	}

	protected void onClose(boolean snap, int time) {
		updateIcons(null, examController.isExamActive());
		addCloseOrientationStyles();
		toolbarPanel.setMoveMode();
		toolbarPanel.close(snap, time);
		app.getAccessibilityManager().focusAnchorOrMenu();
	}

	private void addCloseOrientationStyles() {
		Dom.toggleClass(toolbarPanel, "closePortrait",
				"closeLandscape", app.isPortrait());
	}

	void removeCloseOrientationStyles() {
		toolbarPanel.removeStyleName("closePortrait");
		toolbarPanel.removeStyleName("closeLandscape");
	}

	/**
	 * set labels
	 */
	void setLabels() {
		setAltTexts();
	}

	private void setAltTexts() {
		if (btnMenu != null) {
			btnMenu.setImageAltText(app.getLocalization().getMenu("Menu"));
		}
		setButtonText(btnAlgebra, app.getConfig().getAVTitle());
		setButtonText(btnTools, "Tools");
		setButtonText(btnTableView, "Table");
		setButtonText(btnDistribution, "Distribution");
		setButtonText(btnSpreadsheet, "Perspective.Spreadsheet");
	}

	private void setButtonText(StandardButton btn, String key) {
		if (btn != null) {
			btn.setText(app.getLocalization().getMenu(key));
		}
	}

	/**
	 * @param tabId - tab id
	 */
	void selectTab(TabIds tabId) {
		if (center == null) {
			return;
		}
		updateIcons(tabId, examController.isExamActive());
		toolbarPanel.setSelectedTabId(tabId);
	}

	private void setSelected(StandardButton btn, boolean selected, boolean exam) {
		if (btn != null) {
			GColor color = GColor.WHITE;
			if (!exam) {
				color = selected ? app.getVendorSettings().getPrimaryColor() : GColor.BLACK;
			}
			btn.setIcon(((SVGResource) btn.getIcon()).withFill(color.toString()));
			Dom.toggleClass(btn, "selected", selected);
		}
	}

	/**
	 * @param expanded - whether menu is expanded
	 */
	public void markMenuAsExpanded(boolean expanded) {
		if (btnMenu != null) {
			btnMenu.getElement().setAttribute("aria-expanded",
					String.valueOf(expanded));
			btnMenu.getElement().removeAttribute("aria-pressed");
		}
	}

	private void createMenuButton() {
		btnMenu = new MenuToggleButton(app);
		focusableMenuButton = new FocusableWidget(AccessibilityGroup.MENU, null, btnMenu);
		updateMenuPosition();
		markMenuAsExpanded(false);
	}

	private void updateMenuPosition() {
		if (btnMenu == null) {
			return;
		}
		boolean external = isHeaderExternal();
		btnMenu.setExternal(external);
		if (external) {
			btnMenu.addToGlobalHeader();
			addShareButton();
		} else {
			toolbarPanel.getFrame().add(btnMenu);
		}
	}

	private boolean isHeaderExternal() {
		return needsHeader() && GlobalHeader.isInDOM();
	}

	protected boolean needsHeader() {
		return !app.getAppletFrame().shouldHideHeader();
	}

	private void addShareButton() {
		GlobalHeader.INSTANCE.initShareButton(share -> {
			app.hideMenu();
			FileMenuW.share(app, share);
		}, app);
	}

	/**
	 * Hide the entire undo/redo panel (eg. during animation).
	 */
	public void hideUndoRedoPanel() {
		toolbarPanel.showHideUndoRedoPanel(false);
	}

	/**
	 * @return - true if toolbar is open
	 */
	public boolean isOpen() {
		return toolbarPanel.isOpen();
	}

	private void removeOpenStyles() {
		removeStyleName("header-open-portrait");
		removeStyleName("header-open-landscape");
	}

	private void removeCloseStyles() {
		removeStyleName("header-close-portrait");
		removeStyleName("header-close-landscape");
	}

	/**
	 * update style of toolbar
	 */
	public void updateStyle() {
		if (isAnimating()) {
			return;
		}

		String orientation = app.isPortrait() ? "portrait" : "landscape";
		Dom.toggleClass(this, "compact",
				app.getAppletFrame().hasCompactNavigationRail());
		if (isOpen()) {
			removeCloseStyles();
			addStyleName("header-open-" + orientation);
			if (!app.isPortrait()) {
				clearHeight();
				clearWidth();
			}
		} else {
			removeOpenStyles();
			addStyleName("header-close-" + orientation);
			addCloseOrientationStyles();
		}

		updateMenuButtonStyle();

		toolbarPanel.updateUndoRedoPosition();
		toolbarPanel.updateUndoRedoActions();
		toolbarPanel.updateStyle();
	}

	private void updateMenuButtonStyle() {
		if (btnMenu == null) {
			return;
		}
		Dom.toggleClass(btnMenu, "portraitMenuBtn",
				"landscapeMenuBtn", app.isPortrait());
	}

	/**
	 * handle resize of toolbar
	 */
	public void resize() {
		if (isAnimating()) {
			return;
		}
		updateStyle();
	}

	/**
	 * @return true if animating
	 */
	public boolean isAnimating() {
		return animating;
	}

	/**
	 * @param b
	 *            - set if animating
	 */
	public void setAnimating(boolean b) {
		this.animating = b;
	}

	/**
	 * Resets toolbar.
	 */
	public void reset() {
		resize();
		toolbarPanel.updateUndoRedoPosition();
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange() {
		if (lastOrientation != app.isPortrait()) {
			removeOpenStyles();
			removeCloseStyles();
		} else if (isOpen()) {
			removeCloseStyles();
		} else {
			removeOpenStyles();
		}
		updateStyle();

		lastOrientation = app.isPortrait();

		if (app.isPortrait()) {
			clearWidth();
			clearHeight();
			updateStyle();
		} else {
			if (!isOpen()) {
				int width = app.getAppletParameters().getDataParamWidth();
				if (app.getAppletParameters().getDataParamFitToScreen()) {
					width = NavigatorUtil.getWindowWidth();
				}
				toolbarPanel.setLastOpenWidth((int) (width
						* PerspectiveDecoder.landscapeRatio(app, width)));
			}
		}
	}

	private void clearWidth() {
		getElement().getStyle().clearWidth();
	}

	private void clearHeight() {
		getElement().getStyle().clearHeight();
	}

	/**
	 * Sets tab order for header buttons.
	 */
	public void setTabIndexes() {
		if (btnMenu != null) {
			new FocusableWidget(AccessibilityGroup.MENU, null, btnMenu).attachTo(app);
		}
		if (focusableMenuButton != null) {
			focusableMenuButton.attachTo(app);
		}
		setAltTexts();
	}

	/** Sets focus to Burger menu */
	public void focusMenu() {
		if (btnMenu != null) {
			btnMenu.getElement().focus();
		}
	}

	/**
	 * After visibility changed in landscape
	 */
	public void onLandscapeAnimationEnd() {
		if (!isOpen()) {
			getElement().getStyle().clearWidth();
			toolbarPanel.updateUndoRedoPosition();
		} else {
			toolbarPanel.resizeTabs();
		}
		toolbarPanel.onResize();

		Scheduler.get().scheduleDeferred(() -> {
			toolbarPanel.showHideUndoRedoPanel(true);
			toolbarPanel.updateUndoRedoPosition();
			resize();
		});
	}

	/**
	 * Exam info button.
	 */
	public void initInfoBtnAction() {
		final StandardButton examInfoBtn = GlobalHeader.INSTANCE
				.getExamInfoBtn();
		if (examInfoBtn == null) {
			return;
		}
		examInfoBtn.addFastClickHandler(source -> showExamDialog(examInfoBtn));
	}

	private void showExamDialog(StandardButton examInfoBtn) {
		new ExamLogAndExitDialog(app, true, examInfoBtn).show();
	}

	public void updateIcons(boolean exam) {
		updateIcons(toolbarPanel.getSelectedTabId(), exam);
	}

	private void updateIcons(TabIds tabId, boolean exam) {
		setSelected(btnAlgebra, tabId == TabIds.ALGEBRA, exam);
		setSelected(btnTools, tabId == TabIds.TOOLS, exam);
		setSelected(btnTableView, tabId == TabIds.TABLE, exam);
		setSelected(btnDistribution, tabId == TabIds.DISTRIBUTION, exam);
		setSelected(btnSpreadsheet, tabId == TabIds.SPREADSHEET, exam);
	}

	public void setAVIconNonSelect(boolean exam) {
		setSelected(btnAlgebra, false, exam);
	}

	/**
	 * @param context2d context
	 * @param left distance from left canvas edge
	 * @param top distance from top canvas edge
	 */
	public void paintToCanvas(CanvasRenderingContext2D context2d, int left, int top) {
		int btnTop = 40;
		context2d.globalAlpha = 0.54;
		for (StandardButton btn: new StandardButton[]{btnAlgebra, btnTools, btnTableView,
				btnDistribution, btnSpreadsheet}) {
			if (btn != null) {
				HTMLImageElement el = Js.uncheckedCast(btn.getImage().getElement());
				context2d.drawImage(el, left + 24, top + btnTop);
				btnTop += 72;
			}
		}
		context2d.globalAlpha = 1;
	}
}
