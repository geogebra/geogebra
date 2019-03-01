package org.geogebra.web.full.main.embed;

import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.Browser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class GraspableEmbedElement extends EmbedElement {

	public GraspableEmbedElement(Widget widget) {
		super(widget);
	}

	private native void setContentNative(Element element, String string) /*-{
		$wnd.setTimeout(function() {
			element.contentWindow.postMessage({
				command : 'loadFromJSON',
				gmm_id : 3,
				args : {
					json : string
				}
			}, 'https://graspablemath.com');
		}, 5000);

	}-*/;

	private native void addListeners(Element element, int id,
			EmbedManagerW manager) /*-{
		$wnd.setTimeout(function() {
			element.contentWindow.postMessage({
				command : 'listen',
				gmm_id : 1,
				eventType : 'undoable-action'
			}, 'https://graspablemath.com');
		}, 5000);

		window
				.addEventListener(
						'message',
						function(msg) {
							if (msg.data && msg.data.is_event) {
								element.contentWindow.postMessage({
									command : 'getAsJSON',
									gmm_id : 2
								}, 'https://graspablemath.com');
							} else {
								$wnd.console.log(msg);
								if (msg.data && msg.data.gmm_id == 2) {
									$wnd.console.log("store", msg.data.result);
									manager.@org.geogebra.web.full.main.EmbedManagerW::storeContent(ILjava/lang/String;)(id,msg.data.result);
								}
							}
						});
	}-*/;

	@Override
	public void setContent(String string) {
		setContentNative(getElement(), string);
	}

	@Override
	public void addListeners(int embedID, EmbedManagerW embedManagerW) {
		addListeners(getElement(), embedID, embedManagerW);
	}

	@Override
	public void setSize(int contentWidth, int contentHeight) {
		getElement().getStyle().setWidth(contentWidth - 2, Unit.PX);
		getElement().getStyle().setHeight(contentHeight - 2, Unit.PX);
		Browser.scale(getElement(),
				getGreatParent().getElement().getOffsetWidth()
						/ (double) contentWidth,
				0, 0);
	}

}
