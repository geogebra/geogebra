package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.SimplePanel;

public class ComponentToast extends GPopupPanel {
	private SimplePanel content;
	public static final int TOAST_PADDING = 12;
	private static final int MIN_WIDTH = 300;

	/**
	 * constructor
	 * @param app - see {@link AppW}
	 * @param contentStr - content of the toast
	 */
	public ComponentToast(AppW app, String contentStr) {
		super(app.getAppletFrame(), app);
		addStyleName("toast");
		addStyleName(Shades.NEUTRAL_700.getName());
		buildGUI(contentStr);
		Dom.addEventListener(getElement(), "transitionend", evt -> {
			if (!getElement().hasClassName("fadeIn")) {
				removeFromParent();
			}
		});
	}

	private void buildGUI(String contentStr) {
		content = new SimplePanel();
		content.addStyleName("content");
		content.getElement().setInnerHTML(contentStr);
		add(content);
	}

	public void updateContent(String contentStr) {
		content.getElement().setInnerHTML(contentStr);
	}

	/**
	 * show toast animated and positioned
	 * @param left - left side of the editor
	 * @param top - top of the editor
	 * @param bottom - bottom of the editor
	 * @param width - distance from the left editor border to the right side of side panel
	 */
	public void show(int left, int top, int bottom, int width) {
		if (!isAttached()) {
			getRootPanel().add(this);
		}
		getElement().getStyle().clearWidth();
		int toastWidth = app.isPortrait() ? width - 16 : width;
		int distAVBottomKeyboardTop = (int) (app.getHeight() - bottom
				- ((AppW) app).getAppletFrame().getKeyboardHeight());
		int toastLeft = left;
		if (width < MIN_WIDTH && getOffsetWidth() > MIN_WIDTH) {
			toastWidth = Math.min(MIN_WIDTH, left + width);
			toastLeft = left + width - toastWidth;
		}
		getElement().getStyle().setWidth(toastWidth - 2 * TOAST_PADDING, Unit.PX);
		setPopupPosition(toastLeft, distAVBottomKeyboardTop >= getOffsetHeight()
				? bottom : top - getOffsetHeight());
		Scheduler.get().scheduleDeferred(() -> addStyleName("fadeIn"));
	}

	@Override
	public void hide() {
		removeStyleName("fadeIn");
	}
}
