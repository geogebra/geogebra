package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

public class MaterialCardController {

	private AppW app;
	private Material material;
	private Runnable deleteCallback = new Runnable() {

		@Override
		public void run() {
			Log.debug("DELETE finished");
			MaterialCardController.this.app.getGuiManager().getBrowseView()
					.setMaterialsDefaultStyle();
		}

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
		app.setActiveMaterial(material);
		app.getGuiManager().getBrowseView().close();
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void loadGGBfromTube() {
		if (!StringUtil.empty(getMaterial().getFileName())) {
			load();
			return;
		}
		final long synced = getMaterial().getSyncStamp();
		app.getLoginOperation().getGeoGebraTubeAPI().getItem(
				getMaterial().getSharingKeyOrId() + "", new MaterialCallback() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						if (parseResponse.size() == 1) {
							setMaterial(parseResponse.get(0));
							getMaterial().setSyncStamp(synced);
							if (getMaterial().getType() == MaterialType.csv) {
								app.openCSV(Browser.decodeBase64(
										getMaterial().getBase64()));
							} else {
								app.getGgbApi()
										.setBase64(getMaterial().getBase64());
							}
							app.setActiveMaterial(getMaterial());
						} else {
							app.showError(app.getLocalization()
									.getError("LoadFileFailed"));
						}
					}

					@Override
					public void onError(Throwable error) {
						app.showError(app.getLocalization()
								.getError("LoadFileFailed"));
					}
				});

	}

	public void onConfirmDelete(final MaterialCardI card) {
		card.setVisible(false);
		setAllMaterialsDefault();
		final Material toDelete = this.getMaterial();

		if (app.getNetworkOperation().isOnline() && (toDelete.getId() > 0
				|| !StringUtil.empty(toDelete.getSharingKey()))) {
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
							app.showError(app.getLocalization()
									.getMenu("DeleteFailed"));
						}
					});
		} else {
			Log.debug("DELETE permanent");
			this.app.getFileManager().delete(toDelete, toDelete.getId() <= 0,
					this.deleteCallback);
		}

	}

	private void setAllMaterialsDefault() {
		app.getGuiManager().getBrowseView().setMaterialsDefaultStyle();
	}

	public Runnable getDeleteCallback() {
		return deleteCallback;
	}

}
