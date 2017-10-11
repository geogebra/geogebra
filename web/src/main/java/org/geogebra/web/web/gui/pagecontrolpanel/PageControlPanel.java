package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.CSSAnimation;
import org.geogebra.web.web.gui.toolbar.mow.MOWToolbar;
import org.geogebra.web.web.gui.util.PersistablePanel;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page Control Panel for navigating through multiple pages MOW-269
 * 
 * @author Alicia Hofstaetter
 * 
 */
public class PageControlPanel extends PersistablePanel {

	private AppW app;
	private PersistablePanel contentPanel;
	private StandardButton closeButton;
	private boolean isAttached = false;
	private MOWToolbar mowToolbar;

	/**
	 * @param app
	 *            application
	 */
	public PageControlPanel(AppW app) {
		this.app = app;
		if (app.isWhiteboardActive()) {
			this.mowToolbar = ((AppWapplet) app).getAppletFrame()
					.getMOWToorbar();
		}
		initGUI();
	}

	private void initGUI() {
		addStyleName("pageControlPanel");
		addCloseButton();
		contentPanel = new PersistablePanel();
		add(contentPanel);
	}

	private void addCloseButton() {
		closeButton = new StandardButton(
				new ImageResourcePrototype(null,
						KeyboardResources.INSTANCE.keyboard_close_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		closeButton.setStyleName("closeButton");
		closeButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				close();
			}
		});
		add(closeButton);
	}

	/**
	 * opens the page control panel
	 */
	public void open() {
		if (!isAttached) {
			((AppWapplet) app).getAppletFrame().add(this);
			isAttached = true;
		}
		setVisible(true);
		addStyleName("animateIn");
		final Style style = app.getFrameElement().getStyle();
		style.setOverflow(Overflow.HIDDEN);

		if (app.isWhiteboardActive()) {
			mowToolbar.hidePageControlButton();
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
		setVisible(false);
		app.getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);

		if (app.isWhiteboardActive()) {
			mowToolbar.showPageControlButton();
		}
	}
}
