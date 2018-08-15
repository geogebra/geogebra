package org.geogebra.web.full.gui;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.ShareDialogW;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.SignInButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
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

	@Override
	public void share() {
		Runnable shareCallback = getShareCallback();

		if (app.getActiveMaterial() == null
				|| "P".equals(app.getActiveMaterial().getVisibility())) {
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				loginForShare();
			} else {
				// not saved, logged in
				((DialogManagerW) app.getDialogManager()).getSaveDialog()
						.setDefaultVisibility(MaterialVisibility.Shared)
						.showIfNeeded(shareCallback, true, anchor);
				// autoSaveMaterial(app);
			}
		} else {
			if (app.getActiveMaterial() != null && app.getLoginOperation().isLoggedIn()) {
				autoSaveMaterial();
			}
			// saved
			shareCallback.run();
		}

	}

	private void autoSaveMaterial() {
		if (app.has(Feature.SHARE_DIALOG_MAT_DESIGN) || app.has(Feature.MOW_SHARE_DIALOG)) {
			app.getSaveController().saveActiveMaterial();
		}
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
		((SignInButton) app.getLAF().getSignInButton(app)).login();
	}

	private Runnable getShareCallback() {
		return new Runnable() {
			protected ShareLinkDialog shareDialog;
			protected ShareDialogW sd;
			protected ShareDialogMow mowShareDialog;

			@Override
			public void run() {
				NoDragImage geogebraimg = new NoDragImage(
						AppResources.INSTANCE.geogebraLogo().getSafeUri().asString());
				PushButton geogebrabutton = new PushButton(geogebraimg, new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (!FileMenuW.nativeShareSupported()) {
							app.uploadToGeoGebraTube();
						} else {
							app.getGgbApi().getBase64(true, getShareStringHandler());
						}
						if (app.has(Feature.SHARE_DIALOG_MAT_DESIGN)) {
							shareDialog.hide();
						} else {
							sd.hide();
						}
					}

				});
				String sharingKey = "";
				if (app.getActiveMaterial() != null
						&& app.getActiveMaterial().getSharingKey() != null) {
					sharingKey = app.getActiveMaterial().getSharingKey();
				}
				if (app.has(Feature.SHARE_DIALOG_MAT_DESIGN) && app.isUnbundled()) {
					shareDialog = new ShareLinkDialog(app, app.getCurrentURL(sharingKey, true),
							anchor);
					shareDialog.setVisible(true);
					shareDialog.center();
				} else if (app.has(Feature.MOW_SHARE_DIALOG)) {
					mowShareDialog = new ShareDialogMow(app, app.getCurrentURL(sharingKey, true));
					mowShareDialog.show();
				} else {
					sd = new ShareDialogW(app, anchor, geogebrabutton,
							app.getCurrentURL(sharingKey, true));
					sd.setVisible(true);
					sd.center();
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
				String title = app.getKernel().getConstruction().getTitle();
				MaterialsManagerI fm = app.getFileManager();
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
