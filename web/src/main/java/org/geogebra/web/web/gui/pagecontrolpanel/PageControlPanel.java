package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
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
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page Control Panel for navigating through multiple pages
 * 
 * @author Alicia Hofstaetter
 * 
 */
public class PageControlPanel
		extends PersistablePanel implements SetLabels {

	private AppW app;
	private GeoGebraFrameBoth frame;
	private EuclidianDockPanelW dockPanel;
	private MOWToolbar mowToolbar;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private PagePreviewCard activePreviewCard;
	private StandardButton plusButton;
	private PageControlPanelController pageController;

	/**
	 * @param app
	 *            application
	 */
	public PageControlPanel(AppW app) {
		this.app = app;
		this.frame = ((AppWapplet) app).getAppletFrame();
		this.dockPanel = (EuclidianDockPanelW) (app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN));
		if (app.isWhiteboardActive()) {
			this.mowToolbar = frame.getMOWToorbar();
		}
		pageController = new PageControlPanelController(app);
		app.setPageController(pageController);
		initGUI();
	}

	private void initGUI() {
		addStyleName("mowPageControlPanel");
		addPlusButton();
		addContentPanel();
		addFirstPage();
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

	private void addFirstPage() {
		addNewPage();
		setCardSelected((PagePreviewCard) contentPanel.getWidget(0));
	}

	private void addPlusButton() {
		plusButton = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.add_white()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		plusButton.setStyleName("mowFloatingButton");
		plusButton.addStyleName("mowPlusButton");
		plusButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				loadPage(addNewPage());
				updatePreview();
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
		updatePreview();
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
	 * creates a new page and associated preview card
	 * 
	 * @return index of new slide
	 */
	protected int addNewPage() {
		int slideNumber = pageController.addSlide();
		addPreviewCard(app.getActiveEuclidianView(), slideNumber);
		return slideNumber;
	}

	private void addPreviewCard(EuclidianView view, final int slideNumber) {
		final PagePreviewCard previewCard = new PagePreviewCard(view,
				slideNumber);
		ClickStartHandler.init(previewCard, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				loadPage(previewCard.getPageIndex());
			}
		});
		contentPanel.add(previewCard);
		previewCard.setLabels();
		scrollPanel.scrollToBottom();
	}

	/**
	 * load existing page
	 * 
	 * @param index
	 *            index of page to load
	 */
	protected void loadPage(int index) {
		pageController.loadSlide(index);
		setCardSelected((PagePreviewCard) contentPanel.getWidget(index));
	}

	/**
	 * remove preview card and associated slide
	 * 
	 * @param index
	 *            index of page to be removed
	 * 
	 */
	public void removePage(int index) {
		if (index > pageController.getSlidesAmount()) {
			return;
		}
		int i = index;
		if (pageController.getSlidesAmount() > 1) {
			if (index == 0) {
				i++;
			} else {
				i--;
			}
			if (index == activePreviewCard.getPageIndex()) {
				loadPage(i);
			}
		} else {
			loadPage(addNewPage());
			updatePreview();
		}
		contentPanel.remove(index);
		pageController.removeSlide(index);
		updateIndexes();
	}

	/**
	 * Sets the selected page visible and highlights the preview card
	 * 
	 * @param previewCard
	 *            selected preview card
	 */
	protected void setCardSelected(PagePreviewCard previewCard) {
		deselectAllPreviewCards();
		previewCard.addStyleName("selected");
		activePreviewCard = previewCard;
	}

	/**
	 * Sets all preview cards to not selected
	 */
	protected void deselectAllPreviewCards() {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			((PagePreviewCard) contentPanel.getWidget(i))
					.removeStyleName("selected");
		}
	}

	/**
	 * Updates the preview image of the active preview card
	 */
	public void updatePreview() {
		if (activePreviewCard != null) {
			activePreviewCard.updatePreviewImage();
		}
	}

	private void updateIndexes() {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			PagePreviewCard card = (PagePreviewCard) contentPanel.getWidget(i);
			if (card.getPageIndex() != i) {
				card.setPageIndex(i);
			}
		}
	}

	@Override
	public void setLabels() {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			((PagePreviewCard) contentPanel.getWidget(i)).setLabels();
		}
	}

	/**
	 * resets the page control panel
	 */
	public void reset() {
		contentPanel.clear();
		addFirstPage();
	}
}