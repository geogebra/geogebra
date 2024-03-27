package org.geogebra.web.full.gui.openfileview;

import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.models.ResourceOrdering;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice.FileOpenButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.Widget;

/**
 * View for browsing materials
 */
public class OpenFileViewMebis extends HeaderFileView
		implements OpenFileListener, EventRenderable {

	private final FileViewCommon common;
	/**
	 * application
	 */
	protected AppW app;

	private FlowPanel buttonPanel;
	private StandardButton newFileBtn;
	private final FileOpenButton openFileBtn;
	private FlowPanel loadMoreFilesPanel;

	private ListBox sortDropDown;
	private MaterialCallbackI allMaterialsCB;

	private boolean materialListEmpty = true;

	private ResourceOrdering order = ResourceOrdering.modified;
	private static final ResourceOrdering[] map = ResourceOrdering.values();

	/**
	 * @param app
	 *            application
	 * @param openFileButton
	 *            button to open file picker
	 */
	public OpenFileViewMebis(AppWFull app, FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		app.ensureLoginOperation();
		common = new FileViewCommon(app, "mow.openFileViewTitle", false);
		this.app.getLoginOperation().getView().add(this);
		app.registerOpenFileListener(this);
		initGUI();
	}

	private void initGUI() {
		this.allMaterialsCB = getUserMaterialsCB();
		initButtonPanel();
		initSortDropdown();
	}

	/**
	 * adds content if available, notification otherwise
	 */
	protected void addContent() {
		common.clearContents();
		if (materialListEmpty) {
			common.showEmptyListNotification(getInfoErrorData());
			setExtendedButtonStyle();
			common.addToContent(buttonPanel);
			buttonPanel.getParent().addStyleName("mebisEmptyFileView");
		} else {
			setSmallButtonStyle();
			common.addToContent(buttonPanel);
			common.addToContent(sortDropDown);
			common.addContent();
		}
	}

	private InfoErrorData getInfoErrorData() {
		return new InfoErrorData("emptyMaterialList.caption.mow",
				"emptyMaterialList.info.mow");
	}

	private void initButtonPanel() {
		buttonPanel = new FlowPanel();
		newFileBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.file_plus(),
				localize("New.Mebis"), 18);
		newFileBtn.addFastClickHandler(source -> newFile());
		openFileBtn.setImageAndText(
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder()
						.getSafeUri().asString(),
				localize("mow.offlineMyFiles"));
		openFileBtn.setAcceptedFileType(".ggs");
		buttonPanel.add(openFileBtn);
		buttonPanel.add(newFileBtn);
	}

	private void initSortDropdown() {
		sortDropDown = new ListBox();
		sortDropDown.setMultipleSelect(false);
		sortDropDown.addItem(localize("SortBy"));
		sortDropDown.getElement().getFirstChildElement()
				.setAttribute("disabled", "disabled");
		for (ResourceOrdering value : map) {
			sortDropDown.addItem(localize(labelFor(value)));
		}
		sortDropDown.setSelectedIndex(3);
		sortDropDown.addChangeHandler(event -> updateOrder());
	}

	private static String labelFor(ResourceOrdering order2) {
		switch (order2) {
		case created:
			return "sort_date_created";
		case modified:
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
		loadAllMaterials(0);
	}

	private String localize(String id) {
		return app.getLocalization().getMenu(id);
	}

	/**
	 * start a new file
	 */
	protected void newFile() {
		AsyncOperation<Boolean> newConstruction = active -> app.tryLoadTemplatesOnFileNew();
		app.getAppletParameters().setAttribute("perspective", "");
		app.getSaveController().showDialogIfNeeded(newConstruction, false);
		close();
	}

	private void setExtendedButtonStyle() {
		newFileBtn.setStyleName("extendedFAB");
		newFileBtn.addStyleName("FABteal");
		newFileBtn.addStyleName("buttonMargin24");
		openFileBtn.setStyleName("extendedFAB");
		openFileBtn.addStyleName("FABwhite");
		buttonPanel.setStyleName("fileViewButtonPanel");
		buttonPanel.addStyleName("center");
	}

	private void setSmallButtonStyle() {
		newFileBtn.setStyleName("containedButton");
		newFileBtn.addStyleName("buttonMargin16");
		openFileBtn.setStyleName("containedButton");
		buttonPanel.setStyleName("fileViewButtonPanel");
	}

	@Override
	public AnimatingPanel getPanel() {
		return common;
	}

	@Override
	public void setMaterialsDefaultStyle() {
		if (common.hasNoMaterials()) {
			updateMaterials();
		}
	}

	@Override
	public void loadAllMaterials(int offset) {
		common.showSpinner();
		if (offset == 0) {
			clearMaterials();
		}
		LogInOperation loginOperation = app.getLoginOperation();
		if (loginOperation.isLoggedIn()) {
			loginOperation.getResourcesAPI()
					.getUsersAndSharedMaterials(this.allMaterialsCB,
							order, offset);
		}
	}

	@Override
	public void clearMaterials() {
		common.clearMaterials();
	}

	/**
	 * update material list
	 */
	public void updateMaterials() {
		common.clearPanels();
		loadAllMaterials(0);
	}

	@Override
	public void setLabels() {
		newFileBtn.setText(localize("New.Mebis"));
		openFileBtn
				.setImageAndText(
						MaterialDesignResources.INSTANCE.mow_pdf_open_folder()
								.getSafeUri().asString(),
						localize("mow.offlineMyFiles"));
		if (sortDropDown != null) {
			sortDropDown.setItemText(0, localize("SortBy"));
			for (int i = 0; i < map.length; i++) {
				sortDropDown.setItemText(i + 1, localize(labelFor(map[i])));
			}
		}
		for (int idx = 0; idx < common.materialCount(); idx++) {
			Widget widget = common.materialAt(idx);
			if (widget instanceof MaterialCard) {
				((MaterialCard) widget).setLabels();
			}
		}
		common.setLabels();
	}

	@Override
	public void addMaterial(Material material) {
		for (int idx = 0; idx < common.materialCount(); idx++) {
			Widget widget = common.materialAt(idx);
			if (widget instanceof MaterialCard
					&& isBeforeOrSame(material, ((MaterialCard) widget).getMaterial())) {
				if (((MaterialCard) widget).getMaterial().getSharingKeySafe()
						.equals(material.getSharingKeySafe())) {
					// don't add the same material twice
					return;
				}
				common.insertMaterial(new MaterialCard(material, app), idx);
				return;
			}
		}
		common.addMaterialOrLoadMoreFilesPanel(new MaterialCard(material, app));
	}

	private boolean isBeforeOrSame(Material material, Material material2) {
		switch (order) {
		case title:
			return material.getTitle().compareTo(material2.getTitle()) <= 0;
		case created:
			return material.getDateCreated() >= material2.getDateCreated();
		case modified:
			return material.getTimestamp() >= material2.getTimestamp();
		default:
			return false;
		}

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

	private MaterialCallback getUserMaterialsCB() {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse,
					Pagination meta) {
				clearLoadingMoreFilesButton();
				addUsersMaterials(parseResponse);
				addContent();
				initLoadingMoreFilesButton(meta);
			}
		};
	}

	/**
	 * Adds the given {@link Material materials}.
	 * 
	 * @param matList
	 *            List of materials
	 */
	protected void addUsersMaterials(final List<Material> matList) {
		materialListEmpty = matList.isEmpty();
		for (Material material : matList) {
			addMaterial(material);
		}
		common.hideSpinner();
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			updateMaterials();
			if (event instanceof LogOutEvent) {
				close();
			}
		}
	}

	private void initLoadingMoreFilesButton(Pagination meta) {
		if (meta.to < meta.total) {
			loadMoreFilesPanel = new FlowPanel();
			Label loadMoreFilesText = new Label();
			loadMoreFilesText.setStyleName("loadMoreFilesLabel");
			loadMoreFilesText.setText(app.getLocalization()
					.getPlainDefault("ShowXofYfiles.Mebis", "Showing %0 of %1 files",
							String.valueOf(meta.to), String.valueOf(meta.total)));
			loadMoreFilesPanel.add(loadMoreFilesText);
			addLoadMoreFilesButton(meta.to);
			loadMoreFilesPanel.setStyleName("loadMoreFilesPanel");
			common.addMaterialOrLoadMoreFilesPanel(loadMoreFilesPanel);
		}
	}

	private void addLoadMoreFilesButton(int materialCount) {
		StandardButton loadMoreFilesButton = new StandardButton(localize("loadMore.Mebis"));
		loadMoreFilesButton.addFastClickHandler(source -> {
			loadMoreFilesButton.setText(null);
			loadMoreFilesButton.addStyleName("spinner-button");
			FlowPanel spinner = new FlowPanel();
			spinner.addStyleName("spinner-border");
			Label loading = new Label(localize("Loading"));
			DOM.appendChild(loadMoreFilesButton.getElement(), spinner.getElement());
			DOM.appendChild(loadMoreFilesButton.getElement(), loading.getElement());
			loadAllMaterials(materialCount);
		});
		loadMoreFilesButton.setStyleName("dialogContainedButton");
		loadMoreFilesPanel.add(loadMoreFilesButton);
	}

	private void clearLoadingMoreFilesButton() {
		if (loadMoreFilesPanel != null) {
			loadMoreFilesPanel.removeFromParent();
		}
	}
}
