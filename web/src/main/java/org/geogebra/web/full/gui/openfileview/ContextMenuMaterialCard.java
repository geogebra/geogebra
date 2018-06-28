package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ContextMenuCard;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * Context Menu of Material Cards
 * 
 * @author Alicia
 *
 */
public class ContextMenuMaterialCard extends ContextMenuCard {

	// true if user owns the Material, false otherwise
	private boolean ownMaterial;
	private Material material;

	/**
	 * @param app
	 *            application
	 * @param mat
	 *            associated material
	 */
	public ContextMenuMaterialCard(AppW app, Material mat) {
		super(app);
		this.material = mat;
		ownMaterial = app.getLoginOperation().owns(material);
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		if (ownMaterial) {
			addShareItem();
			addRenameItem();
		}
		addCopyItem();
		if (ownMaterial) {
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
		// TODO
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
