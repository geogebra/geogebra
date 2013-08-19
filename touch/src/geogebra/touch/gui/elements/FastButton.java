package geogebra.touch.gui.elements;

import geogebra.touch.gui.algebra.events.FastClickEvent;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.algebra.events.HasFastClickHandlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PushButton;

public abstract class FastButton extends PushButton implements HasFastClickHandlers {

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
				handleFastClick();
			}
			break;
		}

		case Event.ONTOUCHEND: {
			event.stopPropagation();
			this.touchEventHandled = true;
			handleFastClick();
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

	private void fireFastClickEvent(boolean isDoubleClick) {
		fireEvent(new FastClickEvent(isDoubleClick));
	}
	
	private void handleFastClick() {
		if (System.currentTimeMillis() - this.lastClick < TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK) {
			// doubleClick
			this.fireFastClickEvent(true);
		} else {
			this.fireFastClickEvent(false);
		}
		this.active = true;
		this.lastClick = System.currentTimeMillis();
	}

	@Override
	public HandlerRegistration addFastClickHandler(FastClickHandler handler) {
		return addHandler(handler, FastClickEvent.getType());
	}
}
