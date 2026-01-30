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

package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.web.full.gui.util.IconButtonCardContextMenu;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.general.GeneralIcon;
import org.geogebra.web.html5.main.general.GeneralIconResource;

/**
 * Context menu of the preview card.
 */
public class PagePreviewCardContextMenu extends IconButtonCardContextMenu {

	private final PagePreviewCard card;
	private MenuItemController menuItemController;
	private AriaMenuItem paste;
	private final GeneralIconResource generalIconResource;

	/**
	 * @param app application
	 * @param card associated preview card
	 */
	public PagePreviewCardContextMenu(AppWFull app, PagePreviewCard card) {
		super(app);
		this.card = card;
		this.generalIconResource = app.getGeneralIconResource();
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
		addItem(generalIconResource.getImageResource(GeneralIcon.PLUS),
				loc.getMenu("ContextMenu.NewPage"),
				menuItemController.addNewPage(card.getPageIndex() + 1));
	}

	private void addDuplicatePage() {
		addItem(generalIconResource.getImageResource(GeneralIcon.DUPLICATE),
				loc.getMenu("ContextMenu.DuplicatePage"), menuItemController.onDuplicatePage());
	}

	private void addRenameItem() {
		addItem(generalIconResource.getImageResource(GeneralIcon.RENAME),
				loc.getMenu("Rename"), menuItemController.onRenamePage());
	}

	private void addDeleteItem() {
		addItem(generalIconResource.getImageResource(GeneralIcon.DELETE),
				loc.getMenu("Delete"), menuItemController.onDelete());
	}

	private void addCutItem() {
		addItem(generalIconResource.getImageResource(GeneralIcon.CUT),
				loc.getMenu("Cut"), menuItemController.onCut());
	}

	private void addCopyItem() {
		addItem(generalIconResource.getImageResource(GeneralIcon.COPY),
				loc.getMenu("Copy"), menuItemController.onCopy());
	}

	private void addPasteItem() {
		paste = addItem(generalIconResource.getImageResource(GeneralIcon.PASTE),
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
