package org.geogebra.web.full.gui.browser;

import java.util.List;

import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.SaveControllerW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.openfileview.MaterialCard;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

/**
 * Controller for material cards, common for new and old UI.
 */
public class MaterialCardController implements OpenFileListener {
	/** application */
	protected AppW app;
	private Material material;
	/** callback for deleting materials */
	Runnable deleteCallback = () -> {
		Log.debug("DELETE finished");
		app.getGuiManager().getBrowseView()
				.setMaterialsDefaultStyle();
	};

	/**
	 * @param app
	 *            application
	 */
	public MaterialCardController(AppW app) {
		this.app = app;
	}

	/**
	 * Load current material
	 */
	private void load() {
		app.getArchiveLoader().processFileName(material.getFileName());
		updateActiveMaterial();
		app.getGuiManager().getBrowseView().close();
		((GeoGebraFrameFull) app.getAppletFrame())
				.updateUndoRedoButtonVisibility(!material.isMultiuser());
	}

	private void updateActiveMaterial() {
		app.setActiveMaterial(material);
		app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
		if (material.getType() == MaterialType.ggsTemplate) {
			app.registerOpenFileListener(this);
		} else {
			app.registerOpenFileListener(this::checkMultiuser);
		}
	}

	/**
	 * @return current material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param material
	 *            current material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Load online file.
	 */
	public void loadOnlineFile() {
		app.getShareController().setAssign(false);
		if (!StringUtil.empty(getMaterial().getFileName())) {
			app.resetUrl();
			load();
			return;
		}

		final long synced = getMaterial().getSyncStamp();

		MaterialRestAPI api = app.getLoginOperation().getResourcesAPI();

		api.getItem(getMaterial().getSharingKeySafe(), new MaterialCallback() {
			@Override
			public void onLoaded(final List<Material> parseResponse,
								 Pagination meta) {
				if (parseResponse.size() == 1) {
					setMaterial(parseResponse.get(0));
					getMaterial().setSyncStamp(synced);

					loadMaterial();

					updateActiveMaterial();
					app.resetUrl();
				} else {
					app.showError(Errors.LoadFileFailed);
				}
				app.getGuiManager().getBrowseView().close();
			}

			@Override
			public void onError(Throwable error) {
				app.showError(Errors.LoadFileFailed);
			}
		});
	}

	private void loadMaterial() {
		if (getMaterial().getType() == MaterialType.csv) {
			app.openCSV(Browser.decodeBase64(getMaterial().getBase64()));
		} else {
			app.getArchiveLoader().processFileName(material.getFileName());
		}
	}

	/**
	 * Remove file from the cloud and card from the UI.
	 * 
	 * @param card
	 *            card to be deleted
	 */
	public void onConfirmDelete(final MaterialCardI card) {
		card.setVisible(false);
		setAllMaterialsDefault();
		final Material toDelete = this.getMaterial();

		if (app.getNetworkOperation().isOnline() && onlineFile(toDelete)) {
			app.getLoginOperation().getResourcesAPI()
					.deleteMaterial(toDelete, new MaterialCallback() {

						@Override
						public void onLoaded(List<Material> parseResponse,
								Pagination meta) {
							card.remove();
							MaterialCardController.this.app.getFileManager()
									.delete(toDelete, true,
											MaterialCardController.this.deleteCallback);
							showSnackbar(app.getLocalization().getPlain(
									"ContextMenu.ConfirmDeleteA", toDelete.getTitle()));
							if (toDelete.isMultiuser()) {
								app.getShareController()
										.terminateMultiuser(toDelete, null);
							}
						}

						@Override
						public void onError(Throwable exception) {
							MaterialCardController.this.app.getFileManager()
									.delete(toDelete, false,
											MaterialCardController.this.deleteCallback);
							card.setVisible(true);
							showSnackbar(app.getLocalization().getMenu("ContextMenu.DeleteError"));
						}
					});
		} else {
			Log.debug("DELETE permanent");
			this.app.getFileManager().delete(toDelete, toDelete.getSharingKey() == null,
					this.deleteCallback);
		}
	}

	private void showSnackbar(String message) {
		app.getToolTipManager().showBottomMessage(message, app);
	}

	private static boolean onlineFile(Material toDelete) {
		return toDelete.getSharingKey() != null
				|| !StringUtil.empty(toDelete.getSharingKey());
	}

	private void setAllMaterialsDefault() {
		app.getGuiManager().getBrowseView().setMaterialsDefaultStyle();
	}

	/**
	 * @return callback for deleting materials
	 */
	public Runnable getDeleteCallback() {
		return deleteCallback;
	}

	/**
	 * @param text
	 *            new title
	 * @param card
	 *            card
	 * @param oldTitle
	 *            old title
	 */
	public void rename(final String text, final MaterialCard card,
			final String oldTitle) {
		if (app.getNetworkOperation().isOnline()
				&& onlineFile(getMaterial())) {

			this.getMaterial().setTitle(text);
			app.getLoginOperation().getResourcesAPI()
					.uploadRenameMaterial(this.getMaterial(),
							new MaterialCallback() {

								@Override
								public void onLoaded(
										List<Material> parseResponse,
										Pagination meta) {
									if (parseResponse.size() != 1) {
										app.showError(Errors.RenameFailed);
										card.rename(oldTitle);
									} else {
										Log.debug("RENAME local");
										getMaterial().setModified(parseResponse
												.get(0).getModified());
										getMaterial().setSyncStamp(parseResponse
												.get(0).getModified());
										if (getMaterial().getLocalID() <= 0) {
											return;
										}
										Log.debug("RENAME CALLBACK" + oldTitle
												+ "->" + text);
										getMaterial().setTitle(oldTitle);
										app.getFileManager().rename(
												text, getMaterial());
									}
								}
							});
		} else {
			this.getMaterial()
					.setModified(Math.max(SaveControllerW.getCurrentTimestamp(app),
							getMaterial().getSyncStamp() + 1));
			this.app.getFileManager().rename(text, this.getMaterial());
		}
	}

	/**
	 * Copy this material.
	 */
	public void copy() {
		if (app.getNetworkOperation().isOnline()
				&& onlineFile(getMaterial())) {

			app.getLoginOperation().getResourcesAPI().copy(getMaterial(),
					MaterialRestAPI.getCopyTitle(app.getLocalization(),
							material.getTitle()),
					new MaterialCallback() {
						@Override
						public void onLoaded(List<Material> parseResponse,
								Pagination meta) {
							if (parseResponse.size() == 1) {
								app.getGuiManager().getBrowseView()
									.addMaterial(parseResponse.get(0));
							}
						}
					});
		}
	}

	@Override
	public boolean onOpenFile() {
		app.getKernel().getConstruction().setTitle(null);
		app.setActiveMaterial(material);
		return true; // one time only
	}

	private boolean checkMultiuser() {
		String paramMultiplayerUrl = app.getAppletParameters().getParamMultiplayerUrl();
		GeoGebraTubeUser loggedInUser =
				app.getLoginOperation().getModel().getLoggedInUser();
		if (material.isMultiuser() && !StringUtil.empty(paramMultiplayerUrl)
				&& loggedInUser != null) {
			app.getShareController().startMultiuser(material.getSharingKeySafe());
		}
		return true; // one time only
	}

}
