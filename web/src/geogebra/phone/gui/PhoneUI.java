package geogebra.phone.gui;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.container.ViewContainer;
import geogebra.phone.gui.container.header.Header;
import geogebra.phone.gui.container.header.simple.SimpleHeader;
import geogebra.phone.gui.container.panel.Panel;
import geogebra.phone.gui.container.panel.swipe.ViewPanelContainer;
import geogebra.phone.gui.event.EventUtil;
import geogebra.phone.gui.event.ViewChangeEvent;
import geogebra.phone.gui.event.ViewChangeHandler;
import geogebra.phone.gui.view.View;

import java.util.ArrayList;
import java.util.List;

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

	private View activeView;

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

	public void addView(View view) {
		header.addView(view);
		panel.addView(view);
	}

	public void removeView(View view) {
		header.removeView(view);
		panel.removeView(view);
	}

	public void showView(View view) {
		header.showView(view);
		panel.showView(view);
		activeView = view;
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
		showView(activeView);
	}

}
