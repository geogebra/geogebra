package org.geogebra.web.full.gui.pagecontrolpanel;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.toolbar.mow.NotesLayout;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.PersistablePanel;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.ScrollPanel;

import jsinterop.base.Js;

/**
 * Page Control Panel for navigating through multiple pages
 */
public class PageListPanel
		extends PersistablePanel implements SetLabels, CardListInterface {

	private final AppWFull app;
	private final GeoGebraFrameFull frame;
	private final EuclidianDockPanelW dockPanel;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private StandardButton plusButton;
	private final PageListController pageController;
	private boolean isTouch = false;

	@Nonnull
	private final NotesLayout notesLayout;

	/**
	 * @param app
	 *            application
	 */
	public PageListPanel(AppWFull app) {
		this.app = app;
		this.frame = app.getAppletFrame();
		this.dockPanel = (EuclidianDockPanelW) (app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN));
		this.notesLayout = frame.getNotesLayoutSafe(app);
		pageController = new PageListController(app, this);
		app.setPageController(pageController);
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
		plusButton = new StandardButton(
				MaterialDesignResources.INSTANCE.add_white(), null, 24);
		plusButton.setStyleName("mowFloatingButton");
		plusButton.addStyleName("floatingActionButton");
		plusButton.addStyleName("mowPlusButton");
		// make sure clicking + does not select slides
		ClickStartHandler.initDefaults(plusButton, false, true);
		plusButton.addFastClickHandler(source -> {
			String id = PageListController.nextID();
			app.dispatchEvent(new Event(EventType.ADD_PAGE, null, id));
			loadNewPage(false, id);
		});
		add(plusButton);
		showPlusButton(false);
	}

	/**
	 * Create and load a new page
	 * 
	 * @param selected
	 *            whether to select it
	 */
	public void loadNewPage(boolean selected, String id) {
		int index = addNewPreviewCard(selected, id);
		pageController.loadNewPageStoreUndo(index);
	}

	public void loadNewPage(boolean selected) {
		loadNewPage(selected, PageListController.nextID());
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

		dockPanel.hideZoomPanel();
		notesLayout.showPageControlButton(false);

		setVisible(true);
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
		showPlusButton(false);
		addStyleName("animateOut");
		CSSEvents.runOnAnimation(this::onClose, getElement(), "animateOut");
		return true;
	}

	/**
	 * handles close actions after animation
	 */
	protected void onClose() {
		if (app.isWhiteboardActive()) {
			notesLayout.showPageControlButton(true);
			dockPanel.showZoomPanel();
		}
		setVisible(false);
	}

	/**
	 * creates a new preview card
	 * 
	 * @param selected
	 *            true if added card should be selected, false otherwise
	 * 
	 * @return index of new slide
	 */
	protected int addNewPreviewCard(boolean selected, String id) {
		int index = pageController.getSlideCount();
		pageController.addNewPreviewCard(selected, index, new GgbFile(id));
		addPreviewCard(pageController.getCard(index));
		return index;
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

	@Override
	public int getVerticalScrollPosition() {
		return scrollPanel.getVerticalScrollPosition();
	}

	@Override
	public int getScrollParentHeight() {
		return scrollPanel.getParent().getOffsetHeight();
	}

	@Override
	public ScrollPanel getScrollPanel() {
		return scrollPanel;
	}

	/**
	 * set true if touch event occured
	 */
	public void setIsTouch() {
		isTouch = true;
		update();
	}

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
}

