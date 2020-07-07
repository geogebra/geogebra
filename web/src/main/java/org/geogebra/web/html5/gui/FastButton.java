package org.geogebra.web.html5.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.GCustomButton;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * 
 * GWT Implementation influenced by Google's FastPressElement:
 * 
 * <a href=
 * "https://developers.google.com/mobile/articles/fast_buttons">FastButtons</a>
 * 
 * Using Code examples and comments from:
 * 
 * <a href=
 * "http://stackoverflow.com/questions/9596807/converting-gwt-click-events-to-touch-events">Converting
 * GWT ClickEvents to TouchEvents</a>
 * 
 * The FastButton is used to avoid the 300ms delay on mobile devices (Only do
 * this if you want to ignore the possibility of a double tap - The browser
 * waits to see if we actually want to double top)
 * 
 * The "press" event will occur significantly fast (around 300ms faster).
 * However the biggest improvement is from enabling fast consecutive touches.
 * 
 * If you try to rapidly touch one or more FastButtons, you will notice a MUCH
 * great improvement.
 * 
 * NOTE: Different browsers will handle quick swipe or long hold/drag touches
 * differently. This is an edge case if the user is long pressing or pressing
 * while dragging the finger slightly (but staying on the element) - The browser
 * may or may not fire the event. However, the browser will always fire the
 * regular tap/press very quickly.
 * 
 * 
 * @author ashton with changes from Matthias Meisinger
 * 
 */
public abstract class FastButton extends GCustomButton {

	// in case the same touch reaches different Buttons (f.e. TouchStart +
	// TouchEnd open the StyleBar and MouseUp reaches the first Button on the
	// StyleBar

	private boolean touchMoved = false;
	private int touchId;
	private final List<FastClickHandler> handlers;

	/**
	 * New fast button
	 */
	public FastButton() {
		setStyleName("button");
		// Sink Click and Key Events; touch singed in superclass
		// I am not going to sink Mouse events since
		// I don't think we will gain anything

		sinkEvents(Event.ONCLICK | Event.KEYEVENTS); // Event.TOUCHEVENTS
																			// adds
		                                               // all (Start, End,
		                                               // Cancel, Change)

		this.handlers = new ArrayList<>();
	}

	/**
	 * Use this method in the same way you would use addClickHandler or
	 * addDomHandler
	 * 
	 * @param handler
	 *            handler
	 * 
	 */
	public void addFastClickHandler(FastClickHandler handler) {
		this.handlers.add(handler);
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (!this.isEnabled()) {
			event.stopPropagation();
			return;
		}
		
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			removeStyleName("keyboardFocus");
			onTouchStart(event);
			event.stopPropagation();
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			event.stopPropagation();
			break;
		}
		case Event.ONTOUCHMOVE: {
			onTouchMove(event);
			event.stopPropagation();
			break;
		}
		case Event.ONMOUSEUP: {
			onClick(event);
			event.stopPropagation();
			break;
		}
		case Event.ONMOUSEDOWN: {
			removeStyleName("keyboardFocus");
			event.stopPropagation();
			break;
		}

		case Event.ONKEYDOWN:
			char keyCode = (char) event.getKeyCode();
			if (keyCode == ' ') {
				onClick(event);
			}
			break;
		case Event.ONKEYPRESS:
			keyCode = (char) event.getKeyCode();
			if (keyCode == '\r' || keyCode == '\n') {
				onClick(event);
			}
			break;

		default: {
			// Let parent handle event if not one of the above (?)
			try {
				super.onBrowserEvent(event);
			} catch (Throwable t) {
				Log.debug(DOM.eventGetType(event) + "event failed");
			}
		}
		}
	}

	private void onClick(Event event) {
		event.stopPropagation();
		event.preventDefault();

		if (!CancelEventTimer.cancelMouseEvent()) {
			// Press not handled yet
			fireFastClickEvent(event.getType());
		}

		super.onBrowserEvent(event);
	}

	private void onTouchStart(Event event) {
		// Stop the event from bubbling up
		event.stopPropagation();

		// Only handle if we have exactly one touch
		if (event.getTargetTouches().length() == 1) {
			Touch start = event.getTargetTouches().get(0);
			this.touchId = start.getIdentifier();
			this.touchMoved = false;
		}

	}

	/**
	 * Check to see if the touch has moved off of the element.
	 * 
	 * NOTE that in iOS the elasticScroll may make the touch/move cancel more
	 * difficult.
	 * 
	 * @param event
	 *            touch move event
	 */
	private void onTouchMove(Event event) {

		if (!this.touchMoved) {
			Touch move = null;

			for (int i = 0; i < event.getChangedTouches().length(); i++) {
				if (event.getChangedTouches().get(i).getIdentifier() == this.touchId) {
					move = event.getChangedTouches().get(i);
				}
			}

			// Check to see if we moved off of the original element

			// Use Page coordinates since we compare with widget's absolute
			// coordinates
			if (move != null) {
				int yCord = move.getPageY();
				int xCord = move.getPageX();

				// is y above element
				boolean yTop = this.getAbsoluteTop() > yCord;
				boolean yBottom = (this.getAbsoluteTop() + this
				        .getOffsetHeight()) < yCord; // y below

				// is x to the left of element
				boolean xLeft = this.getAbsoluteLeft() > xCord;
				boolean xRight = (this.getAbsoluteLeft() + this
				        .getOffsetWidth()) < xCord; // x to the right

				this.touchMoved = yTop || yBottom || xLeft || xRight;
			}
		}
	}

	private void onTouchEnd(Event event) {
		CancelEventTimer.touchEventOccured();
		if (!this.touchMoved) {
			fireFastClickEvent(event.getType());
			event.preventDefault();
		}
	}

	/**
	 * Notify all handlers
	 */
	private void fireFastClickEvent(String eventType) {
		for (FastClickHandler h : this.handlers) {
			if (h instanceof FastClickHandler.Typed) {
				((FastClickHandler.Typed) h).onClick(eventType);
			} else {
				h.onClick(this);
			}
		}
	}
}
