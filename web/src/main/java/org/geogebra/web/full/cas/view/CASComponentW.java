package org.geogebra.web.full.cas.view;

import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.EventTarget;
import org.gwtproject.event.dom.client.ScrollEvent;
import org.gwtproject.event.dom.client.ScrollHandler;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.Event.NativePreviewEvent;
import org.gwtproject.user.client.Event.NativePreviewHandler;
import org.gwtproject.user.client.ui.ScrollPanel;

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
