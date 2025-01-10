package org.geogebra.web.full.gui.openfileview;

import java.util.Collection;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.models.ResourceAction;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.MaterialRenameDialog;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconResources;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import elemental2.core.Global;

/**
 * Context Menu of Material Cards
 */
public class ContextMenuButtonMaterialCard extends ContextMenuButtonCard {
	private final Material material;
	private final MaterialCard card;

	/**
	 * @param app
	 *            application
	 * @param mat
	 *            associated material
	 * @param card
	 *            related card
	 */
	public ContextMenuButtonMaterialCard(AppW app, Material mat,
			MaterialCard card) {
		super(app);
		this.material = mat;
		this.card = card;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		Collection<ResourceAction> actions = app.getLoginOperation()
				.getResourcesAPI().getActions(material);
		for (ResourceAction action: actions) {
			ResourceAction displayAction = app.getLAF().getDisplayAction(action);
			addItem(getActionIcon(displayAction), loc.getMenu(displayAction.getTranslationKey()),
					() -> getCommand(displayAction));
		}
	}

	private void getCommand(ResourceAction action) {
		switch (action) {
		case EDIT:
			card.openMaterial();
			break;
		case INSERT_ACTIVITY:
			insertActivity();
			break;
		case VIEW:
			Browser.openWindow(GeoGebraConstants.GEOGEBRA_WEBSITE
					+ "m/" + material.getSharingKeySafe());
			break;
		case COPY:
			card.copy();
			break;
		case SHARE:
			onShare();
			break;
		case DELETE:
			card.onDelete();
			break;
		case RENAME:
			onRename();
			break;
		}
		hide();
	}

	private void insertActivity() {
		app.getLoginOperation().getResourcesAPI()
				.getItem(material.getSharingKey(), new MaterialCallback() {

					@Override
					public void onLoaded(List<Material> parseResponse,
							Pagination meta) {
						String json = parseResponse.get(0).toJson().toString();
						if (GeoGebraGlobal.getLoadWorksheet() != null) {
							GeoGebraGlobal.getLoadWorksheet().accept(Global.JSON.parse(json));
						}
					}
				});
	}

	private SVGResource getActionIcon(ResourceAction action) {
		switch (action) {
		default:
		case EDIT:
			return MaterialDesignResources.INSTANCE.edit_black();
		case INSERT_ACTIVITY:
		case VIEW:
			return MaterialDesignResources.INSTANCE.visibility();
		case COPY:
			return MaterialDesignResources.INSTANCE.copy_black();
		case SHARE:
			return DefaultMenuIconResources.INSTANCE.exportFile();
		case DELETE:
			return MaterialDesignResources.INSTANCE.delete_black();
		case RENAME:
			return MaterialDesignResources.INSTANCE.mow_rename();
		}
	}

	/**
	 * execute share action
	 */
	protected void onShare() {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null && activeMaterial
				.getSharingKeySafe().equals(material.getSharingKeySafe())) {
			app.getShareController().share(); // make sure we save unsaved changes
			return;
		}
		DialogData data = new DialogData("Share", "Cancel", "Save");
		ShareDialogMow dialog = new ShareDialogMow(app, data,
				app.getCurrentURL(material.getSharingKey(), true), material);
		dialog.setCallback(new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					Pagination meta) {
				updateCardVisibility(result);
			}

			@Override
			public void onError(Throwable exception) {
				Log.debug(exception);
			}
		});
		dialog.show();
	}

	/**
	 * @param result
	 *            single material after visibility change
	 */
	protected void updateCardVisibility(List<Material> result) {
		card.updateVisibility(result.get(0));
		card.setThumbnail(result.get(0));
	}

	/**
	 * execute rename action
	 */
	protected void onRename() {
		hide();
		DialogData data = new DialogData("rename.resource", "Cancel", "Rename");
		MaterialRenameDialog renameDialog = new MaterialRenameDialog(
				app, data, card);
		renameDialog.show();
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(this, -130, 28);
	}
}