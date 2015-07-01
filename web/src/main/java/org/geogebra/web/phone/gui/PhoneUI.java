package org.geogebra.web.phone.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.container.ViewContainer;
import org.geogebra.web.phone.gui.container.header.Header;
import org.geogebra.web.phone.gui.container.header.simple.SimpleHeader;
import org.geogebra.web.phone.gui.container.panel.Panel;
import org.geogebra.web.phone.gui.container.panel.swipe.ViewPanelContainer;
import org.geogebra.web.phone.gui.event.EventUtil;
import org.geogebra.web.phone.gui.event.ViewChangeEvent;
import org.geogebra.web.phone.gui.event.ViewChangeHandler;
import org.geogebra.web.phone.gui.view.AbstractView;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PhoneUI extends VerticalPanel implements ViewContainer,
        ViewChangeHandler, ResizeHandler {

	private AppW app;

	private Header header;
	private Panel panel;

	private List<ResizeListener> resizeListeners;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public PhoneUI(AppW app) {
		this.app = app;

		setStyleName("PhoneGUI");
		setPixelSize(Window.getClientWidth(), Window.getClientHeight());

		resizeListeners = new ArrayList<ResizeListener>();

		createHeader();
		createPanel();

		Window.addResizeHandler(this);

		EventUtil.addViewChangeHandler(ViewChangeEvent.getType(), this);
	}

	private void createHeader() {
		header = new SimpleHeader(app);
		add(header);
		resizeListeners.add(header);
	}

	private void createPanel() {
		panel = new ViewPanelContainer();
		add(panel);
		resizeListeners.add(panel);
	}

	public void addView(AbstractView view) {
		header.addView(view);
		panel.addView(view);
	}

	public void removeView(AbstractView view) {
		header.removeView(view);
		panel.removeView(view);
	}

	public void showView(AbstractView view) {
		header.showView(view);
		panel.showView(view);
	}

	public void onViewChange(ViewChangeEvent event) {
		showView(event.getView());
	}

	public void onResize(ResizeEvent event) {
		onResize();
	}

	public void onResize() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		for (final ResizeListener res : resizeListeners) {
			res.onResize();
		}
		panel.updateAfterResize();
	}

}
