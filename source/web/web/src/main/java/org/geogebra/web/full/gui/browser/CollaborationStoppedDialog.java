package org.geogebra.web.full.gui.browser;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;
import org.gwtproject.user.client.ui.Label;

public class CollaborationStoppedDialog extends ComponentDialog {

	/**
	 * Dialog for stopping the collaboration
	 * @param app application
	 */
	public CollaborationStoppedDialog(AppW app) {
		super(app, new DialogData("CollaborationStopped.title",
				"Leave", "Save"), false, true);
		Label content = new Label(app.getLocalization().getMenu("CollaborationStopped.text"));
		addDialogContent(content);
		setOnPositiveAction(() -> {
			app.getGgbApi().getBase64(true, this::uploadACopy);
		});
		setOnNegativeAction(app::resetOnFileNew);
	}

	private void uploadACopy(String base64) {
		Material mat = app.getActiveMaterial();
		Localization loc = app.getLocalization();
		String copyTitle = MaterialRestAPI.getCopyTitle(loc, mat.getTitle());
		MaterialCallback cb = new MaterialCallback() {
			@Override
			public void onLoaded(List<Material> result, Pagination meta) {
				app.setActiveMaterial(result.get(0));
				app.getKernel().getConstruction().setTitle(result.get(0).getTitle());
				((AppW) app).getToolTipManager().showBottomMessage(
						loc.getMenu("SavedSuccessfully"), (AppW) app);
			}
		};
		app.getLoginOperation().getGeoGebraTubeAPI()
				.uploadMaterial(null,
						MaterialVisibility.Private.getToken(),
						copyTitle, base64, cb, mat.getType(), false);
	}
}
