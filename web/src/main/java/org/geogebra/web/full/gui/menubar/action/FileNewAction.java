package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

import java.util.ArrayList;
import java.util.List;

/**
 * Clears construction and initializes a new one
 */
public class FileNewAction extends MenuAction<Void> implements AsyncOperation<Boolean> {
	private AppW app;

	/**
	 * @param app application
	 */
	public FileNewAction(AppW app) {
		super(app.getVendorSettings().getMenuLocalizationKey("New"),
				((AppWFull) app).getActivity().getResourceIconProvider().newFileMenu());
		this.app = app;
	}

	@Override
	public void callback(Boolean active) {
		if (app.isWhiteboardActive() && app.getLoginOperation() != null) {
			app.getLoginOperation().getGeoGebraTubeAPI().getTemplateMaterials(
					new MaterialCallbackI() {
						@Override
						public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
							if (result.isEmpty()) {
								onFileNew();
							} else {
								((GuiManagerW) app.getGuiManager()).getTemplateController()
										.fillTemplates(app, result);
								app.getDialogManager().showTemplateChooser();
							}
						}

						@Override
						public void onError(Throwable exception) {
							Log.error("Error on templates load");
						}
					});
			return;
		}
		onFileNew();
	}

	/**
	 * reset everything for new file
	 */
	public void onFileNew() {
		// ignore active: don't save means we want new construction
		app.setWaitCursor();
		app.fileNew();
		app.setDefaultCursor();

		if (!app.isUnbundledOrWhiteboard()) {
			app.showPerspectivesPopup();
		}
		if (app.getPageController() != null) {
			app.getPageController().resetPageControl();
		}
	}

	@Override
	public void execute(Void geo, AppWFull appW) {
		appW.getDialogManager().getSaveDialog().showIfNeeded(this);
	}
}
