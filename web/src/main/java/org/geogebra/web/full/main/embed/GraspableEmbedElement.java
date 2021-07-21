package org.geogebra.web.full.main.embed;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.Browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Embedded element GM element for Notes
 */
public class GraspableEmbedElement extends EmbedElement {

	private GMCanvas api;
	private String content;
	private EmbedManagerW embedManager;

	/**
	 * @param widget
	 *            UI widget for the iframe
	 * @param embedManager
	 *            embed manager
	 */
	public GraspableEmbedElement(Widget widget,
			final EmbedManagerW embedManager) {
		super(widget);
		this.embedManager = embedManager;
	}

	/**
	 * Set API, execute waiting actions
	 * 
	 * @param core
	 *            core API
	 */
	protected void setAPI(GMCanvas core) {
		this.api = core;
		if (content != null) {
			api.loadFromJSON(content);
			content = null;
		}
	}

	void initCanvas(int id) {
		Object ggbApi = embedManager.getScriptManager().getApi();
		GMCanvas canvas = new GMCanvas("#gm-div" + id,
				JsPropertyMap.of("ggbNotesAPI", ggbApi));

		canvas.controller.on("undoable-action", () -> {
			embedManager.createUndoAction(id);
			GeoElement embed = embedManager.findById(id);
			if (embed != null) {
				embed.notifyUpdate();
			}
		});

		setAPI(canvas);
	}

	@Override
	public void setContent(String string) {
		if (api != null) {
			api.loadFromJSON(string);
		} else {
			content = string;
		}
	}

	@Override
	public void addListeners(final int embedID) {
		if (GMApi.get() != null) {
			initCanvas(embedID);
			return;
		}
		GMLoader.INSTANCE.load(this, embedID);
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
			api.controller.undo();
		} else if (action == EventType.REDO) {
			api.controller.redo();
		}
	}

	@Override
	public JavaScriptObject getApi() {
		return Js.cast(api);
	}

	@Override
	public String getContentSync() {
		return api.toJSON();
	}
}
