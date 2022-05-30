package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.LoginOperationW;

import com.google.gwt.user.client.ui.FlowPanel;

import elemental2.dom.File;

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
	public OpenFileView(AppW app, BrowserDevice.FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		common = new FileViewCommon(app, "Open");
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
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
		openFileBtn.setAcceptedFileType(".ggb");
		buttonPanel.add(openFileBtn);

		addGoogleDriveButton(buttonPanel);
		common.addToContent(buttonPanel);
	}

	private void addGoogleDriveButton(FlowPanel parent) {
		if (loggedInUserHasGoogleDrive()) {
			googleDriveBtn = new StandardButton(
					MaterialDesignResources.INSTANCE.google_drive(),
					app.getLocalization().getMenu("GoogleDrive"), 18);
			googleDriveBtn.addStyleName("containedButton");
			googleDriveBtn.addStyleName("buttonMargin16");

			googleDriveBtn.addFastClickHandler(source -> {
				if (app.getGoogleDriveOperation() != null) {
					app.getFileManager().setFileProvider(Material.Provider.GOOGLE);
					app.getGoogleDriveOperation()
							.requestPicker();
				}
			});
			parent.add(googleDriveBtn);
		}
	}

	private boolean loggedInUserHasGoogleDrive() {
		final GeoGebraTubeUser user = this.app.getLoginOperation().getModel()
				.getLoggedInUser();
		return user != null && user.hasGoogleDrive() && app.getLAF().supportsGoogleDrive();
	}

	@Override
	public boolean onOpenFile() {
		return false;
	}

	@Override
	public void openFile(final File fileToHandle) {
		app.openFile(fileToHandle);
		close();
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			googleDriveBtn.setVisible(event instanceof LoginEvent && loggedInUserHasGoogleDrive());
		}
	}

	@Override
	public AnimatingPanel getPanel() {
		return common;
	}

	@Override
	public void loadAllMaterials(int offset) {
		// fill
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void addMaterial(Material material) {
		// fill
	}
}
