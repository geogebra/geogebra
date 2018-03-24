package org.geogebra.web.full.cas.view;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Widget representing the CAS View
 *
 */
public class CASComponentW extends ScrollPanel implements ScrollHandler,
        NativePreviewHandler {

	private boolean scrollHappened;

	/**
	 * New CAS component
	 */
	public CASComponentW() {
		this.getElement().setClassName("casView");
		addScrollHandler(this);
		Event.addNativePreviewHandler(this);
	}

	@Override
	public void onScroll(ScrollEvent event) {
		scrollHappened = true;
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		EventTarget target = event.getNativeEvent().getEventTarget();
		if (!Element.is(target)) {
			return;
		}
		Element element = Element.as(target);
		if (this.getElement().isOrHasChild(element)
				&& event.getTypeInt() == Event.ONTOUCHEND && scrollHappened) {
					event.cancel();
					scrollHappened = false;
		}
	}

}
