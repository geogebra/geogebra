package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.CSSAnimation;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.toolbar.mow.MOWToolbar;
import org.geogebra.web.web.gui.util.PersistablePanel;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page Control Panel for navigating through multiple pages
 * 
 * @author Alicia Hofstaetter
 * 
 */
public class PageListPanel
		extends PersistablePanel implements SetLabels, CardListInterface {

	private AppW app;
	private GeoGebraFrameBoth frame;
	private EuclidianDockPanelW dockPanel;
	private MOWToolbar mowToolbar;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private StandardButton plusButton;
	private PageListController pageController;
	private FlowPanel divider = null;
	/**
	 * @param app
	 *            application
	 */
	public PageListPanel(AppW app) {
		this.app = app;
		this.frame = ((AppWapplet) app).getAppletFrame();
		this.dockPanel = (EuclidianDockPanelW) (app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN));
		if (app.isWhiteboardActive()) {
			this.mowToolbar = frame.getMOWToorbar();
		}
		pageController = new PageListController(app, this);
		app.setPageController(pageController);
		initGUI();
	}

	private void initGUI() {
		addStyleName("mowPageControlPanel");
		addPlusButton();
		addContentPanel();
		addNewPreviewCard(true);
		frame.add(this);
		setVisible(false);
		if (app.has(Feature.MOW_DRAG_AND_DROP_PAGES)) {
			if (Browser.isTabletBrowser()) {
				addDomHandler(pageController, TouchStartEvent.getType());
				addDomHandler(pageController, TouchMoveEvent.getType());
				addDomHandler(pageController, TouchEndEvent.getType());
			} else {
				addDomHandler(pageController, MouseDownEvent.getType());
				addDomHandler(pageController, MouseMoveEvent.getType());
				addDomHandler(pageController, MouseUpEvent.getType());
			}
			divider = new FlowPanel();
			divider.setStyleName("mowPagePreviewCardDivider");
		}
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
				MaterialDesignResources.INSTANCE.add_white(), null, 24, app);
		plusButton.setStyleName("mowFloatingButton");
		plusButton.addStyleName("mowPlusButton");
		plusButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				loadNewPage(false);
			}
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
	protected void loadNewPage(boolean selected) {
		pageController.loadPage(addNewPreviewCard(selected), true);
		pageController.updatePreviewImage();
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

	/**
	 * opens the page control panel
	 */
	public void open() {
		if (app.isWhiteboardActive()) {
			dockPanel.hideZoomPanel();
			mowToolbar.showPageControlButton(false);
		}
		setVisible(true);
		pageController.updatePreviewImage();
		setLabels();
		addStyleName("animateIn");
		final Style style = app.getFrameElement().getStyle();
		style.setOverflow(Overflow.HIDDEN);
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				style.setOverflow(Overflow.VISIBLE);
				showPlusButton(true);
			}
		}, getElement(), "animateIn");
	}

	/**
	 * closes the page control panel
	 */
	public void close() {
		if (!isVisible()) {
			return;
		}
		showPlusButton(false);
		addStyleName("animateOut");
		app.getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				onClose();
			}
		}, getElement(), "animateOut");
	}

	/**
	 * handles close actions after animation
	 */
	protected void onClose() {
		app.getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);
		if (app.isWhiteboardActive()) {
			mowToolbar.showPageControlButton(true);
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
	protected int addNewPreviewCard(boolean selected) {
		int index = pageController.getSlideCount();
		pageController.addNewPreviewCard(selected, index, new GgbFile());
		addPreviewCard(pageController.getCards()
				.get(index));
		return index;
	}

	private void addPreviewCard(final PagePreviewCard card) {
		if (card == null) {
			Log.error("preview card is null!");
			return;
		}
		final int pageIndex = card.getPageIndex();
		if (!app.has(Feature.MOW_DRAG_AND_DROP_PAGES)) {
			ClickStartHandler.init(card, new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					loadPage(card);
				}
			});
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
	 * Loads the given card.
	 * 
	 * @param card
	 *            to load.
	 */
	void loadPage(PagePreviewCard card) {
		pageController.loadPage(card.getPageIndex(), false);
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
		pageController.removeSlide(index);
		app.getKernel().getConstruction().getUndoManager()
				.storeAction(EventType.REMOVE_SLIDE,
						new String[] { index + "" });
		updateIndexes(index);
		// load new slide
		if (index == 0 && pageController.getSlideCount() == 0) {
			// first and single slide was deleted
			loadNewPage(true);
		} else if (index == pageController.getSlideCount()) {
			// last slide was deleted
			pageController.loadPage(index - 1, false);
		} else {
			// otherwise
			pageController.loadPage(index, false);
		}
	}

	/**
	 * update index and titles above index
	 * 
	 * @param index
	 *            of card that should be updated
	 */
	private void updateIndexes(int index) {
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

	/**
	 * resets the page control panel
	 */
	public void reset() {
		contentPanel.clear();
		addNewPreviewCard(true);
	}

	/**
	 * Duplicates page at given index.
	 * 
	 * @param src
	 *            to duplicate page at.
	 */
	public void duplicatePage(PagePreviewCard src) {
		PagePreviewCard dup = pageController.duplicateSlideStoreUndo(src);
		addPreviewCard(dup);
		pageController.updatePreviewImage();
	}

	/**
	 * Rebuild the panel
	 */
	public void update() {
		contentPanel.clear();
		int index = 0;
		for (PagePreviewCard card : this.pageController.getCards()) {
			card.setPageIndex(index++);
			addPreviewCard(card);
		}
	}

	public void insertDivider(int targetIdx) {
		removeDivider();
		contentPanel.insert(divider, targetIdx);
	}

	public void removeDivider() {
		divider.removeFromParent();
	}
}	
