package org.geogebra.web.full.main.embed;

import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Embedded element GM element for Notes
 */
public class GraspableEmbedElement extends EmbedElement {

	private JavaScriptObject api;
	private String content;

	/**
	 * @param widget
	 *            UI widget for the iframe
	 */
	public GraspableEmbedElement(Widget widget) {
		super(widget);
	}

	private native void setContentNative(JavaScriptObject core,
			String string) /*-{
		core.loadFromJSON(string);
	}-*/;

	/**
	 * Set API, execute waiting actions
	 * 
	 * @param core
	 *            core API
	 */
	protected void setAPI(JavaScriptObject core) {
		this.api = core;
		if (content != null) {
			setContentNative(api, content);
			content = null;
		}
	}

	/**
	 * @param element
	 *            iframe element
	 * @param id
	 *            embed ID
	 * @param manager
	 *            embed manager
	 */
	protected native void addListeners(Element element, int id,
			EmbedManagerW manager) /*-{
		var that = this;
		if ($wnd.ExternalApi) {
			var storeContent = function(core) {
				manager.@org.geogebra.web.full.main.EmbedManagerW::createUndoAction(I)(id);
				core
						.getAsJSON()
						.then(
								function(content) {
									$wnd.console.log("storing content");
									manager.@org.geogebra.web.full.main.EmbedManagerW::storeContent(ILjava/lang/String;)(id, content);
								});
			};

			var loadCallback = function(core) {
				that.@org.geogebra.web.full.main.embed.GraspableEmbedElement::setAPI(*)(core);
				core.addEventListener('undoable-action', function() {
					storeContent(core);
				});
			};

			$wnd
					.ExternalApi(element, 'https://graspablemath.com',
							loadCallback);
		}
	}-*/;

	@Override
	public void setContent(String string) {
		if (api != null) {
			setContentNative(api, string);
		} else {
			content = string;
		}
	}

	@Override
	public void addListeners(final int embedID,
			final EmbedManagerW embedManagerW) {
		ScriptElement el = Document.get().createScriptElement();
		el.setSrc(
				"https://graspablemath.com/shared/libs/gmath/gm-inject.js");
		ResourcesInjector.loadJS(el, new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				addListeners(getElement(), embedID, embedManagerW);
			}

			@Override
			public void onError() {
				Log.warn("Could not load Graspable Math API");
			}

			@Override
			public void cancel() {
				// no need to cancel
			}
		});
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

	@Override
	public void executeAction(EventType action) {
		if (action == EventType.UNDO) {
			undoNative(api);
		} else if (action == EventType.REDO) {
			redoNative(api);
		}
	}

	private native void undoNative(JavaScriptObject core) /*-{
		core.undo();
	}-*/;

	private native void redoNative(JavaScriptObject core) /*-{
		core.redo();
	}-*/;
}
