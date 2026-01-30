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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.UndoInfoStoredListener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.general.GeneralIcon;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.PersistablePanel;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.SimplePanel;

import jsinterop.base.Js;

/**
 * Page Control Panel for navigating through multiple pages
 */
public class PageListPanel
		extends PersistablePanel implements SetLabels, CardListInterface, UndoInfoStoredListener {

	public static final int PAGE_OVERVIEW_WIDTH = 240;
	private final AppWFull app;
	private final GeoGebraFrameFull frame;
	private final EuclidianDockPanelW dockPanel;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private StandardButton plusButton;
	private final PageListController pageController;
	private boolean isTouch = false;
	private SimplePanel indicator;

	/**
	 * @param app
	 *            application
	 */
	public PageListPanel(AppWFull app) {
		this.app = app;
		this.frame = app.getAppletFrame();
		this.dockPanel = (EuclidianDockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		pageController = new PageListController(app, this);
		app.setPageController(pageController);
		app.getUndoManager().addUndoInfoStoredListener(this);
		initGUI();
	}

	private void initGUI() {
		addStyleName("mowPageControlPanel");
		addPlusButton();
		addContentPanel();
		addNewPreviewCard(true, "main");
		frame.add(this);
		setVisible(false);
		addBitlessDomHandler(pageController, TouchStartEvent.getType());
		addBitlessDomHandler(pageController, TouchMoveEvent.getType());
		addBitlessDomHandler(pageController, TouchEndEvent.getType());
		addDomHandler(pageController, MouseDownEvent.getType());
		addDomHandler(pageController, MouseMoveEvent.getType());
		addDomHandler(pageController, MouseUpEvent.getType());
		addDomHandler(pageController, MouseOutEvent.getType());
	}

	private void addContentPanel() {
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("mowPageControlScrollPanel");
		contentPanel = new PersistablePanel();
		contentPanel.addStyleName("mowPageControlContentPanel");
		scrollPanel.add(contentPanel);
		add(scrollPanel);
	}

	private void addPlusButton() {
		plusButton = new StandardButton(app.getGeneralIconResource()
				.getImageResource(GeneralIcon.PLUS).withFill(GColor.WHITE.toString()),
				null, 24, 24);
		plusButton.setStyleName("mowFloatingButton");
		plusButton.addStyleName("floatingActionButton");
		plusButton.addStyleName("mowPlusButton");
		// make sure clicking + does not select slides
		ClickStartHandler.initDefaults(plusButton, false, true);
		plusButton.addFastClickHandler(source -> {
			loadNewPage();
		});
		add(plusButton);
		showPlusButton(false);
	}

	/**
	 * Create and load a new page at specified index.
	 * @param atIndex define where to insert new page
	 */
	public void loadNewPage(int atIndex) {
		String id = PageListController.nextID();
		app.dispatchEvent(new Event(EventType.ADD_PAGE, null, id));
		int index = addNewPreviewCardAt(atIndex, id);
		pageController.loadNewPageStoreUndo(index);
	}

	/**
	 * Create and load a new page.
	 */
	public void loadNewPage() {
		String id = PageListController.nextID();
		app.dispatchEvent(new Event(EventType.ADD_PAGE, null, id));
		int index = addNewPreviewCard(false, id);
		pageController.loadNewPageStoreUndo(index);
	}

	/**
	 * @param doShow
	 *            - true if plus button should be visible, false otherwise
	 */
	protected void showPlusButton(boolean doShow) {
		if (plusButton == null) {
			return;
		}
		plusButton.addStyleName(
				doShow ? "showMowFloatingButton" : "hideMowFloatingButton");
		plusButton.removeStyleName(
				doShow ? "hideMowFloatingButton" : "showMowFloatingButton");
	}

	@Override
	public void open() {
		if (isVisible()) {
			return;
		}

		setVisible(true);
		int dockPanelWidth = dockPanel.getWidth() - PAGE_OVERVIEW_WIDTH;
		dockPanel.resizeView(dockPanelWidth, dockPanel.getHeight());

		setLabels();
		removeStyleName("animateOut");
		addStyleName("animateIn");
		CSSEvents.runOnAnimation(() -> showPlusButton(true), getElement(), "animateIn");
	}

	/**
	 * closes the page control panel
	 * 
	 * @return true if successful.
	 */
	public boolean close() {
		if (!isVisible()) {
			return false;
		}
		dockPanel.resizeView(dockPanel.getWidth(), dockPanel.getHeight());
		showPlusButton(false);
		addStyleName("animateOut");
		CSSEvents.runOnAnimation(this::onClose, getElement(), "animateOut");
		return true;
	}

	/**
	 * handles close actions after animation
	 */
	protected void onClose() {
		setVisible(false);
	}

	/**
	 * creates a new preview card
	 * 
	 * @param selected
	 *            true if added card should be linked to selected page, false otherwise
	 * 
	 * @return index of new slide
	 */
	protected int addNewPreviewCard(boolean selected, String id) {
		int index = pageController.getSlideCount();
		PagePreviewCard card = pageController.addNewPreviewCard(index, new GgbFile(id));
		if (selected) {
			pageController.setCardSelected(card);
		}
		addPreviewCard(pageController.getCard(index));
		return index;
	}

	/**
	 * Create a new preview card
	 * @param index at which new page will be inserted
	 * @param id generated ID for next slide
	 * @return index of new slide
	 */
	protected int addNewPreviewCardAt(int index, String id) {
		PagePreviewCard newCard = pageController.addNewPreviewCard(index, new GgbFile(id));
		addPreviewCard(newCard);
		pageController.updatePreviewImage();
		update();
		return newCard.getPageIndex();
	}

	private void addPreviewCard(final PagePreviewCard card) {
		if (card == null) {
			Log.error("preview card is null!");
			return;
		}
		final int pageIndex = card.getPageIndex();
		if (isTouch) {
			card.removeStyleName("desktop");
		}
		
		if (pageIndex < pageController.getSlideCount()) {
			contentPanel.insert(card, pageIndex);
		} else {
			contentPanel.add(card);
			scrollPanel.scrollToBottom();
		}
		card.setLabels();
	}

	/**
	 * remove preview card and associated slide
	 * 
	 * @param index
	 *            index of page to be removed
	 * 
	 */
	public void removePage(int index) {
		// invalid index
		if (index > pageController.getSlideCount()) {
			return;
		}
		// remove preview card
		contentPanel.remove(index);
		// remove associated ggb file
		pageController.removePage(index);
		update();
	}

	/**
	 * update index and titles above index
	 * 
	 * @param index
	 *            of card that should be updated
	 */
	@Override
	public void updateIndexes(int index) {
		// update only slides after deleted slide
		for (int i = index; i < contentPanel.getWidgetCount(); i++) {
			PagePreviewCard card = (PagePreviewCard) contentPanel.getWidget(i);
			if (card.getPageIndex() != i) {
				card.setPageIndex(i);
			}
		}
	}

	@Override
	public void setLabels() {
		// update labels of cards
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			((PagePreviewCard) contentPanel.getWidget(i)).setLabels();
		}
	}

	@Override
	public void reset() {
		contentPanel.clear();
		addNewPreviewCard(true, "main");
	}

	/**
	 * Duplicates page at given index.
	 * 
	 * @param src
	 *            to duplicate page at.
	 */
	public void pastePage(PagePreviewCard src, String targetID, String json) {
		PagePreviewCard dup = pageController.pasteSlideStoreUndo(src, targetID, json);
		addPreviewCard(dup);
		pageController.updatePreviewImage();
		update();
	}

	/**
	 * Rebuild the panel
	 */
	@Override
	public void update() {
		contentPanel.clear();
		int index = 0;
		for (PagePreviewCard card : this.pageController.getCards()) {
			card.setPageIndex(index++);
			addPreviewCard(card);
		}
		pageController.resetCardPositions();
	}

	@Override
	public void scrollBy(int diff) {
		elemental2.dom.Element elem = Js.uncheckedCast(scrollPanel.getElement());
		if (elem.scrollTop < elem.scrollHeight) {
			elem.scrollTop += diff;
		}
	}

	/**
	 * set true if touch event occurred
	 */
	public void setIsTouch() {
		isTouch = true;
		update();
	}

	/**
	 * If card is selected, persist slide in a file associated with the card
	 * @param card slide card
	 */
	public void saveSlide(PagePreviewCard card) {
		pageController.refreshSlide(card);
	}

	@Override
	public void updateContentPanelHeight() {
		int count = pageController.getCardCount();
		contentPanel.getElement().getStyle().setProperty("minHeight",
				PagePreviewCard.computeTop(count) + "px");
		contentPanel.getElement().getStyle().setProperty("maxHeight",
				PagePreviewCard.computeTop(count) + "px");
	}

	@Override
	public void onUndoInfoStored() {
		if (pageController != null) {
			pageController.updatePreviewImage();
		}
	}

	/**
	 * Show indicator after last card, so the user knows where the new page will be added.
	 */
	public void showIndicator() {
		if (indicator == null) {
			indicator = new SimplePanel();
			indicator.addStyleName("pageInsertIndicator");
		}
		if (!indicator.isAttached()) {
			contentPanel.add(indicator);
		}

		indicator.setVisible(true);
		PagePreviewCard lastCard = pageController.getCard(pageController.getSlideCount() - 1);
		indicator.getElement().getStyle().setTop(lastCard.getBottom(), Unit.PX);
	}

	/**
	 * Hide indicator (on context menu close).
	 */
	public void hideIndicator() {
		if (indicator != null) {
			indicator.setVisible(false);
		}
	}

}

