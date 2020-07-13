package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.BrowserStorage;

/**
 * Context Menu of Page Preview Cards
 * 
 * @author Alicia Hofstaetter
 *
 */
public class ContextMenuButtonPreviewCard extends ContextMenuButtonCard {

	private PagePreviewCard card;
	private AriaMenuItem paste;

	/**
	 * @param app
	 *            application
	 * @param card
	 *            associated preview card
	 */
	public ContextMenuButtonPreviewCard(AppW app, PagePreviewCard card) {
		super(app);
		this.card = card;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		addDeleteItem();
		addCutItem();
		addCopyItem();
		addPasteItem();
	}

	private void addDeleteItem() {
		addItem(MaterialDesignResources.INSTANCE.delete_black(),
				loc.getMenu("Delete"), this::onDelete);
	}

	private void addCutItem() {
		addItem(MaterialDesignResources.INSTANCE.cut_black(),
				loc.getMenu("Cut"), () -> {
			onCopy();
			onDelete();
		});
	}

	private void addCopyItem() {
		addItem(MaterialDesignResources.INSTANCE.copy_black(),
				loc.getMenu("Copy"), this::onCopy);
	}

	private void addPasteItem() {
		paste = addItem(MaterialDesignResources.INSTANCE.paste_black(),
				loc.getMenu("Paste"), this::onPaste);
	}

	/**
	 * execute delete action
	 */
	private void onDelete() {
		hide();
		frame.getPageControlPanel().removePage(card.getPageIndex());
	}

	/**
	 * execute duplicate action
	 */
	private void onPaste() {
		hide();
		frame.getPageControlPanel().pastePage(card,
				BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE));
	}

	private void onCopy() {
		hide();
		frame.getPageControlPanel().saveSlide(card);
		BrowserStorage.LOCAL.setItem(BrowserStorage.COPY_SLIDE,
				app.getGgbApi().toJson(card.getFile()));
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(
				new GPoint(getAbsoluteLeft() - 122, getAbsoluteTop() + 36));
		String slideContent = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE);
		if (paste != null && StringUtil.empty(slideContent)) {
			paste.setEnabled(false);
		}
	}
}
