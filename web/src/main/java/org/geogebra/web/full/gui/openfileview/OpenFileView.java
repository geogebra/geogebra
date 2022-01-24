package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.main.BrowserDevice.FileOpenButton;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.File;

/**
 * View for browsing materials
 */
public class OpenFileView extends HeaderFileView
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

	private MaterialCallbackI ggtMaterialsCB;
	private MaterialCallbackI allMaterialsCB;
	private LoadSpinner spinner;

	private boolean materialListEmpty = true;

	private Order order = Order.timestamp;
	private static final Order[] map = new Order[] { Order.title, Order.created,
			Order.timestamp };

	private int materialCount = 0;

	/**
	 * @param app
	 *            application
	 * @param openFileButton
	 *            button to open file picker
	 */
	public OpenFileView(AppW app, FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		common = new FileViewCommon(app, "mow.openFileViewTitle");
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
		this.app.getLoginOperation().getView().add(this);
		app.registerOpenFileListener(this);
		initGUI();
	}

	private void initGUI() {
		this.allMaterialsCB = getUserMaterialsCB();
		this.ggtMaterialsCB = getGgtMaterialsCB();
		initSpinner();
		initButtonPanel();
		initSortDropdown();
	}

	private void initSpinner() {
		spinner = new LoadSpinner();
		common.addToContent(spinner);
	}

	/**
	 * adds content if available, notification otherwise
	 */
	protected void addContent() {
		common.clearContents();
		if (materialListEmpty) {
			common.showEmptyListNotification();
			setExtendedButtonStyle();
			common.addToContent(buttonPanel);
		} else {
			setSmallButtonStyle();
			common.addToContent(buttonPanel);
			common.addToContent(sortDropDown);
			common.addMaterialPanel();
		}
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
		closeAndResetFileAmount();
	}

	@Override
	public void openFile(final File fileToHandle) {
		app.openFile(fileToHandle);
		closeAndResetFileAmount();
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
	public MyHeaderPanel getPanel() {
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
		spinner.show();
		if (offset == 0) {
			clearMaterials();
		}
		LogInOperation loginOperation = app.getLoginOperation();
		if (loginOperation.isLoggedIn()) {
			loginOperation.getGeoGebraTubeAPI()
					.getUsersAndSharedMaterials(this.allMaterialsCB,
							order, offset);
		} else if (!loginOperation.getModel().isLoginStarted()) {
			loginOperation.getGeoGebraTubeAPI()
					.getFeaturedMaterials(this.ggtMaterialsCB);
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
				if (((MaterialCard) widget).getMaterial().getSharingKeyOrId()
						.equals(material.getSharingKeyOrId())) {
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
		case timestamp:
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
					ArrayList<Chapter> meta) {
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
		spinner.hide();
	}

	private MaterialCallback getGgtMaterialsCB() {
		return new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				exception.printStackTrace();
				Log.warn(exception.getMessage());
				spinner.hide();
			}

			@Override
			public void onLoaded(final List<Material> response,
					ArrayList<Chapter> meta) {
				addGGTMaterials(response, meta);
				addContent();
				spinner.hide();
			}
		};
	}

	/**
	 * adds the new materials (matList) - GeoGebraTube only
	 * 
	 * @param matList
	 *            List of materials
	 * @param chapters
	 *            list of book chapters
	 */
	public final void addGGTMaterials(final List<Material> matList,
			final ArrayList<Chapter> chapters) {
		materialListEmpty = matList.isEmpty();
		if (chapters == null || chapters.size() < 2) {
			for (final Material mat : matList) {
				addMaterial(mat);
			}
		}
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		closeAndResetFileAmount();
		app.checkSaved(callback);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			updateMaterials();
			if (event instanceof LogOutEvent) {
				closeAndResetFileAmount();
			}
		}
	}

	private void initLoadingMoreFilesButton(ArrayList<Chapter> meta) {
		int[] counts = meta.get(0).getMaterials();
		materialCount = counts[1];
		if (counts[1] < counts[2]) {
			loadMoreFilesPanel = new FlowPanel();
			Label loadMoreFilesText = new Label();
			loadMoreFilesText.setStyleName("loadMoreFilesLabel");
			loadMoreFilesText.setText(app.getLocalization()
					.getPlainDefault("ShowXofYfiles.Mebis", "Showing %0 of %1 files",
							String.valueOf(counts[1]), String.valueOf(counts[2])));
			loadMoreFilesPanel.add(loadMoreFilesText);
			addLoadMoreFilesButton();
			loadMoreFilesPanel.setStyleName("loadMoreFilesPanel");
			common.addMaterialOrLoadMoreFilesPanel(loadMoreFilesPanel);
		}
	}

	private void addLoadMoreFilesButton() {
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

	private void closeAndResetFileAmount() {
		materialCount = 0;
		close();
	}

	private void clearLoadingMoreFilesButton() {
		if (loadMoreFilesPanel != null) {
			loadMoreFilesPanel.removeFromParent();
		}
	}
}