/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui;

import java.util.function.Consumer;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.AccessibilityAnalytics;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.DateTimeFormat;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Widget;

import elemental2.core.JsDate;

/**
 * If no existent material -&gt; show save dialog and ask for title to save
 *
 * <p>If material existent -&gt; always auto save before share
 *
 * <p>Share with group -&gt; material stays private (visibility)
 *
 * <p>Share by link -&gt; material will be shared (visibility)
 *
 * @author laszlo
 *
 */
public final class ShareControllerW implements ShareController {
	private final AppW app;
	private Widget anchor = null;
	private boolean isAssign = false;

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
		runAfterLogin(this::shareAsLoggedIn, AccessibilityAnalytics.Value.SHARE);
	}

	@Override
	public void assign() {
		runAfterLogin(this::showAssignDialog, AccessibilityAnalytics.Value.ASSIGN);
	}

	private void shareAsLoggedIn() {
		AsyncOperation<Boolean> shareCallback = getShareCallback();
		// not saved as material yet
		Material activeMaterial = app.getActiveMaterial();
		boolean untitled =
				activeMaterial == null || activeMaterial.getType() == Material.MaterialType.ggsTemplate;
		if (untitled) {
			// not saved, logged in
			saveUntitledMaterial(shareCallback);
		} else {
			// auto save changes of existent material before share
			autoSaveMaterial(shareCallback);
		}
	}

	/**
	 * Make sure current file is saved (using generated name if needed) and run a callback
	 * @param callback run after save
	 */
	public void afterSaved(Consumer<Material> callback) {
		Material activeMaterial = app.getActiveMaterial();
		boolean untitled =
				activeMaterial == null || activeMaterial.getType() == Material.MaterialType.ggsTemplate;
		if (untitled) {
			// not saved, logged in
			Material material = new Material(
					app.isWhiteboardActive() ? Material.MaterialType.ggs : Material.MaterialType.ggb);
			app.setActiveMaterial(material);
			LocalizationW loc = app.getLocalization();
			String formatDate = DateTimeFormat.formatDate(new JsDate(), loc.getLanguageTag());
			String appName = loc.getMenu(app.getConfig().getAppNameWithoutCalc());
			material.setTitle(loc.getPlain("assignDialog.titlePattern", appName, formatDate));
		}
		autoSaveMaterial(success -> callback.accept(app.getActiveMaterial()));
	}

	/**
	 * Create material and save online
	 */
	private void saveUntitledMaterial(AsyncOperation<Boolean> shareCallback) {
		app.getAccessibilityAnalyticsContext().setFlow(AccessibilityAnalytics.Value.SHARE);
		SaveDialogI saveDialog = ((DialogManagerW) app.getDialogManager()).getSaveDialog(false);
		((SaveControllerW) app.getSaveController())
				.showDialogIfNeeded(shareCallback, true, false, false);
		saveDialog.setDiscardMode();
	}

	private void autoSaveMaterial(AsyncOperation<Boolean> shareCallback) {
		boolean updatedVisibility = ensureMaterialSharingVisibility();
		if (app.isSaved() && !updatedVisibility) {
			shareCallback.callback(true);
		} else {
			app.getSaveController().saveActiveMaterial(shareCallback);
		}
	}

	private boolean ensureMaterialSharingVisibility() {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial == null) {
			return false;
		}
		if ("P".equals(activeMaterial.getVisibility())) {
			activeMaterial.setVisibility(MaterialVisibility.Shared.getToken());
			return true;
		}
		return false;
	}

	private void runAfterLogin(Runnable afterLogin, String flow) {
		if (app.getLoginOperation().isLoggedIn()) {
			afterLogin.run();
		} else {
			LogInOperation loginOperation = app.getLoginOperation();
			app.getAccessibilityAnalyticsContext().setFlow(flow);
			app.getGuiManager().listenToLogin(afterLogin);
			loginOperation.showLoginDialog();
		}
	}

	private AsyncOperation<Boolean> getShareCallback() {
		return active -> {
			if (!active) {
				return;
			}

			String sharingKey = "";
			Material activeMaterial = getAppW().getActiveMaterial();
			if (activeMaterial != null && activeMaterial.getSharingKey() != null) {
				sharingKey = activeMaterial.getSharingKey();
			}

			DialogData data = new DialogData("Share", null, null);
			ShareLinkDialog shareDialog = new ShareLinkDialog(
					getAppW(), data, getAppW().getCurrentURL(sharingKey, true), getAnchor());
			registerShareDialogShown();
			shareDialog.show();
			shareDialog.center();
		};
	}

	private void showAssignDialog() {
		isAssign = true;
		registerAssignDialogShown();
		DialogData data = new DialogData("assignDialog.title", "Cancel", null);
		AssignDialog assignDialog = new AssignDialog(getAppW(), data, this);
		assignDialog.show();
	}

	private void registerShareDialogShown() {
		AccessibilityAnalytics.logShareDialogShown(
				app.getAccessibilityAnalyticsContext().getTrigger());
	}

	private void registerAssignDialogShown() {
		AccessibilityAnalytics.logAssignDialogShown();
	}

	/**
	 *
	 * @return handler for native sharing
	 */
	public StringConsumer getShareStringHandler() {
		return s -> {
			String title = getAppW().getKernel().getConstruction().getTitle();
			MaterialsManagerI fm = getAppW().getFileManager();
			fm.nativeShare(s, "".equals(title) ? "construction" : title);
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

	@Override
	public void setAssign(boolean isAssign) {
		this.isAssign = isAssign;
	}

	@Override
	public boolean isAssign() {
		return isAssign;
	}
}
