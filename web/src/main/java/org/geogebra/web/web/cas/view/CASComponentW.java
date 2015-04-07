package org.geogebra.web.web.cas.view;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CASComponentW extends ScrollPanel implements ScrollHandler,
        NativePreviewHandler {

	private boolean scrollHappened;

	public CASComponentW() {
		this.getElement().setClassName("casView");
		addScrollHandler(this);
		Event.addNativePreviewHandler(this);
	}

	public void onScroll(ScrollEvent event) {
		scrollHappened = true;
	}

	public void onPreviewNativeEvent(NativePreviewEvent event) {
		Element element = Element.as(event.getNativeEvent().getEventTarget());
		if (this.getElement().isOrHasChild(element)) {
			if (event.getTypeInt() == Event.ONTOUCHEND) {
				if (scrollHappened) {
					event.cancel();
					scrollHappened = false;
				}
			}
		}
	}

}
