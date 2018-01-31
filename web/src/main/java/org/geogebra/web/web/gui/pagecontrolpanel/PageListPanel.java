package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
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
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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
		extends PersistablePanel implements MouseDownHandler, MouseMoveHandler,
		MouseUpHandler, SetLabels, CardListener {

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
		pageController = new PageListController(app);
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
			addDomHandler(this, MouseDownEvent.getType());
			addDomHandler(this, MouseMoveEvent.getType());
			addDomHandler(this, MouseUpEvent.getType());
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
		final PagePreviewCard card = pageController.addSlide();
		card.setCardListener(this);
		addPreviewCard(card);
		if (selected) {
			pageController.setCardSelected(card);
		}
		return card.getPageIndex();
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

	@Override
	public void loadPage(PagePreviewCard card) {
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
		// start with empty new page
		addNewPreviewCard(true);
	}

	/**
	 * Duplicates page at given index.
	 * 
	 * @param src
	 *            to duplicate page at.
	 */
	public void duplicatePage(PagePreviewCard src) {
		PagePreviewCard dup = pageController.duplicateSlide(src);
		dup.setCardListener(this);
		addPreviewCard(dup);
		pageController.updatePreviewImage();
	}

	/**
	 * Rebuild the panel
	 */
	public void update() {
		contentPanel.clear();
		for (PagePreviewCard card : this.pageController.getCards()) {
			addPreviewCard(card);
		}
	}

	@Override
	public void reorder(int srcIdx, int destIdx) {
		pageController.reorder(srcIdx, destIdx);
		update();
	}

	@Override
	public void dropTo(int x, int y) {
		if (pageController.dropTo(x, y)) {
			update();
		}
	}

	@Override
	public void hover(int pageIndex) {
		pageController.styleCard(pageIndex, "highlight");
	}

	@Override
	public void makeSpace(int pageIndex, boolean before) {
		pageController.styleCard(pageIndex, "spaceBeforeAnimated");
	}

	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
		CancelEventTimer.dragCanStart();
	}

	public void onMouseMove(MouseMoveEvent event) {
		int x = event.getClientX();
		int y = event.getClientY();

		if (CancelEventTimer.isDragStarted()) {
			app.getPageController().startDrag(x, y);
		} else if (CancelEventTimer.isDragging()) {
			int targetIdx = app.getPageController().drag(x, y);
			if (targetIdx != -1) {
				// divider.removeFromParent();
				contentPanel.insert(divider, targetIdx);
			}
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		int x = event.getClientX();
		int y = event.getClientY();

		app.getPageController().stopDrag();
		if (CancelEventTimer.isDragging()) {
			dropTo(x, y);
		} else {
			app.getPageController().loadPageAt(x, y);
		}
		CancelEventTimer.resetDrag();
	}
}	
