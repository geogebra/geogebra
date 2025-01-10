package org.geogebra.web.full.gui.openfileview;

import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.models.ResourceOrdering;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.user.client.ui.FlowPanel;

public class OpenFileView extends HeaderFileView
		implements OpenFileListener, EventRenderable {

	private final FileViewCommon common;
	private final AppW app;
	private final BrowserDevice.FileOpenButton openFileBtn;
	private StandardButton googleDriveBtn;

	/**
	 * @param app - application
	 * @param openFileButton - button to open file picker
	 */
	public OpenFileView(AppWFull app, BrowserDevice.FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		app.ensureLoginOperation();
		common = new FileViewCommon(app, "Open", true);
		this.app.getLoginOperation().getView().add(this);
		if (app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}
		app.registerOpenFileListener(this);
		initGUI();
	}

	private void initGUI() {
		initButtonPanel();
	}

	private void initButtonPanel() {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("fileViewButtonPanel");

		openFileBtn.setImageAndText(
				MaterialDesignResources.INSTANCE.open_local_file()
						.getSafeUri().asString(),
				app.getLocalization().getMenu("OpenFileView.LocalFile"));
		openFileBtn.setAcceptedFileType(app.getFileExtension());
		if (app.enableFileFeatures()) {
			buttonPanel.add(openFileBtn);
		}

		addGoogleDriveButton(buttonPanel);
		common.addToContent(buttonPanel);
	}

	private void addGoogleDriveButton(FlowPanel parent) {
		googleDriveBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.google_drive(),
				app.getLocalization().getMenu("GoogleDrive"), 18);
		googleDriveBtn.addStyleName("containedButton");
		googleDriveBtn.addStyleName("buttonMargin16");

		googleDriveBtn.addFastClickHandler(source -> {
			if (app.getGoogleDriveOperation() != null) {
				app.getFileManager().setFileProvider(Material.Provider.GOOGLE);
				app.getGoogleDriveOperation().requestPicker();
			}
		});
		googleDriveBtn.setVisible(loggedInUserHasGoogleDrive());
		parent.add(googleDriveBtn);
	}

	private boolean loggedInUserHasGoogleDrive() {
		final GeoGebraTubeUser user = app.getLoginOperation().getModel()
				.getLoggedInUser();
		return user != null && user.hasGoogleDrive() && app.getLAF().supportsGoogleDrive();
	}

	@Override
	public boolean onOpenFile() {
		return false;
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			googleDriveBtn.setVisible(event instanceof LoginEvent && loggedInUserHasGoogleDrive());
			loadAllMaterials(0);
		}
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			common.onLogin((LoginEvent) event);
		}
		if (event instanceof LogOutEvent) {
			common.onLogout();
		}
	}

	@Override
	public AnimatingPanel getPanel() {
		return common;
	}

	@Override
	public void loadAllMaterials(int offset) {
		clearContentPanelAndShowSpinner();
		LogInOperation loginOperation = app.getLoginOperation();
		if (loginOperation.isLoggedIn()) {
			loginOperation.getResourcesAPI().getUsersMaterials(getCallback(),
					ResourceOrdering.created);
		} else if (!loginOperation.getModel().isLoginStarted()) {
			loginOperation.getResourcesAPI()
					.getFeaturedMaterials(getCallback());
		}
	}

	private InfoErrorData getInfoErrorData() {
		return new InfoErrorData("emptyMaterialList.caption",
				"emptyMaterialList.info", null, MaterialDesignResources.INSTANCE.search_black());
	}

	private MaterialCallbackI getCallback() {
		return new MaterialCallbackI() {

			@Override
			public void onLoaded(final List<Material> matList,
					Pagination meta) {
				if (matList.isEmpty()) {
					common.showEmptyListNotification(getInfoErrorData());
				} else {
					common.addContent();
					for (Material material : matList) {
						addMaterial(material);
					}
				}
				common.hideSpinner();
			}

			@Override
			public void onError(Throwable exception) {
				common.showEmptyListNotification(getInfoErrorData());
				common.hideSpinner();
			}
		};
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void addMaterial(Material material) {
		common.addMaterialCard(new MaterialCard(material, app));
	}

	@Override
	public void displaySearchResults(String query) {
		// protect backend from very long queries (would result in error anyway)
		if (StringUtil.emptyTrim(query) || query.length() > 300) {
			loadAllMaterials(0);
		} else {
			clearContentPanelAndShowSpinner();
			app.getLoginOperation().getResourcesAPI().search(
					query, getCallback());
		}
	}

	private void clearContentPanelAndShowSpinner() {
		common.clearMaterials();
		common.removeEmptyInfoPanel();
		common.showSpinner();
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}
