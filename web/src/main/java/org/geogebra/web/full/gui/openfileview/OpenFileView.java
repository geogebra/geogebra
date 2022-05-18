package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.shared.ggtapi.LoginOperationW;

import com.google.gwt.user.client.ui.Widget;

public class OpenFileView extends HeaderFileView
		implements OpenFileListener, EventRenderable, FastClickHandler {

	private final FileViewCommon common;
	private final AppW app;
	/**
	 * @param app - application
	 * @param openFileButton - button to open file picker
	 */
	public OpenFileView(AppW app, BrowserDevice.FileOpenButton openFileButton) {
		this.app = app;
		common = new FileViewCommon(app, "Open");
		styleCommonFileView();
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
		this.app.getLoginOperation().getView().add(this);
		app.registerOpenFileListener(this);
		initGUI();
	}

	private void styleCommonFileView() {
		common.addStyleName("panelFadeIn");
		common.getHeader().getBackButton().addFastClickHandler(this);
		common.resizeHeader();
	}

	private void initGUI() {
	}

	@Override
	public boolean onOpenFile() {
		return false;
	}

	@Override
	public void renderEvent(BaseEvent event) {

	}

	@Override
	public AnimatingPanel getPanel() {
		return common;
	}

	@Override
	public void loadAllMaterials(int offset) {

	}

	@Override
	public void setLabels() {

	}

	@Override
	public void addMaterial(Material material) {

	}

	@Override
	public void onClick(Widget source) {
		if (source == common.getHeader().getBackButton()) {
			common.updateAnimateOutStyle();
			CSSEvents.runOnAnimation(this::close, common.getElement(),
					common.getAnimateOutStyle());
		}
	}
}
