package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.gui.layout.scientific.SettingsAnimator;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.shared.components.ComponentSearchBar;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileViewCommon extends AnimatingPanel {

	private final AppW app;
	private final String title;
	// header
	private HeaderView headerView;
	private ComponentSearchBar searchBar;

	// content panel
	private FlowPanel contentPanel;
	// material panel
	private FlowPanel materialPanel;
	private final LocalizationW loc;

	/**
	 * @param app the application
	 * @param title the header title key.
	 * @param withSearch true if searchbar should be added to header
	 */
	public FileViewCommon(AppW app, String title, boolean withSearch) {
		loc = app.getLocalization();
		this.app = app;
		this.title = title;
		setAnimator(new SettingsAnimator(app.getAppletFrame(), this));
		initGUI(withSearch);
	}

	private void initGUI(boolean withSearch) {
		this.setStyleName("openFileView");
		addStyleName("panelFadeIn");
		initHeader(withSearch);
		initContentPanel();
		initMaterialPanel();
		setLabels();
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("materialPanel");
	}

	private void initHeader(boolean withSearch) {
		headerView = new HeaderView();
		headerView.setCaption(title);
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(source -> {
			updateAnimateOutStyle();
			CSSEvents.runOnAnimation(this::close, getElement(), getAnimateOutStyle());
		});

		if (withSearch) {
			addSearchBar();
		}
		this.setHeaderWidget(headerView);
	}

	private void addSearchBar() {
		searchBar = new ComponentSearchBar(app);
		getHeader().add(searchBar);
	}

	private void initContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	/**
	 * adds content if available
	 */
	public void addContent() {
		contentPanel.add(materialPanel);
	}

	/**
	 * Clear contents
	 */
	public void clearPanels() {
		if (contentPanel != null) {
			contentPanel.clear();
		}
	}

	@Override
	public void setLabels() {
		headerView.setCaption(localize(title));
		for (int i = 0; i < materialCount(); i++) {
			Widget widget = materialPanel.getWidget(i);
			if (widget instanceof MaterialCard) {
				((MaterialCard) widget).setLabels();
			}
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		resizeHeader();
	}

	@Override
	public void onResize() {
		super.onResize();
		resizeHeader();
	}

	/**
	 * update header style on resize
	 */
	public void resizeHeader() {
		boolean smallScreen = app.getAppletFrame()
				.hasSmallWindowOrCompactHeader();
		headerView.resizeTo(smallScreen);
		if (searchBar != null) {
			Dom.toggleClass(searchBar, "compact", smallScreen);
		}
	}

	/**
	 * adds MaterialPanel
	 */
	void addMaterialPanel() {
		contentPanel.add(materialPanel);
	}

	public void clearMaterials() {
		materialPanel.clear();
	}

	void showEmptyListNotification() {
		contentPanel.clear();
		contentPanel.add(getEmptyListNotificationPanel());
	}

	private FlowPanel getEmptyListNotificationPanel() {
		InfoErrorData data = new InfoErrorData("emptyMaterialList.caption.mow",
				"emptyMaterialList.info.mow");
		ComponentInfoErrorPanel noMaterials = new ComponentInfoErrorPanel(loc, data);
		return noMaterials;
	}

	String localize(String key) {
		return loc.getMenu(key);
	}

	/**
	 *
	 * @param card to add.
	 */
	public void addMaterialCard(Widget card) {
		materialPanel.add(card);
	}

	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	public void clearContents() {
		contentPanel.clear();
	}

	public boolean hasNoMaterials() {
		return materialCount() == 0;
	}

	public int materialCount() {
		return materialPanel.getWidgetCount();
	}

	public Widget materialAt(int index) {
		return materialPanel.getWidget(index);
	}

	public void addMaterialOrLoadMoreFilesPanel(Widget widget) {
		materialPanel.add(widget);
	}

	public void insertMaterial(Widget widget, int idx) {
		materialPanel.insert(widget, idx);
	}

	public HeaderView getHeader() {
		return headerView;
	}
}
