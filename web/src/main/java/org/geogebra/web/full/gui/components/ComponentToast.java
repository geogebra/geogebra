package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
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
		super(app.getPanel(), app);
		addStyleName("toast");
		buildGUI(contentStr);
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
		getRootPanel().add(this);
		addStyleName("fadeIn");
		int toastWidth = app.isPortrait() ? width - 16 : width;
		getElement().getStyle().setWidth(toastWidth - 2 * TOAST_PADDING, Unit.PX);
		int distAVBottomKeyboardTop = (int) (app.getHeight() - bottom
				- ((AppW) app).getAppletFrame().getKeyboardHeight());
		setPopupPosition(left, distAVBottomKeyboardTop >= getOffsetHeight()
				? bottom : top - getOffsetHeight());
	}

	@Override
	public void hide() {
		removeStyleName("fadeIn");
	}
}
