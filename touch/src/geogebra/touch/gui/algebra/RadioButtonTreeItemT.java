package geogebra.touch.gui.algebra;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.events.FastClickEvent;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.algebra.events.HasFastClickHandlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class RadioButtonTreeItemT extends RadioButtonTreeItem implements
		HasFastClickHandlers {

	private static final int TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK = 500;
	private final TouchController controller;
	private long lastClick = -1;
	private boolean touchEventHandled = false;

	public RadioButtonTreeItemT(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl, MouseDownHandler mdh, TouchController controller) {

		super(ge, showUrl, hiddenUrl, mdh);
		sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
		this.controller = controller;

		this.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onSingleClick() {
				handleClick();
			}
			
			@Override
			public void onDoubleClick() {
				openRedefine();
			}
		});

	}

	protected void openRedefine() {
		this.controller.redefine(this.getGeo());
	}
	protected void handleClick() {
		final Hits hits = new Hits();
		hits.add(this.getGeo());
		this.controller.handleEvent(hits);
	}

	@Override
	public HandlerRegistration addFastClickHandler(FastClickHandler handler) {
		return addHandler(handler, FastClickEvent.getType());
	}

	/**
	 * @see https://developers.google.com/mobile/articles/fast_buttons
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

	private void handleFastClick() {
		if (System.currentTimeMillis() - this.lastClick < TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK) {
			// doubleClick
			this.fireFastClickEvent(true);
		} else {
			// first click or single click
			this.fireFastClickEvent(false);
		}
		this.lastClick = System.currentTimeMillis();
	}
	
	private void fireFastClickEvent(boolean isDoubleClick) {
		fireEvent(new FastClickEvent(isDoubleClick));
	}

	@Override
	public void onClick(ClickEvent evt) {
		// done with FastClickHandler
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		// done with FastClickHandler
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		// don't do anything
	}
}
