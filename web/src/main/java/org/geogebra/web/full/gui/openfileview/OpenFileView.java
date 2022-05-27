package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.LoginOperationW;

public class OpenFileView extends HeaderFileView
		implements OpenFileListener, EventRenderable {

	private final FileViewCommon common;
	private final AppW app;
	private final BrowserDevice.FileOpenButton openFileBtn;

	/**
	 * @param app - application
	 * @param openFileButton - button to open file picker
	 */
	public OpenFileView(AppW app, BrowserDevice.FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		common = new FileViewCommon(app, "Open", true);
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
		this.app.getLoginOperation().getView().add(this);
		app.registerOpenFileListener(this);
	}

	@Override
	public boolean onOpenFile() {
		return false;
	}

	@Override
	public void renderEvent(BaseEvent event) {
		// fill
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
