package org.geogebra.web.full.gui;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.multiplayer.MultiplayerResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.browser.CollaborationStoppedDialog;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.DateTimeFormat;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.util.GGBMultiplayer;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.core.JsArray;
import elemental2.core.JsDate;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

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
public class ShareControllerW implements ShareController {

	private final AppW app;
	private Widget anchor = null;
	private GGBMultiplayer multiplayer;
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
		runAfterLogin(this::shareAsLoggedIn);
	}

	@Override
	public void assign() {
		runAfterLogin(this::showAssignDialog);
	}

	private void shareAsLoggedIn() {
		AsyncOperation<Boolean> shareCallback = getShareCallback();
		// not saved as material yet
		Material activeMaterial = app.getActiveMaterial();
		boolean untitled = activeMaterial == null
				|| activeMaterial.getType() == Material.MaterialType.ggsTemplate;
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
		boolean untitled = activeMaterial == null
				|| activeMaterial.getType() == Material.MaterialType.ggsTemplate;
		if (untitled) {
			// not saved, logged in
			Material material = new Material(app.isWhiteboardActive() ? Material.MaterialType.ggs
					: Material.MaterialType.ggb);
			app.setActiveMaterial(material);
			LocalizationW loc = app.getLocalization();
			String formatDate = DateTimeFormat.formatDate(new JsDate(), loc.getLanguageTag());
			String appName = loc.getMenu(app.getConfig().getAppNameWithoutCalc());
			material.setTitle(loc.getPlain("assignDialog.titlePattern",
					appName, formatDate));
		}
		autoSaveMaterial(success -> callback.accept(app.getActiveMaterial()));
	}

	/**
	 * Create material and save online
	 */
	private void saveUntitledMaterial(AsyncOperation<Boolean> shareCallback) {
		SaveDialogI saveDialog = ((DialogManagerW) app.getDialogManager())
				.getSaveDialog(false);
		((SaveControllerW) app.getSaveController())
				.showDialogIfNeeded(shareCallback, true,
						false, false);
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
		if (app.isByCS() || activeMaterial == null) {
			// mebis can share private materials with group
			return false;
		}
		if ("P".equals(activeMaterial.getVisibility())) {
			activeMaterial.setVisibility(MaterialVisibility.Shared.getToken());
			return true;
		}
		return false;
	}

	private void runAfterLogin(Runnable afterLogin) {
		if (app.getLoginOperation().isLoggedIn()) {
			afterLogin.run();
		} else {
			app.getGuiManager().listenToLogin(afterLogin);
			app.getLoginOperation().showLoginDialog();
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
			if (getAppW().isByCS()) {
				DialogData data = new DialogData("Share", "Cancel", "Save");
				ShareDialogMow shareDialogMow = new ShareDialogMow(getAppW(), data,
						getAppW().getCurrentURL(sharingKey, true),
						null);
				shareDialogMow.setCallback(new MaterialCallback() {
					// empty callback, just to avoid NPEs
				});
				shareDialogMow.show();
			} else {
				DialogData data = new DialogData("Share",
						null, null);
				ShareLinkDialog shareDialog = new ShareLinkDialog(getAppW(), data,
						getAppW().getCurrentURL(sharingKey, true),
						getAnchor());
				shareDialog.show();
				shareDialog.center();
			}
		};
	}

	private void showAssignDialog() {
		isAssign = true;
		DialogData data = new DialogData("assignDialog.title", "Cancel", null);
		AssignDialog shareDialogMow = new AssignDialog(getAppW(), data,
				this);
		shareDialogMow.show();
	}

	/**
	 * 
	 * @return handler for native sharing
	 */
	public StringConsumer getShareStringHandler() {
		return s -> {
			String title = getAppW().getKernel().getConstruction()
					.getTitle();
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
	public void startMultiuser(String sharingKey) {
		getFrame().updateUndoRedoButtonVisibility(false);
		onMultiplayerLoad(sharingKey, ((ScriptManagerW) app.getScriptManager()).getApi(),
				mp -> {
					multiplayer = Js.uncheckedCast(mp);
					multiplayer.addUserChangeListener(this::handleMultiuserChange);
					multiplayer.start(app.getLoginOperation().getUserName());
				});
	}

	private GeoGebraFrameFull getFrame() {
		return  ((AppWFull) app).getAppletFrame();
	}

	@Override
	public void saveAndTerminateMultiuser(Material mat, MaterialCallbackI after) {
		if (!terminateActiveMultiuser(mat)) {
			// temporary instance, do not store
			AppletParameters parameters = new AppletParameters(
					app.getAppletParameters().getDataParamAppName());
			AppWFull appF = (AppWFull) app;
			Element el = DOM.createElement("div");
			GDimension currentSize = app.getActiveEuclidianView().getSettings().getPreferredSize();
			GeoGebraFrameFull fr = new GeoGebraFrameFull(
					appF.getAppletFrame().getAppletFactory(), appF.getLAF(),
					appF.getDevice(), GeoGebraElement.as(el), parameters);
			fr.setOnLoadCallback(exportedApi -> {
				fr.getApp().getActiveEuclidianView().getSettings().setPreferredSize(currentSize);
				onMultiplayerLoad(mat.getSharingKeySafe(), exportedApi,
						mp -> saveAndTerminate(Js.uncheckedCast(mp), fr.getApp(), mat, after));
			});
			fr.runAsyncAfterSplash();
		}
	}

	@Override
	public void terminateMultiuser(Material mat, MaterialCallbackI after) {
		if (!terminateActiveMultiuser(mat)) {
			// temporary instance, do not store
			onMultiplayerLoad(mat.getSharingKeySafe(), null,
						mp -> Js.<GGBMultiplayer>uncheckedCast(mp).terminate());
		}
	}

	private boolean terminateActiveMultiuser(Material mat) {
		String sharingKey = mat.getSharingKeySafe();
		Material activeMaterial = app.getActiveMaterial();
		if (multiplayer != null && activeMaterial != null
				&& activeMaterial.getSharingKey().equals(sharingKey)) {
			multiplayer.terminate();
			getFrame().updateUndoRedoButtonVisibility(true);
			multiplayer = null;
			return true;
		}
		return false;
	}

	private void saveAndTerminate(GGBMultiplayer mp, AppW otherApp, Material mat,
			MaterialCallbackI after) {
		mp.addConnectionChangeListener(evt -> {
			if (evt.connected) {
				MaterialCallback cb = new MaterialCallback() {
					@Override
					public void onLoaded(List<Material> result, Pagination meta) {
						mat.setFileName(result.get(0).getFileName());
						mp.terminate();
						if (after != null) {
							after.onLoaded(result, meta);
						}
					}
				};
				otherApp.getGgbApi().getBase64(true, base64 -> {
					app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(
							mat.getSharingKeySafe(), mat.getVisibility(), mat.getTitle(),
							base64, cb, mat.getType(), false);
					mp.terminate();
				});
			}
		});
		mp.start(app.getLoginOperation().getUserName());
	}

	@Override
	public void disconnectMultiuser() {
		if (multiplayer != null) {
			multiplayer.disconnect();
			updateUserChips(JsArray.of());
			multiplayer = null;
		}
	}

	private void handleMultiuserChange(JsArray<Object> users) {
		if (users.length == 0
				&& app.getActiveMaterial() != null
				&& !app.getLoginOperation().getResourcesAPI().owns(app.getActiveMaterial())) {
			CollaborationStoppedDialog dialog = new CollaborationStoppedDialog(app);
			dialog.show();
		}
		updateUserChips(users);
	}

	private void updateUserChips(JsArray<Object> users) {
		if (GeoGebraGlobal.getGgbMultiplayerChange() != null) {
			GeoGebraGlobal.getGgbMultiplayerChange().call(DomGlobal.window, users);
		}
	}

	private void onMultiplayerLoad(String sharingKey, Object api, Consumer<Object> callback) {
		GeoGebraTubeUser loggedInUser =
				app.getLoginOperation().getModel().getLoggedInUser();
		GWT.runAsync(GGBMultiplayer.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Log.error("Multiplayer script failed to load");
			}

			@Override
			public void onSuccess() {
				String paramMultiplayerUrl = app.getAppletParameters().getParamMultiplayerUrl();
				JavaScriptInjector.inject(MultiplayerResources.INSTANCE.multiplayer());
				JsPropertyMap<?> config = JsPropertyMap.of("collabUrl", paramMultiplayerUrl);
				String hostname = DomGlobal.location.hostname;
				String teamId = hostname.replaceAll("\\W", "") + "_" + sharingKey;
				GGBMultiplayer multiplayer = new GGBMultiplayer(api, teamId, config,
						loggedInUser.getJWTToken());
				callback.accept(multiplayer);
			}
		});
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
