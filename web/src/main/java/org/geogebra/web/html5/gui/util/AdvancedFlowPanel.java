package org.geogebra.web.html5.gui.util;

import java.util.Iterator;

import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.HasAllKeyHandlers;
import org.gwtproject.event.dom.client.HasAllMouseHandlers;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.event.dom.client.KeyPressHandler;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseOutHandler;
import org.gwtproject.event.dom.client.MouseOverHandler;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.dom.client.MouseWheelHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.FocusPanel;
import org.gwtproject.user.client.ui.HasWidgets;
import org.gwtproject.user.client.ui.Widget;

/**
 * Focusable flowpanel.
 */
public class AdvancedFlowPanel extends Composite implements HasWidgets,
		HasAllKeyHandlers, HasAllMouseHandlers {
	protected FlowPanel mainPanel;
	protected FocusPanel focusPanel;

	/**
	 * New flow panel with focus handler.
	 */
	public AdvancedFlowPanel() {
		mainPanel = new FlowPanel();
		focusPanel = new FocusPanel(mainPanel);
		initWidget(focusPanel);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return focusPanel.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return focusPanel.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return focusPanel.addKeyPressHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return focusPanel.addMouseDownHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return focusPanel.addMouseUpHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return focusPanel.addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return focusPanel.addMouseOverHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return focusPanel.addMouseMoveHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return focusPanel.addMouseWheelHandler(handler);

	}

	/**
	 * @param handler
	 *            blur handler
	 * @return registration
	 */
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return focusPanel.addBlurHandler(handler);
	}

	/**
	 * @param handler
	 *            focus handler
	 * @return registration
	 */
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return focusPanel.addFocusHandler(handler);
	}

	@Override
	public void add(Widget w) {
		mainPanel.add(w);
	}

	@Override
	public void clear() {
		mainPanel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return mainPanel.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return mainPanel.remove(w);
	}

	@Override
	public void addStyleName(String style) {
		mainPanel.addStyleName(style);
	}

	/**
	 * Sets attribute
	 *
	 * @param attribute
	 *            key.
	 * @param value
	 *            value.
	 */
	public void setAttribute(String attribute, String value) {
		getElement().setAttribute(attribute, value);
	}

	/**
	 * Make panel focused.
	 */
	public void requestFocus() {
		getElement().focus();
	}
}
