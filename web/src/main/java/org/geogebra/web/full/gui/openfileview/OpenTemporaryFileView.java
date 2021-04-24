package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.MessagePanel;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.main.BrowserDevice.FileOpenButton;
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
	// button panel
	private FlowPanel buttonPanel;
	private StandardButton newFileBtn;
	private FileOpenButton openFileBtn;

	// dropdown
	private ListBox sortDropDown;

	// material panel
	private FlowPanel materialPanel;
	private MaterialCallbackI ggtMaterialsCB;
	private MaterialCallbackI userMaterialsCB;
	private MaterialCallbackI sharedMaterialsCB;
	// info panel
	private FlowPanel infoPanel;
	private MessagePanel messagePanel;
	private LoadSpinner spinner;

	private boolean[] materialListEmpty = { true, true };
	private static final int TYPE_USER = 0;
	private static final int TYPE_SHARED = 1;

	private Order order = Order.timestamp;
	private static Order[] map = new Order[] { Order.title, Order.created,
			Order.timestamp };

	/**
	 * @param app
	 *            application
	 * @param openFileButton
	 *            button to open file picker
	 */
	public OpenTemporaryFileView(AppW app, FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
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
	}

	private void initSpinner() {
		spinner = new LoadSpinner("startscreen.notes");
	}

	/**
	 * adds content if available, notification otherwise
	 */
	protected void addContent() {
		contentPanel.clear();
		if (materialListEmpty[TYPE_USER] && materialListEmpty[TYPE_SHARED]) {
			showEmptyListNotification();
			setExtendedButtonStyle();
			infoPanel.add(buttonPanel);
		} else {
			setSmallButtonStyle();
			contentPanel.add(buttonPanel);
			contentPanel.add(sortDropDown);
			contentPanel.add(materialPanel);
		}
	}

	private void initHeader() {
		headerView = new HeaderView();
		headerView.setCaption(localize("mow.openFileViewTitle"));
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
		for (int i = 0; i < map.length; i++) {
			sortDropDown.addItem(localize(labelFor(map[i])));
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

		order = map[sortDropDown.getSelectedIndex() - 1];
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
		MessagePanel messagePanel = new MessagePanel();
		messagePanel.setImageUri(MaterialDesignResources.INSTANCE.mow_lightbulb());
		setMessagePanelLabels(messagePanel);
		return messagePanel;
	}

	private void setMessagePanelLabels(MessagePanel messagePanel) {
		messagePanel.setPanelTitle(localize("emptyMaterialList.caption.mow"));
		messagePanel.setPanelMessage(localize("emptyMaterialList.info.mow"));
	}

	private void setExtendedButtonStyle() {
		openFileBtn.setStyleName("extendedFAB");
		openFileBtn.addStyleName("FABwhite");
	}

	private void setSmallButtonStyle() {
		openFileBtn.setStyleName("containedButton");
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setMaterialsDefaultStyle() {
		if (materialPanel.getWidgetCount() == 0) {
			updateMaterials();
		}
	}

	@Override
	public void loadAllMaterials() {
		spinner.show();
		clearMaterials();
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

	/**
	 * update material list
	 */
	public void updateMaterials() {
		clearPanels();
		loadAllMaterials();
	}

	/**
	 * update temporary saved file list in exam mode.
	 */
	public void updateTemporaryFilesInExamMode() {
		clearPanels();
		loadTemporaryMaterials();
	}

	private void loadTemporaryMaterials() {
		spinner.show();
	}

	@Override
	public void disableMaterials() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSearchResults(List<Material> response,
			ArrayList<Chapter> chapters) {
		// TODO Auto-generated method stub
	}

	@Override
	public void displaySearchResults(String query) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rememberSelected(MaterialListElementI materialElement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLabels() {
		headerView.setCaption(localize("mow.openFileViewTitle"));
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
		for (int i = 0; i < materialPanel.getWidgetCount(); i++) {
			Widget widget = materialPanel.getWidget(i);
			if (widget instanceof MaterialCard
					&& isBeforeOrSame(material, ((MaterialCard) widget).getMaterial())) {
				if (((MaterialCard) widget).getMaterial().getSharingKeyOrId()
						.equals(material.getSharingKeyOrId())) {
					// don't add the same material twice
					return;
				}
				materialPanel.insert(new MaterialCard(material, app), i);
				return;
			}
		}
		materialPanel.add(new MaterialCard(material, app));
	}

	private boolean isBeforeOrSame(Material material, Material material2) {
		switch (order) {
		case title:
			return material.getTitle().compareTo(material2.getTitle()) <= 0;
		case created:
			return material.getDateCreated() >= material2.getDateCreated();
		case timestamp:
			return material.getTimestamp() >= material2.getTimestamp();
		default:
			return false;
		}

	}

	@Override
	public void removeMaterial(Material material) {
		// TODO Auto-generated method stub
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

	/**
	 * Adds the given {@link Material materials}.
	 * 
	 * @param matList
	 *            List of materials
	 * @param type
	 *            TYPE_USER or TYPE_SHARED
	 */
	protected void addUsersMaterials(final List<Material> matList, int type) {
		materialListEmpty[type] = matList.isEmpty();
		for (Material material : matList) {
			addMaterial(material);
		}
		spinner.hide();
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}