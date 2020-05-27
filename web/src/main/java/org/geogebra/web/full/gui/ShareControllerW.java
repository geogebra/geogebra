package org.geogebra.web.full.gui;

import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.Widget;

/**
 * If no existent material -> show save dialog and ask for title to save
 * 
 * If material existent -> always auto save before share
 * 
 * Share with group -> material stays private (visibility)
 * 
 * Share by link -> material will be shared (visibility)
 * 
 * @author laszlo
 *
 */
public class ShareControllerW implements ShareController {

	private AppW app;
	private Widget anchor = null;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            the application.
	 */
	public ShareControllerW(AppW app) {
		this.app = app;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return app;
	}

	@Override
	public void share() {
		AsyncOperation<Boolean> shareCallback = getShareCallback();
		// not saved as material yet
		Material activeMaterial = app.getActiveMaterial();
		boolean untitled = activeMaterial == null
				|| activeMaterial.getType() == Material.MaterialType.ggsTemplate;
		if (untitled || "P".equals(activeMaterial.getVisibility())) {
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				loginForShare();
			} else {
				// not saved, logged in
				if (untitled) {
				    saveUntitledMaterial(shareCallback);
				} else {
					autoSaveMaterial(shareCallback);
				}
			}
		} else {
			// share public or shared material but not logged in
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				loginForShare();
			}
			// auto save changes of existent material before share
			else if (app.getActiveMaterial() != null
					&& app.getLoginOperation().isLoggedIn()) {
				autoSaveMaterial(shareCallback);
			}
		}
	}

	/**
	 * Create material and save online
	 */
	private void saveUntitledMaterial(AsyncOperation<Boolean> shareCallback) {
		((SaveControllerW) app.getSaveController())
				.showDialogIfNeeded(shareCallback, true, anchor);
	}

	private void autoSaveMaterial(AsyncOperation<Boolean> shareCallback) {
		app.getSaveController().saveActiveMaterial(shareCallback);
	}

	private void loginForShare() {
		app.getLoginOperation().getView().add(new EventRenderable() {
			@Override
			public void renderEvent(BaseEvent event) {
				if (event instanceof LoginEvent && ((LoginEvent) event).isSuccessful()) {
					share();
				}
			}
		});
		app.getLoginOperation().showLoginDialog();
	}

	private AsyncOperation<Boolean> getShareCallback() {
		return new AsyncOperation<Boolean>() {
			protected ShareLinkDialog shareDialog;
			protected ShareDialogMow shareDialogMow;

			@Override
			public void callback(Boolean active) {
				if (!active) {
					return;
				}

				String sharingKey = "";
				Material activeMaterial = getAppW().getActiveMaterial();
				if (activeMaterial != null && activeMaterial.getSharingKey() != null) {
					sharingKey = activeMaterial.getSharingKey();
				}
				if (getAppW().isMebis()) {
					DialogData data = new DialogData("Share", "Cancel", "Save");
					shareDialogMow = new ShareDialogMow(getAppW(), data,
							getAppW().getCurrentURL(sharingKey, true),
							null);
					shareDialogMow.show();
				} else {
					DialogData data = new DialogData("Share",
							null, null);
					shareDialog = new ShareLinkDialog(getAppW(), data,
							getAppW().getCurrentURL(sharingKey, true),
							getAnchor());
					shareDialog.show();
					shareDialog.center();
				}
			}
		};
	}

	/**
	 * 
	 * @return handler for native sharing
	 */
	public AsyncOperation<String> getShareStringHandler() {
		return new AsyncOperation<String>() {
			@Override
			public void callback(String s) {
				String title = getAppW().getKernel().getConstruction()
						.getTitle();
				MaterialsManagerI fm = getAppW().getFileManager();
				fm.nativeShare(s, "".equals(title) ? "construction" : title);
			}
		};
	}

	/**
	 * 
	 * @return anchor widget.
	 */
	public Widget getAnchor() {
		return anchor;
	}

	/**
	 * 
	 * @param anchor
	 *            widget to set.
	 */
	public void setAnchor(Widget anchor) {
		this.anchor = anchor;
	}

	@Override
	public void getBase64() {
		app.getGgbApi().getBase64(true, getShareStringHandler());
	}
}
