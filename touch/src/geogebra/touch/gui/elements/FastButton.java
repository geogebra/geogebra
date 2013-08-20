package geogebra.touch.gui.elements;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PushButton;

public class FastButton extends PushButton {
	private boolean touchHandled = false;
	private boolean clickHandled = false;
	private boolean touchMoved = false;
	private int startY;
	private int startX;

	private boolean isActive;

	public FastButton() {
		this.setStyleName("button");
		sinkEvents(Event.TOUCHEVENTS | Event.ONCLICK);
	}

	@Override
	public void onBrowserEvent(Event event) {

		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			onTouchStart(event);
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			break;
		}
		case Event.ONTOUCHMOVE: {
			onTouchMove(event);
			break;
		}
		case Event.ONCLICK: {
			onClick(event);
			return;
		}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * 
	 * @param event
	 */
	private void onClick(Event event) {
		event.stopPropagation();

		if (this.touchHandled) {
			this.touchHandled = false;
			this.clickHandled = true;
			super.onBrowserEvent(event);
		} else {
			if (this.clickHandled) {

				event.preventDefault();
			} else {
				this.clickHandled = false;
				super.onBrowserEvent(event);
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchEnd(Event event) {
		if (!this.touchMoved) {
			this.touchHandled = true;
			fireClick();
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchMove(Event event) {
		if (!this.touchMoved) {
			Touch touch = event.getTouches().get(0);
			int deltaX = Math.abs(this.startX - touch.getClientX());
			int deltaY = Math.abs(this.startY - touch.getClientY());

			if (deltaX > 5 || deltaY > 5) {
				this.touchMoved = true;
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchStart(Event event) {
		Touch touch = event.getTouches().get(0);
		this.startX = touch.getClientX();
		this.startY = touch.getClientY();
		this.touchMoved = false;
	}

	/**
	 * @param executor
	 * @return
	 */
	private void fireClick() {
		setActive(true);
		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
				false, false, false);
		getElement().dispatchEvent(evt);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
