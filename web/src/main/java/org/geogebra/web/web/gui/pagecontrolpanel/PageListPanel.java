package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page Control Panel for navigating through multiple pages
 * 
 * @author Alicia Hofstaetter
 * 
 */
public class PageListPanel
		extends PersistablePanel implements SetLabels {

	private AppW app;
	private GeoGebraFrameBoth frame;
	private EuclidianDockPanelW dockPanel;
	private MOWToolbar mowToolbar;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private PagePreviewCard selectedPreviewCard;
	private StandardButton plusButton;
	private PageListController pageController;

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
				loadPage(addNewPreviewCard(false), true);
				updatePreviewImage();
			}
		});
		add(plusButton);
		showPlusButton(false);
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
		updatePreviewImage();
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
		addPreviewCard(card);
		if (selected) {
			setCardSelected(card);
		}
		return card.getPageIndex();
	}

	private void addPreviewCard(final PagePreviewCard card) {
		if (card == null) {
			Log.error("preview card is null!");
			return;
		}
		final int pageIndex = card.getPageIndex();
		ClickStartHandler.init(card, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				loadPage(pageIndex, false);
			}
		});

		if (pageIndex < pageController.getSlidesAmount()) {
			contentPanel.insert(card, pageIndex);

		} else {
			contentPanel.add(card);
		}
		card.setLabels();
		scrollPanel.scrollToBottom();
	}

	/**
	 * load existing page
	 * 
	 * @param index
	 *            index of page to load
	 * @param newPage
	 *            true if slide is new page
	 */
	protected void loadPage(int index, boolean newPage) {
		pageController.loadSlide(selectedPreviewCard, index, newPage);
		setCardSelected(pageController.getSlides().get(index));
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
		if (index > pageController.getSlidesAmount()) {
			return;
		}
		// remove preview card
		contentPanel.remove(index);
		// remove associated ggb file
		pageController.removeSlide(index);
		updateIndexes(index);
		// load new slide
		if (index == 0 && pageController.getSlidesAmount() == 0) {
			// first and single slide was deleted
			loadPage(addNewPreviewCard(true), true);
			updatePreviewImage();
		} else if (index == pageController.getSlidesAmount()) {
			// last slide was deleted
			loadPage(index - 1, false);
		} else {
			// otherwise
			loadPage(index, false);
		}
	}

	/**
	 * Sets the selected page visible and highlights the preview card
	 * 
	 * @param previewCard
	 *            selected preview card
	 */
	protected void setCardSelected(PagePreviewCard previewCard) {
		if (selectedPreviewCard != null) {
			// deselect old selected card
			selectedPreviewCard.removeStyleName("selected");
		}
		// select new card
		previewCard.addStyleName("selected");
		//
		selectedPreviewCard = previewCard;
	}

	/**
	 * Updates the preview image of the active preview card
	 */
	public void updatePreviewImage() {
		if (selectedPreviewCard != null) {
			selectedPreviewCard.updatePreviewImage();
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
	 * @param card
	 *            to duplicate page at.
	 */
	public void duplicatePage(PagePreviewCard card) {
		PagePreviewCard dup = pageController.duplicateSlide(card);
		addPreviewCard(dup);

		int idx = dup.getPageIndex();
		pageController.loadSlide(dup, idx, false);
		setCardSelected(dup);

		updateIndexes(idx);
		// loadPage(dup.getPageIndex(), false);
		updatePreviewImage();
	}
}