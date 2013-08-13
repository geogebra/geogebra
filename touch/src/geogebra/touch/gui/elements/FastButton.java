package geogebra.touch.gui.elements;

import geogebra.touch.gui.algebra.events.FastClickEvent;
import geogebra.touch.gui.algebra.events.FastClickHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PushButton;

public class FastButton extends PushButton implements FastClickHandler {

	public static final int TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK = 500;

	private boolean active;
	private boolean touchEventHandled = false;

	private long lastClick = -1;

	public FastButton() {
		this.setStyleName("button");
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see <a href =
	 *      https://developers.google.com/mobile/articles/fast_buttons>Fast
	 *      Buttons </a>
	 */
	@Override
	public void onBrowserEvent(Event event) {

		switch (DOM.eventGetType(event)) {

		case Event.ONCLICK: {
			event.stopPropagation();
			if (this.touchEventHandled) {
				this.touchEventHandled = false;
			} else {
				fireFastClickEvent();
			}
			break;
		}

		case Event.ONTOUCHEND: {
			event.stopPropagation();
			this.touchEventHandled = true;
			fireFastClickEvent();
			break;
		}

		case Event.ONTOUCHSTART: {
			event.stopPropagation();
			break;
		}

		case Event.ONTOUCHMOVE: {
			break;
		}
		default: {
			super.onBrowserEvent(event);
		}
		}
	}

	private void fireFastClickEvent() {
		fireEvent(new FastClickEvent());
	}

	@Override
	public void onFastClick(FastClickEvent event) {

		if (System.currentTimeMillis() - FastButton.this.lastClick < TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK) {
			// doubleClick
			FastButton.this.onDoubleClick();
		} else {
			// first click or single click
			FastButton.this.onClick();
		}
		FastButton.this.lastClick = System.currentTimeMillis();
	}

	@Override
	protected void onClick() {
		this.active = true;
	}

	protected void onDoubleClick() {
		this.active = true;
		// TODO Auto-generated method stub

	}

}
