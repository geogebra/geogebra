package org.geogebra.web.html5.euclidian;

import com.google.gwt.dom.client.CanvasElement;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.richtext.Editor;
import org.geogebra.web.richtext.impl.CarotaEditor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of the inline text controller.
 */
public class InlineTextControllerW implements InlineTextController {

	private static final String INVISIBLE = "invisible";
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
		widget.addStyleName(INVISIBLE);
		style = widget.getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		parent.appendChild(editor.getWidget().getElement());

		updateContent();
		editor.addListener(new Editor.EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				if (!content.equals(geo.getContent())) {
					geo.setContent(content);
					geo.getKernel().storeUndoInfo();
				}
			}

			@Override
			public void onSizeChanged(int minHeight) {
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
	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	@Override
	public void toBackground() {
		editor.deselect();
		editor.getWidget().addStyleName(INVISIBLE);
		geo.updateRepaint();
	}

	@Override
	public void toForeground(int x, int y) {
		editor.getWidget().removeStyleName(INVISIBLE);
		editor.focus(x - editor.getWidget().getAbsoluteLeft(),
				y - editor.getWidget().getAbsoluteTop() + view.getAbsoluteTop());
	}

	@Override
	public void format(String key, Object val) {
		editor.format(key, val);
		geo.setContent(editor.getContent());
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		if (editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			return editor.getDocumentFormat(key, fallback);
		} else {
			return editor.getFormat(key, fallback);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		GPoint2D origin = geo.getLocation();
		int x = view.toScreenCoordX(origin.getX());
		int y = view.toScreenCoordY(origin.getY());
		g2.saveTransform();

		GAffineTransform transform = AwtFactory.getPrototype().newAffineTransform();
		transform.translate(x, y);
		transform.rotate(geo.getAngle());
		g2.transform(transform);

		if (geo.getBackgroundColor() != null) {
			g2.setPaint(geo.getBackgroundColor());
			g2.fillRect(0, 0, (int) geo.getWidth(), (int) geo.getHeight());
		}
		if (editor.getWidget().getElement().hasClassName(INVISIBLE)) {
			CanvasElement canvas = editor.getCanvasElement();
			if (canvas.getWidth() > 0 && canvas.getHeight() > 0) {
				((GGraphics2DW) g2).drawImage(canvas,
						DrawInlineText.PADDING, DrawInlineText.PADDING);
			}
		}

		g2.restoreTransform();
	}
}
