package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.MessagePanel;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.File;

/**
 * View for browsing materials
 */
public class OpenTemporaryFileView extends MyHeaderPanel
		implements BrowseViewI, OpenFileListener {

	/**
	 * application
	 */
	protected AppW app;
	// header
	private HeaderView headerView;

	// content panel
	private FlowPanel contentPanel;

	// dropdown
	private ListBox sortDropDown;

	// material panel
	private FlowPanel materialPanel;
	// info panel
	private FlowPanel infoPanel;
	private MessagePanel messagePanel;
	private LoadSpinner spinner;


	private static final Order[] map = new Order[] { Order.title, Order.created,
			Order.timestamp };

	/**
	 * @param app
	 *            application
	 *
	 */
	public OpenTemporaryFileView(AppW app) {
		this.app = app;
		app.registerOpenFileListener(this);
		initGUI();
	}

	private void initGUI() {
		this.setStyleName("openFileView");
		initSpinner();
		initHeader();
		initContentPanel();
		initSortDropdown();
		initMaterialPanel();
		setLabels();
	}

	private void initSpinner() {
		spinner = new LoadSpinner("startscreen.notes");
	}

	/**
	 * adds content if available
	 */
	protected void addContent() {
		contentPanel.clear();
		contentPanel.add(sortDropDown);
		contentPanel.add(materialPanel);
	}

	private Collection<Material> getMaterials() {
		return app.getExam().getTempStorage().collectTempMaterials();
	}

	private void initHeader() {
		headerView = new HeaderView();
		headerView.setCaption(("Open"));
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(source -> close());

		this.setHeaderWidget(headerView);
	}

	private void initContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	private void initSortDropdown() {
		sortDropDown = new ListBox();
		sortDropDown.setMultipleSelect(false);
		sortDropDown.addItem(localize("SortBy"));
		sortDropDown.getElement().getFirstChildElement()
				.setAttribute("disabled", "disabled");

		for (Order value : map) {
			sortDropDown.addItem(localize(labelFor(value)));
		}
		sortDropDown.setSelectedIndex(3);
		sortDropDown.addChangeHandler(event -> updateOrder());
	}

	private static String labelFor(Order order2) {
		switch (order2) {
		case created:
			return "sort_date_created";
		case timestamp:
			return "sort_last_modified";
		default:
		case title:
			return "sort_title";
		}
	}

	/**
	 * Reload materials sorted by another property.
	 */
	protected void updateOrder() {
		loadAllMaterials();
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("materialPanel");
	}

	private String localize(String id) {
		return app.getLocalization().getMenu(id);
	}

	@Override
	public void openFile(final File fileToHandle) {
		app.openFile(fileToHandle);
		close();
	}

	private void showEmptyListNotification() {
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("emptyMaterialListInfo");

		messagePanel = createMessagePanel();
		infoPanel.add(messagePanel);

		contentPanel.clear();
		contentPanel.add(infoPanel);
	}

	private MessagePanel createMessagePanel() {
		MessagePanel panel = new MessagePanel();
		panel.setImageUri(MaterialDesignResources.INSTANCE.mow_lightbulb());
		setMessagePanelLabels(panel);
		return panel;
	}

	private void setMessagePanelLabels(MessagePanel messagePanel) {
		messagePanel.setPanelTitle(localize("emptyMaterialList.caption.mow"));
		messagePanel.setPanelMessage(localize("emptyMaterialList.info.mow"));
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		// not used
	}

	@Override
	public void setMaterialsDefaultStyle() {
		// not used
	}

	@Override
	public void loadAllMaterials() {
		spinner.show();
		clearMaterials();
		if (getMaterials().isEmpty()) {
			showEmptyListNotification();
		} else {
			addContent();
			addTemporaryMaterials();
		}
	}

	@Override
	public void clearMaterials() {
		materialPanel.clear();
	}

	private void clearPanels() {
		if (contentPanel != null) {
			contentPanel.clear();
		}
		if (infoPanel != null) {
			infoPanel.clear();
		}
	}

	private void addTemporaryMaterials() {
		spinner.show();
		clearPanels();
		for (Material material : getMaterials()) {
			addMaterial(material);
		}
		contentPanel.add(materialPanel);
		spinner.hide();
	}

	@Override
	public void disableMaterials() {
		// not used
	}

	@Override
	public void onSearchResults(List<Material> response,
			ArrayList<Chapter> chapters) {
		// not used
	}

	@Override
	public void displaySearchResults(String query) {
		// not used
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		// not used
	}

	@Override
	public void rememberSelected(MaterialListElementI materialElement) {
		// not used
	}

	@Override
	public void setLabels() {
		headerView.setCaption(localize("Open"));
		if (sortDropDown != null) {
			sortDropDown.setItemText(0, localize("SortBy"));
			for (int i = 0; i < map.length; i++) {
				sortDropDown.setItemText(i + 1, localize(labelFor(map[i])));
			}
		}
		for (int i = 0; i < materialPanel.getWidgetCount(); i++) {
			Widget widget = materialPanel.getWidget(i);
			if (widget instanceof MaterialCard) {
				((MaterialCard) widget).setLabels();
			}
		}
		if (messagePanel != null) {
			setMessagePanelLabels(messagePanel);
		}
	}

	@Override
	public void addMaterial(Material material) {
		materialPanel.add(new TemporaryCard(material, app));
	}

	@Override
	public void removeMaterial(Material material) {
		// not used
	}

	@Override
	public boolean onOpenFile() {
		setConstructionTitleAsMaterial();
		return false;
	}

	private void setConstructionTitleAsMaterial() {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null) {
			app.getKernel().getConstruction().setTitle(
					activeMaterial.getTitle());
		}
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}