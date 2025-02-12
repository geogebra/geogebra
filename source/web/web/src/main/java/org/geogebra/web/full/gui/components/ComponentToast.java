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
	 * @param left - left side of the av cell
	 * @param top - top of the av cell
	 * @param bottom - bottom of the av cell
	 * @param width - width of av input panel
	 */
	public void show(int left, int top, int bottom, int width) {
		if (!isAttached()) {
			getRootPanel().add(this);
		}
		int toastWidth = app.isPortrait() ? width - 16 : width;
		getElement().getStyle().setWidth(toastWidth - 2 * TOAST_PADDING, Unit.PX);
		int distAVBottomKeyboardTop = (int) (app.getHeight() - bottom
				- ((AppW) app).getAppletFrame().getKeyboardHeight());
		setPopupPosition(left, distAVBottomKeyboardTop >= getOffsetHeight()
				? bottom : top - getOffsetHeight());
		Scheduler.get().scheduleDeferred(() -> addStyleName("fadeIn"));
	}

	@Override
	public void hide() {
		removeStyleName("fadeIn");
	}
}
