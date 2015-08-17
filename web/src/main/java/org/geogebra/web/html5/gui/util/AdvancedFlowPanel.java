package org.geogebra.web.html5.gui.util;

import java.util.Iterator;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedFlowPanel extends Composite implements HasWidgets,
		HasAllKeyHandlers, HasAllMouseHandlers {
	private FlowPanel mainPanel;
	private FocusPanel focusPanel;

	public AdvancedFlowPanel() {
		mainPanel = new FlowPanel();
		focusPanel = new FocusPanel(mainPanel);
		initWidget(focusPanel);
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return focusPanel.addKeyUpHandler(handler);
	}

	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return focusPanel.addKeyDownHandler(handler);
	}

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return focusPanel.addKeyPressHandler(handler);
	}

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return focusPanel.addMouseDownHandler(handler);
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return focusPanel.addMouseUpHandler(handler);
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return focusPanel.addMouseOutHandler(handler);
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return focusPanel.addMouseOverHandler(handler);
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return focusPanel.addMouseMoveHandler(handler);
	}

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return focusPanel.addMouseWheelHandler(handler);

	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return focusPanel.addBlurHandler(handler);
	}

	public void add(Widget w) {
		mainPanel.add(w);
	}

	public void clear() {
		mainPanel.clear();
	}

	public Iterator<Widget> iterator() {
		return mainPanel.iterator();
	}

	public boolean remove(Widget w) {
		return mainPanel.remove(w);
	}

	@Override
	public void addStyleName(String style) {
		mainPanel.addStyleName(style);
	}
}
