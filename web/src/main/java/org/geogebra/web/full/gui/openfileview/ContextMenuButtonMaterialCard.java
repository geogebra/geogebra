package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.MaterialRenameDialog;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * Context Menu of Material Cards
 * 
 * @author Alicia
 *
 */
public class ContextMenuButtonMaterialCard extends ContextMenuButtonCard {

	private Material material;

	/**
	 * @param app
	 *            application
	 * @param mat
	 *            associated material
	 */
	public ContextMenuButtonMaterialCard(AppW app, Material mat) {
		super(app);
		this.material = mat;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		if (app.getLoginOperation().owns(material)) {
			addShareItem();
			addRenameItem();
		}
		addCopyItem();
		if (app.getLoginOperation().owns(material)) {
			addDeleteItem();
		}
	}

	private void addShareItem() {
		addItem(MaterialDesignResources.INSTANCE.share_black().getSafeUri()
				.asString(), loc.getMenu("Share"), new Command() {
					@Override
					public void execute() {
						onShare();
					}
				});
	}

	private void addRenameItem() {
		addItem(MaterialDesignResources.INSTANCE.mow_rename().getSafeUri()
				.asString(), loc.getMenu("Rename"), new Command() {
					@Override
					public void execute() {
						onRename();
					}
				});
	}

	private void addCopyItem() {
		addItem(MaterialDesignResources.INSTANCE.copy_black().getSafeUri()
				.asString(), loc.getMenu("makeACopy"), new Command() {
					@Override
					public void execute() {
						onCopy();
					}
				});
	}

	private void addDeleteItem() {
		addItem(MaterialDesignResources.INSTANCE.delete_black().getSafeUri()
				.asString(), loc.getMenu("Delete"), new Command() {
					@Override
					public void execute() {
						onDelete();
					}
				});
	}

	/**
	 * execute share action
	 */
	protected void onShare() {
		hide();
		// TODO
	}

	/**
	 * execute rename action
	 */
	protected void onRename() {
		hide();
		MaterialRenameDialog renameDialog = new MaterialRenameDialog(app.getPanel(),
				app);
		renameDialog.show();
		renameDialog.center();
	}

	/**
	 * execute copy action
	 */
	protected void onCopy() {
		hide();
		// TODO
	}

	/**
	 * execute delete action
	 */
	protected void onDelete() {
		hide();
		// TODO
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(
				new GPoint(getAbsoluteLeft() - 130, getAbsoluteTop() + 28));
	}
}
