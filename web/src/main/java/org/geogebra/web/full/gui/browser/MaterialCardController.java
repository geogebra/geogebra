package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.SaveControllerW;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.BackendAPIFactory;
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
		app.getViewW().processFileName(material.getFileName());
		updateActiveMaterial();
		app.getGuiManager().getBrowseView().close();
	}

	private void updateActiveMaterial() {
		app.setActiveMaterial(material);
		app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
		if (material.getType() == MaterialType.ggsTemplate) {
			app.registerOpenFileListener(this);
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
		if (!StringUtil.empty(getMaterial().getFileName())) {
			app.resetUrl();
			load();
			return;
		}

		final long synced = getMaterial().getSyncStamp();

		BackendAPI api;
		if (getMaterial().getType() == MaterialType.ggsTemplate) {
			api = new BackendAPIFactory(app).newMaterialRestAPI();
			api.setClient(app.getClientInfo());
		} else {
			api = app.getLoginOperation().getGeoGebraTubeAPI();
		}

		api.getItem(getMaterial().getSharingKeyOrId(), new MaterialCallback() {
			@Override
			public void onLoaded(final List<Material> parseResponse,
								 ArrayList<Chapter> meta) {
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
		} else if (getMaterial().getType() == MaterialType.ggsTemplate) {
			if (app.isMebis()) {
				app.getViewW().processFileName(material.getFileName());
			} else {
				app.getViewW().processFileName(material.getURL());
			}
		} else {
			app.getGgbApi().setBase64(getMaterial().getBase64());
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
			app.getLoginOperation().getGeoGebraTubeAPI()
					.deleteMaterial(toDelete, new MaterialCallback() {

						@Override
						public void onLoaded(List<Material> parseResponse,
								ArrayList<Chapter> meta) {
							Log.debug("DELETE local");
							card.remove();
							MaterialCardController.this.app.getFileManager()
									.delete(toDelete, true,
											MaterialCardController.this.deleteCallback);
						}

						@Override
						public void onError(Throwable exception) {
							Log.debug("DELETE backup");
							MaterialCardController.this.app.getFileManager()
									.delete(toDelete, false,
											MaterialCardController.this.deleteCallback);
							card.setVisible(true);
							app.showError(Errors.DeleteFailed);
						}
					});
		} else {
			Log.debug("DELETE permanent");
			this.app.getFileManager().delete(toDelete, toDelete.getId() <= 0,
					this.deleteCallback);
		}

	}

	private static boolean onlineFile(Material toDelete) {
		return toDelete.getId() > 0
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
	public void rename(final String text, final MaterialCardI card,
			final String oldTitle) {
		if (app.getNetworkOperation().isOnline()
				&& onlineFile(getMaterial())) {

			this.getMaterial().setTitle(text);
			app.getLoginOperation().getGeoGebraTubeAPI()
					.uploadRenameMaterial(this.getMaterial(),
							new MaterialCallback() {

								@Override
								public void onLoaded(
										List<Material> parseResponse,
										ArrayList<Chapter> meta) {
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

			app.getLoginOperation().getGeoGebraTubeAPI().copy(getMaterial(),
					MaterialRestAPI.getCopyTitle(app.getLocalization(),
							material.getTitle()),
					new MaterialCallback() {
						@Override
						public void onLoaded(List<Material> parseResponse,
								ArrayList<Chapter> meta) {
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
}
