package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSAnimation;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.toolbar.mow.MOWToolbar;
import org.geogebra.web.web.gui.util.PersistablePanel;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;

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
	private PersistablePanel contentPanel;
	private PagePreviewCard activePreviewCard;
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
		addStyleName("pageControlPanel");
		addContentPanel();
	}

	private void addContentPanel() {
		contentPanel = new PersistablePanel();
		contentPanel.addStyleName("contentPanel");
		add(contentPanel);
	}

	/**
	 * opens the page control panel
	 */
	public void open() {
		if (!isAttached) {
			frame.add(this);
			// add pages for testing
			addPreviewCard(app.getActiveEuclidianView());
			addPreviewCard(app.getActiveEuclidianView());
			addPreviewCard(app.getActiveEuclidianView());
			addPreviewCard(app.getActiveEuclidianView());
			addPreviewCard(app.getActiveEuclidianView());
			addPreviewCard(app.getActiveEuclidianView());
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
		// set first page active
		if (contentPanel.getWidgetCount() == 1) {
			setPageSelected(previewCard);
		}
	}

	/*
	 * private void removePreviewCard(PagePreviewCard previewCard) {
	 * contentPanel.remove(previewCard); // TODO remove associated page also //
	 * update default labels }
	 */

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