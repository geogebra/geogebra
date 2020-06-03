package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.MaterialRenameDialog;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ShareDialogMow;
import org.geogebra.web.shared.components.DialogData;

/**
 * Context Menu of Material Cards
 */
public class ContextMenuButtonMaterialCard extends ContextMenuButtonCard {
	private Material material;
	private MaterialCardI card;

	/**
	 * @param app
	 *            application
	 * @param mat
	 *            associated material
	 * @param card
	 *            related card
	 */
	public ContextMenuButtonMaterialCard(AppW app, Material mat,
			MaterialCardI card) {
		super(app);
		this.material = mat;
		this.card = card;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		if (app.getLoginOperation().getGeoGebraTubeAPI().owns(material)) {
			if (app.getLoginOperation().canUserShare()) {
				addShareItem();
			}
			addRenameItem();
		}
		addCopyItem();
		if (app.getLoginOperation().getGeoGebraTubeAPI().owns(material)) {
			addDeleteItem();
		}
	}

	private void addShareItem() {
		addItem(MaterialDesignResources.INSTANCE.share_black(),
				loc.getMenu("Share"), this::onShare);
	}

	private void addRenameItem() {
		addItem(MaterialDesignResources.INSTANCE.mow_rename(),
				loc.getMenu("Rename"), this::onRename);
	}

	private void addCopyItem() {
		addItem(MaterialDesignResources.INSTANCE.copy_black(),
				loc.getMenu("makeACopy"), this::onCopy);
	}

	private void addDeleteItem() {
		addItem(MaterialDesignResources.INSTANCE.delete_black(),
				loc.getMenu("Delete"), this::onDelete);
	}

	/**
	 * execute share action
	 */
	protected void onShare() {
		DialogData data = new DialogData("Share", "Cancel", "Save");
		ShareDialogMow dialog = new ShareDialogMow(app, data,
				app.getCurrentURL(material.getSharingKey(), true), material);
		dialog.setCallback(new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
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
		card.updateVisibility(result.get(0).getVisibility());
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

	/**
	 * execute copy action
	 */
	protected void onCopy() {
		card.copy();
		hide();
	}

	/**
	 * execute delete action
	 */
	protected void onDelete() {
		card.onDelete();
		hide();
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(
				new GPoint(getAbsoluteLeft() - 130, getAbsoluteTop() + 28));
	}
}