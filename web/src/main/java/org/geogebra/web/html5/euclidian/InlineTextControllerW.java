package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.web.richtext.Editor;
import org.geogebra.web.richtext.impl.CarotaEditor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of the inline text controller.
 */
public class InlineTextControllerW implements InlineTextController {

	private GeoInlineText geo;

	private Element parent;
	private Editor editor;
	private Style style;

	/**
	 * @param geo
	 *            text
	 * @param parent
	 *            parent div
	 */
	public InlineTextControllerW(GeoInlineText geo, Element parent) {
		this.geo = geo;
		this.parent = parent;
	}

	@Override
	public void create() {
		editor = new CarotaEditor();

		final Widget widget = editor.getWidget();
		style = widget.getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		parent.appendChild(editor.getWidget().getElement());

		updateContent();
		editor.addListener(new Editor.EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				geo.setContent(content);
				geo.getKernel().storeUndoInfo();
			}

			@Override
			public void onSizeChanged() {
				geo.setWidth(Math.max(widget.getOffsetWidth(), geo.getWidth()));
				geo.setHeight(Math.max(widget.getOffsetHeight(), geo.getHeight()));
				geo.updateRepaint();
			}
		});
	}

	@Override
	public void discard() {
		editor.getWidget().getElement().removeFromParent();
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	/**
	 * Set content from geo
	 */
	public void updateContent() {
		if (geo.getContent() != null) {
			editor.setContent(geo.getContent());
		}
	}

	@Override
	public void setWidth(int width) {
		style.setWidth(width, Style.Unit.PX);
	}

	@Override
	public void setHeight(int height) {
		style.setProperty("minHeight", height, Style.Unit.PX);
	}

	@Override
	public void toBackground() {
		editor.getWidget().addStyleName("background");
	}

	@Override
	public void toForeground() {
		editor.getWidget().removeStyleName("background");
		editor.focus();
	}
}
