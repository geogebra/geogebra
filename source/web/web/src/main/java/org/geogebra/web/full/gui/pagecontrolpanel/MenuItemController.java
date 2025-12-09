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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.ObjectLabelHandler;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.dialog.PreviewCardRenameDialog;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.Response;

public class MenuItemController {
	private final AppWFull appW;
	private final GeoGebraFrameFull frame;
	private final GPopupPanel contextMenu;
	private final PagePreviewCard card;

	/**
	 * Constructor
	 * @param appW application
	 * @param contextMenu context menu popup
	 * @param card of context menu (null for {@link PageControlPanelContextMenu})
	 */
	public MenuItemController(AppWFull appW, GPopupPanel contextMenu, PagePreviewCard card) {
		this.appW = appW;
		this.frame = appW.getAppletFrame();
		this.contextMenu = contextMenu;
		this.card = card;
	}

	/**
	 * @return cut action
	 */
	public Scheduler.ScheduledCommand onCut() {
		return () -> {
			onCopy().execute();
			onDelete().execute();
		};
	}

	/**
	 * @return copy action
	 */
	public Scheduler.ScheduledCommand onCopy() {
		return () -> {
			contextMenu.hide();
			String blob = CopyPasteW.asBlobURL(getContent(card));
			BrowserStorage.LOCAL.setItem(BrowserStorage.COPY_SLIDE, blob);
			BrowserStorage.LOCAL.setItem(BrowserStorage.COPY_SLIDE_OBJECTS, getObjectNames(card));
		};
	}

	/**
	 * @param pasteAfter after this card should the new one pasted
	 * @param paste menu item
	 * @return paste action
	 */
	public Scheduler.ScheduledCommand onPaste(PagePreviewCard pasteAfter, AriaMenuItem paste) {
		return () -> {
			contextMenu.hide();
			String url = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE);
			String objects = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE_OBJECTS);

			DomGlobal.fetch(url).then(Response::text).then(text -> {
				pastePage(pasteAfter, text, objects);
				return null;
			}).catch_(err -> {
				// paste data from previous session -> delete
				BrowserStorage.LOCAL.removeItem(BrowserStorage.COPY_SLIDE);
				BrowserStorage.LOCAL.removeItem(BrowserStorage.COPY_SLIDE_OBJECTS);
				if (paste != null) {
					paste.setEnabled(false);
				}
				return null;
			});
		};
	}

	/**
	 * @return delete action
	 */
	public Scheduler.ScheduledCommand onDelete() {
		return () -> {
			contextMenu.hide();
			boolean oneSlide = appW.getPageController().getSlideCount() == 1;
			appW.dispatchEvent(new Event(oneSlide ? EventType.CLEAR_PAGE
					: EventType.REMOVE_PAGE, null,
					card.getID()));
			frame.getPageControlPanel().removePage(card.getPageIndex());
		};
	}

	/**
	 * @return new page action
	 */
	public Scheduler.ScheduledCommand addNewPage(int atIndex) {
		return () -> {
			contextMenu.hide();
			frame.getPageControlPanel().loadNewPage(atIndex);
		};
	}

	/**
	 * @return duplicate action
	 */
	public Scheduler.ScheduledCommand onDuplicatePage() {
		return () -> {
			contextMenu.hide();
			pastePage(card, getContent(card), getObjectNames(card));
		};
	}

	/**
	 * @return rename page action
	 */
	public Scheduler.ScheduledCommand onRenamePage() {
		return () -> {
			DialogData data = new DialogData("Rename");
			PreviewCardRenameDialog renameDialog = new PreviewCardRenameDialog(appW, data, card);
			renameDialog.show();
		};
	}

	private void pastePage(PagePreviewCard pasteAfter, String text, String objects) {
		String targetID = PageListController.nextID();
		appW.dispatchEvent(new Event(EventType.PASTE_PAGE)
				.setJsonArgument(getPasteJson(pasteAfter, text, targetID, objects)));
		frame.getPageControlPanel().pastePage(pasteAfter, targetID, text);
	}

	protected Map<String, Object> getPasteJson(PagePreviewCard pasteAfter, String content,
			String targetId, String objects) {
		Map<String, Object> pasteJson = new HashMap<>();
		pasteJson.put("argument", targetId);
		pasteJson.put("to", pasteAfter.getPageIndex() + 1);
		pasteJson.put("ggbFile", content);
		pasteJson.put("targets", Global.JSON.parse(objects));
		return pasteJson;
	}

	private String getContent(PagePreviewCard card) {
		frame.getPageControlPanel().saveSlide(card);
		return appW.getGgbApi().toJson(card.getFile());
	}

	private String getObjectNames(PagePreviewCard card) {
		return Global.JSON.stringify(ObjectLabelHandler.findObjectNames(
				card.getFile().get("geogebra.xml").string));
	}

	/**
	 * Update visibility of paste item based on available internal clipboard content.
	 * @param pasteItem paste item
	 */
	public void updatePasteVisibility(@CheckForNull AriaMenuItem pasteItem) {
		if (pasteItem == null) {
			return;
		}
		String slideContent = BrowserStorage.LOCAL.getItem(BrowserStorage.COPY_SLIDE);
		if (StringUtil.empty(slideContent)) {
			pasteItem.setEnabled(false);
			return;
		}

		DomGlobal.fetch(slideContent).then(text -> {
			pasteItem.setEnabled(true);
			return null;
		}).catch_(error -> {
			pasteItem.setEnabled(false);
			return null;
		});
	}
}
