package geogebra.touch.gui.algebra;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.elements.FastButton;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * @see FastButton (uses the same kind of eventHandling)
 */
class RadioButtonTreeItemT extends RadioButtonTreeItem {

	/**
	 * time (in ms) between two clickEvents to create a doubleClick
	 */
	private static final long TIME_BETWEEN_CLICKS = 500;
	private final TouchController controller;
	private boolean touchHandled, clickHandled, touchMoved;
	private int touchId;
	private long lastEvent = -1;

	RadioButtonTreeItemT(GeoElement ge, SafeUri showUrl, SafeUri hiddenUrl,
			MouseDownHandler mdh, TouchController controller) {

		super(ge, showUrl, hiddenUrl, mdh);
		sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
		this.controller = controller;
		updateOnNextRepaint();
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			touchStart(event);
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			break;
		}
		case Event.ONTOUCHMOVE: {
			touchMove(event);
			break;
		}
		case Event.ONCLICK: {
			click(event);
			break;
		}
		default: {
			// Let parent handle event if not one of the above (?)
			super.onBrowserEvent(event);
		}
		}
	}

	private void click(Event event) {
		event.stopPropagation();
		event.preventDefault();

		if (this.touchHandled) {
			// if the touch is already handled, we are on a device that supports
			// touch (so you aren't in the desktop browser)

			this.touchHandled = false; // reset for next press
			this.clickHandled = true; // ignore future ClickEvents
		} else if (!this.clickHandled) {
			// Press not handled yet
			handleClick();
		}

		super.onBrowserEvent(event);
	}

	private void touchStart(Event event) {

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
	 */
	private void touchMove(Event event) {

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

				if (yTop || yBottom || xLeft || xRight) {
					this.touchMoved = true;
				}
			}

		}

	}

	private void onTouchEnd(Event event) {
		if (!this.touchMoved) {
			this.touchHandled = true;
			handleClick();
			event.preventDefault();
		}
	}

	@Override
	public void onClick(ClickEvent evt) {
		// don't do anything
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		// don't do anything
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		// don't do anything
	}

	private void openRedefine() {
		this.controller.redefine(this.getGeo());
	}

	private void handleClick() {
		long currentTime = System.currentTimeMillis();

		if (currentTime - this.lastEvent < TIME_BETWEEN_CLICKS) {
			openRedefine();
		} else {
			final Hits hits = new Hits();
			hits.add(this.getGeo());
			this.controller.handleEvent(hits);
		}
		this.lastEvent = currentTime;
	}
}
