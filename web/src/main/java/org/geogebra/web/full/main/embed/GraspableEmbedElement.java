package org.geogebra.web.full.main.embed;

import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.ScriptManagerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Embedded element GM element for Notes
 */
public class GraspableEmbedElement extends EmbedElement {

	private JavaScriptObject api;
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

    private native void setContentNative(JavaScriptObject canvas,
			String string) /*-{
		canvas.loadFromJSON(string);
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

    private static native boolean isGraspableMathLoaded() /*-{
		return !!$wnd.gmath;
	}-*/;

    native void initCanvas(int id) /*-{
		var that = this;
		var apiObject = that.@org.geogebra.web.full.main.embed.GraspableEmbedElement::getApi()();
		var canvas = new $wnd.gmath.Canvas('#gm-div' + id, {
			ggbNotesAPI : apiObject
		});

		var storeContent = function() {
			that.@org.geogebra.web.full.main.embed.GraspableEmbedElement::createUndoAction(I)(id);
		};

		canvas.controller.on('undoable-action', storeContent);
		that.@org.geogebra.web.full.main.embed.GraspableEmbedElement::setAPI(*)(canvas);
	}-*/;

    @ExternalAccess
    private void createUndoAction(int id) {
        embedManager.createUndoAction(id);
    }

    @ExternalAccess
    private JavaScriptObject getApi() {
        ScriptManagerW scriptManager = embedManager.getScriptManager();
        return scriptManager.getApi();
    }

	@Override
	public void setContent(String string) {
		if (api != null) {
			setContentNative(api, string);
		} else {
			content = string;
		}
	}

	@Override
    public void addListeners(final int embedID) {
        if (isGraspableMathLoaded()) {
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
			undoNative(api);
		} else if (action == EventType.REDO) {
			redoNative(api);
		}
	}

    private native void undoNative(JavaScriptObject canvas) /*-{
		canvas.controller.undo();
	}-*/;

    private native void redoNative(JavaScriptObject canvas) /*-{
		canvas.controller.redo();
	}-*/;

    @Override
    public String getContentSync() {
        return getContentByCanvas(api);
    }

    private native String getContentByCanvas(JavaScriptObject canvas) /*-{
		return canvas.toJSON();
	}-*/;
}
