package org.geogebra.web.full.gui.pagecontrolpanel;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.PreviewCardRenameDialog;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.shared.components.dialog.DialogData;

import elemental2.dom.DomGlobal;
import elemental2.dom.Response;

/**
 * Context Menu of Page Preview Cards
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
		addCutItem();
		addCopyItem();
		addPasteItem();
		addSeparator();
		addRenameItem();
		addDeleteItem();
	}

	private void addSeparator() {
		wrappedPopup.addSeparator();
	}

	private void addRenameItem() {
		addItem(MaterialDesignResources.INSTANCE.mow_rename(),
				loc.getMenu("Rename"), this::onRename);

	}

	private void onRename() {
		DialogData data = new DialogData("Rename");
		PreviewCardRenameDialog renameDialog = new PreviewCardRenameDialog(app, data, card);
		renameDialog.show();
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
		boolean oneSlide = app.getPageController().getSlideCount() == 1;
		app.dispatchEvent(new Event(oneSlide ? EventType.CLEAR_SLIDE
				: EventType.REMOVE_SLIDE, null,
				card.getPageIndex() + ""));
		frame.getPageControlPanel().removePage(card.getPageIndex());
	}

	/**
	 * execute duplicate action
	 */
	private void onPaste() {
		hide();
		String url = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE);
		DomGlobal.fetch(url).then(Response::text).then(text -> {
			app.dispatchEvent(new Event(EventType.PASTE_SLIDE)
					.setJsonArgument(getPasteJson(text)));
			frame.getPageControlPanel().pastePage(card, text);
			return null;
		}).catch_(err -> {
			// paste data from previous session -> delete
			BrowserStorage.LOCAL.removeItem(BrowserStorage.COPY_SLIDE);
			paste.setEnabled(false);
			return null;
		});
	}

	protected Map<String, Object> getPasteJson(String content) {
		Map<String, Object> pasteJson = new HashMap<>();
		pasteJson.put("cardIdx", card.getPageIndex());
		pasteJson.put("ggbFile", content);

		return pasteJson;
	}

	private void onCopy() {
		hide();
		frame.getPageControlPanel().saveSlide(card);
		String blob = CopyPasteW.asBlobURL(app.getGgbApi().toJson(card.getFile()));
		BrowserStorage.LOCAL.setItem(BrowserStorage.COPY_SLIDE, blob);
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(this, -122, 36);
		String slideContent = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE);
		if (paste != null) {
			paste.setEnabled(!StringUtil.empty(slideContent));
		}
	}
}
