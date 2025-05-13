package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.IconButtonCardContextMenu;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;

/**
 * Context menu of the preview card.
 */
public class PagePreviewCardContextMenu extends IconButtonCardContextMenu {

	private final PagePreviewCard card;
	private MenuItemController menuItemController;
	private AriaMenuItem paste;

	/**
	 * @param app application
	 * @param card associated preview card
	 */
	public PagePreviewCardContextMenu(AppWFull app, PagePreviewCard card) {
		super(app);
		this.card = card;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		menuItemController = new MenuItemController(appW, wrappedPopup.getPopupPanel(), card);
		addCutItem();
		addCopyItem();
		addPasteItem();
		addDeleteItem();
		addSeparator();
		addNewPage();
		addDuplicatePage();
		addSeparator();
		addRenameItem();
	}

	private void addSeparator() {
		wrappedPopup.addSeparator();
	}

	private void addNewPage() {
		addItem(MaterialDesignResources.INSTANCE.add_black(),
				loc.getMenu("ContextMenu.NewPage"),
				menuItemController.addNewPage(card.getPageIndex() + 1));
	}

	private void addDuplicatePage() {
		addItem(MaterialDesignResources.INSTANCE.duplicatePage(),
				loc.getMenu("ContextMenu.DuplicatePage"), menuItemController.onDuplicatePage());
	}

	private void addRenameItem() {
		addItem(MaterialDesignResources.INSTANCE.mow_rename(),
				loc.getMenu("Rename"), menuItemController.onRenamePage());
	}

	private void addDeleteItem() {
		addItem(MaterialDesignResources.INSTANCE.delete_black(),
				loc.getMenu("Delete"), menuItemController.onDelete());
	}

	private void addCutItem() {
		addItem(MaterialDesignResources.INSTANCE.cut_black(),
				loc.getMenu("Cut"), menuItemController.onCut());
	}

	private void addCopyItem() {
		addItem(MaterialDesignResources.INSTANCE.copy_black(),
				loc.getMenu("Copy"), menuItemController.onCopy());
	}

	private void addPasteItem() {
		paste = addItem(MaterialDesignResources.INSTANCE.paste_black(),
				loc.getMenu("Paste"), menuItemController.onPaste(card, paste));
	}

	@Override
	protected void show() {
		super.show();
		menuItemController.updatePasteVisibility(paste);
	}

	/**
	 * position context menu
	 * @param x - horizontal position
	 * @param y - vertical position
	 */
	public void showAt(int x, int y) {
		wrappedPopup.showAtPoint(0, 0);

		int popupWidth = wrappedPopup.getPopupPanel().getOffsetWidth();
		int popupHeight = wrappedPopup.getPopupPanel().getOffsetHeight();

		int horPos = x - appW.getAppletFrame().getAbsoluteLeft();
		if (horPos + popupWidth > appW.getAppletWidth()) {
			horPos = appW.getAppletWidth() - popupWidth;
		}

		int vertPos = y - appW.getAppletFrame().getAbsoluteTop();
		if (vertPos + popupHeight > appW.getAppletHeight()) {
			vertPos = appW.getAppletHeight() - popupHeight;
		}

		menuItemController.updatePasteVisibility(paste);
		wrappedPopup.showAtPoint(horPos, vertPos);
	}
}
