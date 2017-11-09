package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
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
 * Page Control Panel for navigating through multiple pages MOW-269
 * 
 * @author Alicia Hofstaetter
 * 
 */
public class PageControlPanel extends PersistablePanel {

	private AppW app;
	private GeoGebraFrameBoth frame;
	private EuclidianDockPanelW dockPanel;
	private MOWToolbar mowToolbar;
	private ScrollPanel scrollPanel;
	private PersistablePanel contentPanel;
	private PagePreviewCard activePreviewCard;
	private StandardButton plusButton;
	private boolean isAttached = false;

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
		initGUI();
	}

	private void initGUI() {
		addStyleName("mowPageControlPanel");
		addPlusButton();
		addContentPanel();
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
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.add_white()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		plusButton.setStyleName("mowFloatingButton");
		plusButton.addStyleName("mowPlusButton");
		plusButton.addFastClickHandler(new FastClickHandler() {
			public void onClick(Widget source) {
				addNewPage();
			}
		});
		add(plusButton);
		hidePlusButton();
	}

	/**
	 * Sets plus button visible
	 */
	protected void showPlusButton() {
		if (plusButton == null) {
			return;
		}
		plusButton.addStyleName("showMowFloatingButton");
		plusButton.removeStyleName("hideMowFloatingButton");
	}

	/**
	 * Hides plus button
	 */
	protected void hidePlusButton() {
		if (plusButton == null) {
			return;
		}
		plusButton.addStyleName("hideMowFloatingButton");
		plusButton.removeStyleName("showMowFloatingButton");
	}

	/**
	 * opens the page control panel
	 */
	public void open() {
		if (!isAttached) {
			frame.add(this);
			// add pages for testing
			addNewPage();
			isAttached = true;
		}
		updatePreview();
		setVisible(true);
		addStyleName("animateIn");
		final Style style = app.getFrameElement().getStyle();
		style.setOverflow(Overflow.HIDDEN);

		if (app.isWhiteboardActive()) {
			mowToolbar.hidePageControlButton();
			dockPanel.hideZoomPanel();
		}
		CSSAnimation.runOnAnimation(new Runnable() {
			public void run() {
				style.setOverflow(Overflow.VISIBLE);
				showPlusButton();
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
		hidePlusButton();
		addStyleName("animateOut");
		app.getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		CSSAnimation.runOnAnimation(new Runnable() {
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
			mowToolbar.showPageControlButton();
			dockPanel.showZoomPanel();
		}
		setVisible(false);
	}

	/**
	 * creates a new page and associated preview card
	 */
	protected void addNewPage() {
		// TODO create new page
		// replace app.getActiveEuclidianView() with new view
		addPreviewCard(app.getActiveEuclidianView());
	}

	private void addPreviewCard(EuclidianView view) {
		final PagePreviewCard previewCard = new PagePreviewCard(view,
				contentPanel.getWidgetCount());
		ClickStartHandler.init(previewCard, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setPageSelected(previewCard);
			}
		});
		contentPanel.add(previewCard);
		// set new page active
		setPageSelected(previewCard);
		scrollPanel.scrollToBottom();
	}

	/**
	 * remove preview card and associated view
	 * 
	 * @param previewCard
	 *            the card to be removed
	 */
	public void removePreviewCard(PagePreviewCard previewCard) {
		contentPanel.remove(previewCard);
		// TODO remove associated page also
		updateDefaultLabels();
	}

	private void updateDefaultLabels() {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			PagePreviewCard card = (PagePreviewCard) contentPanel.getWidget(i);
			if (card.getPageIndex() != i) {
				card.setPageIndex(i);
			}
		}
	}
	/**
	 * Sets the selected page visible and highlights the preview card
	 * 
	 * @param previewCard
	 *            selected preview card
	 */
	protected void setPageSelected(PagePreviewCard previewCard) {
		deselectAllPreviewCards();
		previewCard.addStyleName("selected");
		activePreviewCard = previewCard;
		// TODO set associated page visible
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
}