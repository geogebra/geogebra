package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.geos.GProperty;
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
	private EuclidianView view;

	/**
	 * @param geo
	 *            text
	 * @param parent
	 *            parent div
	 */
	public InlineTextControllerW(GeoInlineText geo, Element parent,
			EuclidianView view) {
		this.geo = geo;
		this.parent = parent;
		this.view = view;
	}

	@Override
	public void create() {
		editor = new CarotaEditor(view.getFontSize());
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
			public void onSizeChanged(double minHeight) {
				geo.setHeight(Math.max(minHeight, geo.getHeight()));
				geo.setMinHeight(minHeight);
				geo.updateRepaint();
			}

			@Override
			public void onSelectionChanged() {
				geo.getKernel().notifyUpdateVisualStyle(geo, GProperty.FONT);
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

	@Override
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
		editor.deselect();
		editor.getWidget().addStyleName("background");
		geo.updateRepaint();
	}

	@Override
	public void toForeground(int x, int y) {
		editor.getWidget().removeStyleName("background");
		editor.focus(x - editor.getWidget().getAbsoluteLeft(),
				y - editor.getWidget().getAbsoluteTop() + view.getAbsoluteTop());
	}

	@Override
	public void format(String key, Object val) {
		editor.format(key, val);
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return editor.getFormat(key, fallback);
	}
}
